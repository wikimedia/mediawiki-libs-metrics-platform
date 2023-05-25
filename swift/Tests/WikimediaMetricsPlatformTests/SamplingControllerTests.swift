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

        let streamConfigWithoutSamplingConfig = StreamConfig(
                schemaTitle: "test/event",
                destinationEventService: DestinationEventService.analytics
        )

        self.streamConfigs = [
            "noSamplingConfig": streamConfigWithoutSamplingConfig,
            "hasSamplingConfigWithRateZero": newMetricsPlatformClientStreamConfig(rate: 0.0),
            "hasSamplingConfigWithRateAHalf": newMetricsPlatformClientStreamConfig(rate: 0.5),
            "hasSamplingConfigWithRateOne": newMetricsPlatformClientStreamConfig(rate: 1.0),
        ]
    }
    
    private func newMetricsPlatformClientStreamConfig(rate: Double, unit: String = "session") -> StreamConfig {
        return StreamConfig(
            schemaTitle: "test/event",
            destinationEventService: DestinationEventService.analytics,
            producers: StreamConfig.Producers(
                metricsPlatformClient: StreamConfig.Producers.MetricsPlatformClient(
                    sampling: SamplingConfig(
                        rate: rate,
                        unit: unit
                    )
                )
            )
        )
    }

    func testSamplingDeterminations() {
        XCTAssertTrue(
            samplingController!.inSample(
                stream: "hasSamplingConfigWithRateOne",
                config: streamConfigs!["hasSamplingConfigWithRateOne"]!
            ),
            "Should always return true with sampling rate of 1.0"
        )

        XCTAssertFalse(
            samplingController!.inSample(
                stream: "hasSamplingConfigWithRateZero",
                config: streamConfigs!["hasSamplingConfigWithRateZero"]!
            ),
            "Should always return false with sampling rate of 0.0"
        )
    }

    func testCachesAndReusesInitialSamplingDetermination() {
        let initialSamplingDetermination = samplingController!.inSample(
            stream: "hasSamplingConfigWithRateAHalf",
            config: streamConfigs!["hasSamplingConfigWithRateAHalf"]!
        )

        for _ in 1...10 {
            XCTAssertEqual(
                samplingController!.inSample(
                    stream: "hasSamplingConfigWithRateAHalf",
                    config: streamConfigs!["hasSamplingConfigWithRateAHalf"]!
                ),
                initialSamplingDetermination
            )
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
