/*
 * Wikimedia event platform metrics client.
 *
 * DESCRIPTION
 *     Collects analytics events, supplements them with additional context data,
 *     evaluates them for submission, and ultimately submits them according to
 *     instructions provided via remote configuration.
 *
 * LICENSE NOTICE
 *     Copyright 2021 Wikimedia Foundation
 *
 *     Redistribution and use in source and binary forms, with or without
 *     modification, are permitted provided that the following conditions are
 *     met:
 *
 *     1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *     THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *     PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 *     CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import Foundation

/// Support macOS and Linux. See https://github.com/tensorflow/swift/issues/486#issuecomment-646083111
#if canImport(FoundationNetworking)
    import FoundationNetworking
#endif

/**
 * Wikimedia Metrics Client - Swift
 *
 * Use `MetricsClient.submit(stream, event)` to produce events.
 */
public class MetricsClient {

    /**
     * Integration layer providing access to application functionality.
     */
    let integration: MetricsClientIntegration

    /**
     * Evaluates whether streams are in-sample based on their configuration and the application state
     */
    let samplingController: SamplingController

    /**
     * Manages app analytics session state
     */
    let sessionController: SessionController

    /**
     * Stores a limited number of unvalidated events until stream configurations are available.
     *
     * The metrics client library makes an HTTP request to a remote stream configuration service for information
     * about how to evaluate incoming event data. Until this initialization is complete, we store any incoming
     * events in this buffer.
     */
    private var unvalidatedEvents: LimitedCapacityDeque<Event> = LimitedCapacityDeque(capacity: 128)

    /**
     * Holds validated events that have been scheduled for POSTing to the analytics event intake service
     */
    private var validatedEvents = EventBuffer(destination: URL(string: "https://intake-analytics.wikimedia.org/v1/events")!)

    /**
     * Holds validated error events that have been scheduled for POSTing to the error logging intake service
     */
    private var validatedErrors = EventBuffer(destination: URL(string: "https://intake-logging.wikimedia.org/v1/events")!)

    /**
     * Serial dispatch queue that enables working with properties in a thread-safe way
     */
    private let queue = DispatchQueue(label: "MetricsClient-" + UUID().uuidString)

    /**
     * Serial dispatch queue for encoding data on a background thread
     */
    private let encodeQueue = DispatchQueue(label: "MetricsClientEncode-" + UUID().uuidString, qos: .background)

    /**
     * Serialize and deserialize events and stream configs to and from JSON strings.
     */
    private let encoder = JSONEncoder()

    private let dateFormatter = ISO8601DateFormatter()

    private var streamConfigs: [String: StreamConfig]? {
        get {
            self.streamConfigsFetcher.getStreamConfigs()
        }
    }

    private var streamConfigsFetcher: StreamConfigsFetcher

    // MARK: Initializer

    init(
            integration: MetricsClientIntegration,
            sessionController: SessionController? = nil,
            samplingController: SamplingController? = nil
    ) {
        let sessionController = sessionController ?? SessionController(date: Date())
        let samplingController = samplingController ?? SamplingController(
            integration: integration,
            sessionController: sessionController
        )

        self.integration = integration
        self.sessionController = sessionController
        self.samplingController = samplingController

        self.streamConfigsFetcher = StreamConfigsFetcher(integration: integration)

        self.encoder.dateEncodingStrategy = .iso8601
        #if DEBUG
        self.encoder.outputFormatting = .prettyPrinted
        #endif
    }

    // MARK: Instance methods

    /**
     * Submit an event according to the given stream's configuration.
     * - Parameters:
     *      - stream: The stream to submit the event to
     *      - event: The event data
     *
     * An example call:
     * ```
     * struct TestEvent: EventInterface {
     *   static let schema = "/analytics/mobile_apps/test/1.0.0"
     *   let test_string: String
     *   let test_map: SourceInfo
     *   struct SourceInfo: Codable {
     *       let file: String
     *       let method: String
     *   }
     * }
     *
     * let sourceInfo = TestEvent.SourceInfo(file: "Features/Feed/ExploreViewController.swift", method: "refreshControlActivated")
     * let event = TestEvent(test_string: "Explore Feed refreshed", test_map: sourceInfo)
     *
     * MetricsClient.shared?.submit(
     *   stream: stream,
     *   event: event
     * )
     * ```
     *
     * See https://wikitech.wikimedia.org/wiki/Metrics_Platform/Swift_API
     */
    public func submit(stream: String, event: Event) {
        if !integration.loggingEnabled() {
            return
        }
        self.addEventMetadata(event: event)

        queue.async {
            if self.streamConfigs == nil {
                self.unvalidatedEvents.append(event)
                return
            }
            guard let config = self.streamConfigs?[stream] else {
                NSLog("MetricsClient: Stream '\(stream)' is not configured")
                return
            }
            if !self.samplingController.inSample(stream: stream, config: config) {
                NSLog("MetricsClient: Stream '\(stream)' is not in sample")
                return
            }
            if config.destinationEventService == DestinationEventService.analytics {
                self.validatedEvents.append(event)
            } else if config.destinationEventService == DestinationEventService.errorLogging {
                self.validatedErrors.append(event)
            } else {
                // TODO: Unknown destination service; Log a client error
            }
        }
    }

    /**
     * Sets the following top-level fields required by all mobile app analytics schemas:
     * - `dt`: timestamp reflecting when event was received by the MetricsClient library
     * - `app_install_id`: app install ID as reported by the host client
     * - `app_session_id`: app session ID
     */
    private func addEventMetadata(event: Event) {
        event.agent.appInstallId = self.integration.appInstallId()
        event.performer?.sessionId = self.sessionController.sessionId()
    }

    /**
     * Process unvalidated events upon stream configs becoming available.
     */
    private func processUnvalidatedEvents() {
        queue.sync {
            while let event = self.unvalidatedEvents.popFirst() {
                guard let config = streamConfigs?[event.meta.stream] else {
                    continue
                }
                guard samplingController.inSample(stream: event.meta.stream, config: config) else {
                    continue
                }
                if config.destinationEventService == DestinationEventService.analytics {
                    self.validatedEvents.append(event)
                } else if config.destinationEventService == DestinationEventService.errorLogging {
                    self.validatedErrors.append(event)
                } else {
                    // TODO: Unknown destination service; Log a client error
                }
            }
        }
    }

    private func postAllScheduled(_ completion: (() -> Void)? = nil) {
        encodeQueue.async {
            [self.validatedEvents, self.validatedErrors].forEach { buffer in
                NSLog("MetricsClient: Posting all scheduled events")

                var eventsToSend: [Event] = []
                while let event = buffer.popFirst() {
                    eventsToSend.append(event)
                }

                var data: Data
                do {
                    data = try self.encoder.encode(eventsToSend)
                } catch {
                    NSLog("MetricsClient: Error encoding events pending submission")
                    return
                }

                #if DEBUG
                if let jsonString = String(data: data, encoding: .utf8) {
                    NSLog("MetricsClient: Sending event with POST body:\n\(jsonString)")
                }
                #endif

                self.integration.httpPost(buffer.destination, body: data) { result in
                    switch result {
                    case .success:
                        NSLog("MetricsClient: Event submission succeeded")
                        break
                    case .failure:
                        // Re-enqueue event to be retried on next POST attempt
                        for failedEvent in eventsToSend.reversed() {
                            buffer.prepend(failedEvent)
                        }
                        NSLog("MetricsClient: Event submission failed; re-enqueuing to retry later")
                    }
                }
            }
        }
    }

    /**
     * This method is called by the application delegate in
     * `applicationWillResignActive()` and disables event logging.
     */
    public func onAppPause() {
        sessionController.touchSession()
    }

    /**
     * This method is called by the application delegate in
     * `applicationDidBecomeActive()` and re-enables event logging.
     *
     * If it has been more than 15 minutes since the app entered background state,
     * a new session is started.
     */
    public func onAppResume() {
        if sessionController.sessionExpired() {
            sessionController.beginNewSession()
        } else {
            sessionController.touchSession()
        }
    }

    /**
     * Called when user toggles logging permissions in Settings
     */
    public func resetSession() {
        sessionController.beginNewSession()
    }

    /**
     * This method is called by the application delegate in
     * `applicationWillTerminate()`
     *
     * We do not persist session ID on app close because we have decided that a
     * session ends when the user (or the OS) has closed the app or when 15
     * minutes of inactivity have passed.
     */
    public func onAppClose() {
        // Placeholder for any onTerminate logic
        /// TODO: Dump the output queue one last time? Will the async HTTP POST complete before the app terminates?
    }

}
