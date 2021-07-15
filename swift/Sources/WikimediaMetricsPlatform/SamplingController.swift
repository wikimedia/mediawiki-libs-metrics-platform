import Foundation

class SamplingController {

    private let integration: MetricsClientIntegration
    private let sessionController: SessionController

    /**
     * Cache of "in sample" / "out of sample" determination for each stream
     *
     * The process of determining only has to happen the first time an event is
     * logged to a stream for which stream configuration is available. All other
     * times `in_sample` simply returns the cached determination.
     *
     * Only cache determinations asynchronously via `queue.async`
     */
    private var samplingCache: [String: Bool] = [:]

    init(integration: MetricsClientIntegration, sessionController: SessionController) {
        self.integration = integration
        self.sessionController = sessionController
    }

    /**
     * Compute a boolean function on a random identifier
     * - Parameter stream: name of the stream
     * - Parameter config: stream configuration for the provided stream name
     * - Returns: `true` if in sample or `false` otherwise
     *
     * The determinations are lazy and cached, so each stream's in-sample vs
     * out-of-sample determination is computed only once, the first time an event
     * is logged to that stream.ß
     *
     * Refer to sampling settings section in
     * [mw:Wikimedia Product/Analytics Infrastructure/Stream configuration](https://www.mediawiki.org/wiki/Wikimedia_Product/Analytics_Infrastructure/Stream_configuration)
     * for more information.
     */
    func inSample(stream: String, config: StreamConfig) -> Bool {
        guard let rate = config.getSamplingConfig()?.rate else {
            /**
             * If stream is present in streamConfigs but doesn't have
             * sampling settings, it is always in-sample.
             */
            return true
        }

        /**
         * All platforms use session ID as the default identifier for determining
         * in- vs out-of-sample of events sent to streams. On the web, streams can
         * be set to use pageview token instead. On the apps, streams can be set
         * to use device token instead.
         */
        let sessionIdentifierType = "session"
        let deviceIdentifierType = "device"
        let identifierType = config.getSamplingConfig()?.identifier ?? sessionIdentifierType
        let appInstallID = integration.appInstallId()

        guard (identifierType == sessionIdentifierType) || (identifierType == deviceIdentifierType) else {
            NSLog("MetricsClient: Logged to stream which is not configured for sampling based on \(sessionIdentifierType) or \(deviceIdentifierType) identifier")
            return false
        }

        let identifier = identifierType == sessionIdentifierType ? sessionController.sessionId() : appInstallID
        return determine(identifier, rate)
    }

    /**
     * Yields a deterministic (not stochastic) determination of whether the
     * provided `id` is in-sample or out-of-sample according to the `acceptance`
     * rate
     * - Parameter id: identifier to use for determining sampling
     * - Parameter acceptance: the desired proportion of many `token`-s being
     *   accepted
     *
     * The algorithm works in a "widen the net on frozen fish" fashion -- tokens
     * continue evaluating to true as the acceptance rate increases. For example,
     * a device determined to be in-sample for a stream "A" having rate 0.1 will
     * be determined to be in-sample for a stream "B" having rate 0.2, and its
     * events will show up in tables "A" and "B".
     */
    func determine(_ id: String, _ acceptance: Double) -> Bool {
        guard let token = UInt32(id.prefix(8), radix: 16) else {
            return false
        }
        return (Double(token) / Double(UInt32.max)) < acceptance
    }

}
