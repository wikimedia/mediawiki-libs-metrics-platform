import XCTest
@testable import WikimediaMetricsPlatform

final class ContextControllerTests: XCTestCase {

    var streamConfig: StreamConfig?

    override func setUp() {
        super.setUp()

        let clientConfig = StreamConfig.Producers.MetricsPlatformClient(provideValues: ContextAttribute.allCases)
        let producersConfig = StreamConfig.Producers(metricsPlatformClient: clientConfig)
        self.streamConfig = StreamConfig(
            schemaTitle: "test/event",
            destinationEventService: DestinationEventService.analytics,
            producers: producersConfig
        )
    }

    func testAddRequestedValues() {
        let integration = TestMetricsClientIntegration()
        let event = Event(stream: "test.events", name: "test.event")

        ContextController(integration: integration)
            .addRequestedValues(event, config: self.streamConfig!)

        XCTAssertNil(event.agent.appInstallId)
        XCTAssertEqual(event.agent.clientPlatform, "ios")
        XCTAssertEqual(event.agent.clientPlatformFamily, "app")
        
        XCTAssertEqual(event.page?.id, 1)
        XCTAssertEqual(event.page?.title, "Test")
        XCTAssertEqual(event.page?.namespace, 0)
        XCTAssertEqual(event.page?.namespaceName, "")
        XCTAssertEqual(event.page?.revisionId, 1)
        XCTAssertEqual(event.page?.wikidataId, "Q1")
        XCTAssertEqual(event.page?.contentLanguage, "zh")
        XCTAssertEqual(event.page?.isRedirect, false)
        XCTAssertEqual(event.page?.userGroupsAllowedToEdit, [])
        XCTAssertEqual(event.page?.userGroupsAllowedToMove, [])
        
        XCTAssertEqual(event.performer?.isLoggedIn, true)
        XCTAssertEqual(event.performer?.id, 1)
        XCTAssertEqual(event.performer?.name, "TestUser")

        /// TODO: Make ContextController get the session and pageview IDs from the session controller
        /// and/or integration.
        XCTAssertNil(event.performer?.sessionId)
        XCTAssertNil(event.performer?.pageviewId)
        
        XCTAssertEqual(event.performer?.groups, ["*"])
        XCTAssertEqual(event.performer?.isBot, false)
        XCTAssertEqual(event.performer?.language, "zh")
        XCTAssertEqual(event.performer?.languageVariant, "zh-tw")
        XCTAssertEqual(event.performer?.canProbablyEditPage, true)

        XCTAssertEqual(event.performer?.editCount, 10)
        XCTAssertEqual(event.performer?.editCountBucket, "5-99 edits")
        
        /// TODO: Make this assertion pass (it should!)
        //XCTAssertEqual(event.performer?.registrationDt, integration.getUserRegistrationTimestamp())
    }

    static var allTests = [
        ("testAddRequestedValues", testAddRequestedValues),
    ]

}
