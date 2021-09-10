import XCTest
@testable import WikimediaMetricsPlatform

final class SamplingControllerTests: XCTestCase {

    var streamConfigs: [String: StreamConfig]?
    var samplingController: SamplingController?

    override func setUp() {
        super.setUp()

        let integration = TestMetricsClientIntegration()
        let sessionController = SessionController(date: Date())
        self.samplingController = SamplingController(integration: integration, sessionController: sessionController)

        let samplingConfig = SamplingConfig(rate: 0.5, identifier: "session")
        let clientConfig = StreamConfig.ProducerConfig.MetricsPlatformClientConfig(samplingConfig: samplingConfig)
        let producerConfig = StreamConfig.ProducerConfig(clientConfig: clientConfig)
        let streamConfigWithSamplingConfig = StreamConfig(
                stream: "test.event",
                schema: "test/event",
                destination: DestinationEventService.analytics,
                producerConfig: producerConfig
        )
        let streamConfigWithoutSamplingConfig = StreamConfig(
                stream: "test.event",
                schema: "test/event",
                destination: DestinationEventService.analytics,
                producerConfig: nil
        )
        self.streamConfigs = [
            "hasSamplingConfig": streamConfigWithSamplingConfig,
            "noSamplingConfig": streamConfigWithoutSamplingConfig
        ]
    }

    func testSamplingDeterminations() {
        let id = SessionController.generateSessionId()
        XCTAssertTrue(samplingController!.determine(id, 1.0), "Should always return true with sampling rate of 1.0")
        XCTAssertFalse(samplingController!.determine(id, 0.0), "Should always return false with sampling rate of 0.0")
    }

    func testCachesAndReusesInitialSamplingDetermination() {
        let initialSamplingDetermination = samplingController!.inSample(stream: "hasSamplingConfig", config: streamConfigs!["hasSamplingConfig"]!)
        for _ in 1...10 {
            XCTAssertEqual(samplingController!.inSample(stream: "hasSamplingConfig", config: streamConfigs!["hasSamplingConfig"]!), initialSamplingDetermination)
        }
    }

    func testAlwaysInSampleIfNoSamplingConfigIsDefined() {
        XCTAssertTrue(samplingController!.inSample(stream: "noSamplingConfig", config: streamConfigs!["noSamplingConfig"]!))
    }

    static var allTests = [
        ("testSamplingDeterminations", testSamplingDeterminations),
        ("testCachesAndReusesInitialSamplingDetermination", testCachesAndReusesInitialSamplingDetermination),
        ("testAlwaysInSampleIfNoSamplingConfigIsDefined", testAlwaysInSampleIfNoSamplingConfigIsDefined)
    ]

}
