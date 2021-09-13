import XCTest
@testable import WikimediaMetricsPlatform

final class ContextControllerTests: XCTestCase {

    var streamConfigs: [String: StreamConfig] = [:]

    override func setUp() {
        super.setUp()
        let requestedValues = [
            ContextValue.pageId,
            ContextValue.pageNamespaceId,
            ContextValue.pageNamespaceText,
            ContextValue.pageTitle,
            ContextValue.pageIsRedirect,
            ContextValue.pageRevisionId,
            ContextValue.pageContentLanguage,
            ContextValue.pageWikidataId,
            ContextValue.pageUserGroupsAllowedToEdit,
            ContextValue.pageUserGroupsAllowedToMove,
            ContextValue.userId,
            ContextValue.userName,
            ContextValue.userGroups,
            ContextValue.userIsLoggedIn,
            ContextValue.userIsBot,
            ContextValue.userEditCount,
            ContextValue.userEditCountBucket,
            ContextValue.userRegistrationTimestamp,
            ContextValue.userLanguage,
            ContextValue.userLanguageVariant,
            ContextValue.userCanProbablyEditPage,
            ContextValue.devicePixelRatio,
            ContextValue.deviceHardwareConcurrency,
            ContextValue.deviceMaxTouchPoints,
            ContextValue.accessMethod,
            ContextValue.platform,
            ContextValue.platformFamily,
            ContextValue.isProduction,
        ]
        let clientConfig = StreamConfig.ProducerConfig.MetricsPlatformClientConfig(requestedValues: requestedValues)
        let producerConfig = StreamConfig.ProducerConfig(clientConfig: clientConfig)
        let streamConfig = StreamConfig(
                stream: "test.event",
                schema: "test/event",
                destination: DestinationEventService.analytics,
                producerConfig: producerConfig
        )
        self.streamConfigs = ["test.event": streamConfig]
    }

    func testAddRequestedValues() {
        let event = Event(stream: "test.event", schema: "test/event")
        let contextController = ContextController(integration: TestMetricsClientIntegration())
        contextController.addRequestedValues(event, config: self.streamConfigs["test.event"]!)

        XCTAssertEqual(event.pageData?.id, 1)
        XCTAssertEqual(event.pageData?.namespaceId, 0)
        XCTAssertEqual(event.pageData?.namespaceText, "")
        XCTAssertEqual(event.pageData?.title, "Test")
        XCTAssertEqual(event.pageData?.isRedirect, false)
        XCTAssertEqual(event.pageData?.revisionId, 1)
        XCTAssertEqual(event.pageData?.contentLanguage, "zh")
        XCTAssertEqual(event.pageData?.wikidataId, "Q1")
        XCTAssertEqual(event.pageData?.userGroupsAllowedToMove, [])
        XCTAssertEqual(event.pageData?.userGroupsAllowedToEdit, [])

        XCTAssertEqual(event.userData?.id, 1)
        XCTAssertEqual(event.userData?.name, "TestUser")
        XCTAssertEqual(event.userData?.groups, ["*"])
        XCTAssertEqual(event.userData?.isLoggedIn, true)
        XCTAssertEqual(event.userData?.isBot, false)
        XCTAssertEqual(event.userData?.editCount, 10)
        XCTAssertEqual(event.userData?.editCountBucket, "5-99 edits")
        XCTAssertEqual(event.userData?.registrationTimestamp, 1427224089000)
        XCTAssertEqual(event.userData?.language, "zh")
        XCTAssertEqual(event.userData?.languageVariant, "zh-tw")
        XCTAssertEqual(event.userData?.canProbablyEditPage, true)

        XCTAssertEqual(event.deviceData?.pixelRatio, 1.0)
        XCTAssertEqual(event.deviceData?.hardwareConcurrency, 1)
        XCTAssertEqual(event.deviceData?.maxTouchPoints, 1)

        XCTAssertEqual(event.accessMethod, "mobile app")
        XCTAssertEqual(event.platform, "ios")
        XCTAssertEqual(event.platformFamily, "app")
        XCTAssertEqual(event.isProduction, true)
    }

    static var allTests = [
        ("testAddRequestedValues", testAddRequestedValues),
    ]

}
