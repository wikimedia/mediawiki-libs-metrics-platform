import XCTest
@testable import WikimediaMetricsPlatform

final class CurationFilterTests: XCTestCase {

    var curationFilter: CurationFilter = CurationFilter()

    override func setUp() {
        super.setUp()
        let json = """
                   {
                     \"page_id\": {
                       \"less_than\": 500,
                       \"not_equals\": 42
                     },
                     \"page_namespace_text\": {
                       \"equals\": \"Talk\"
                     },
                     \"user_is_logged_in\": {
                       \"equals\": true
                     },
                     \"user_edit_count_bucket\": {
                       \"in\": [ \"100-999 edits\", \"1000+ edits\" ]
                     },
                     \"user_groups\": {
                       \"contains_all\": [ \"user\", \"autoconfirmed\" ],
                       \"does_not_contain\": \"sysop\"
                     },
                     \"device_pixel_ratio\": {
                       \"greater_than_or_equals\": 1.5,
                       \"less_than_or_equals\": 2.5
                     }
                   }
                   """
        guard let data = json.data(using: .utf8) else {
            XCTFail()
            return
        }
        do {
            let curationFilter = try JSONDecoder().decode(CurationFilter.self, from: data)
            self.curationFilter = curationFilter
        } catch {
            XCTFail("Failed to encode or decode curation filter")
        }
    }

    func testEventPasses() {
        let event = Event(stream: "test.event", schema: "test/event")
        event.pageData = PageData(id: 1, namespaceText: "Talk")
        event.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: true, editCountBucket: "1000+ edits")
        event.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertTrue(curationFilter.apply(to: event))
    }

    func testEventFailsWrongPageId() {
        let event1 = Event(stream: "test.event", schema: "test/event")
        event1.pageData = PageData(id: 42, namespaceText: "Talk")
        event1.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: true, editCountBucket: "1000+ edits")
        event1.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertFalse(curationFilter.apply(to: event1))

        let event2 = Event(stream: "test.event", schema: "test/event")
        event2.pageData = PageData(id: 501, namespaceText: "Talk")
        event2.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: true, editCountBucket: "1000+ edits")
        event2.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertFalse(curationFilter.apply(to: event2))
    }

    func testEventFailsWrongPageNamespaceText() {
        let event1 = Event(stream: "test.event", schema: "test/event")
        event1.pageData = PageData(id: 1, namespaceText: "User")
        event1.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: true, editCountBucket: "1000+ edits")
        event1.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertFalse(curationFilter.apply(to: event1))

        let event2 = Event(stream: "test.event", schema: "test/event")
        event2.pageData = PageData(id: 1, namespaceText: "")
        event2.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: true, editCountBucket: "1000+ edits")
        event2.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertFalse(curationFilter.apply(to: event2))
    }

    func testEventFailsWrongUserGroups() {
        let event1 = Event(stream: "test.event", schema: "test/event")
        event1.pageData = PageData(id: 1, namespaceText: "Talk")
        event1.userData = UserData(groups: ["user", "autoconfirmed", "sysop"], isLoggedIn: true, editCountBucket: "1000+ edits")
        event1.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertFalse(curationFilter.apply(to: event1))

        let event2 = Event(stream: "test.event", schema: "test/event")
        event2.pageData = PageData(id: 1, namespaceText: "Talk")
        event2.userData = UserData(groups: [], isLoggedIn: true, editCountBucket: "1000+ edits")
        event2.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertFalse(curationFilter.apply(to: event2))
    }

    func testEventFailsUserNotLoggedIn() {
        let event = Event(stream: "test.event", schema: "test/event")
        event.pageData = PageData(id: 1, namespaceText: "Talk")
        event.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: false, editCountBucket: "1000+ edits")
        event.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertFalse(curationFilter.apply(to: event))
    }

    func testEventFailsWrongUserEditCountBucket() {
        let event = Event(stream: "test.event", schema: "test/event")
        event.pageData = PageData(id: 1, namespaceText: "Talk")
        event.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: true, editCountBucket: "5-99 edits")
        event.deviceData = DeviceData(pixelRatio: 2.0)
        XCTAssertFalse(curationFilter.apply(to: event))
    }

    func testEventFailsWrongDevicePixelRatio() {
        let event1 = Event(stream: "test.event", schema: "test/event")
        event1.pageData = PageData(id: 1, namespaceText: "Talk")
        event1.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: true, editCountBucket: "1000+ edits")
        event1.deviceData = DeviceData(pixelRatio: 1.0)
        XCTAssertFalse(curationFilter.apply(to: event1))

        let event2 = Event(stream: "test.event", schema: "test/event")
        event2.pageData = PageData(id: 1, namespaceText: "Talk")
        event2.userData = UserData(groups: ["user", "autoconfirmed", "steward"], isLoggedIn: true, editCountBucket: "1000+ edits")
        event2.deviceData = DeviceData(pixelRatio: 3.0)
        XCTAssertFalse(curationFilter.apply(to: event2))
    }

    func testEmptyEventFailsButDoesNotThrow() {
        let event = Event(stream: "test.event", schema: "test/event")
        XCTAssertFalse(curationFilter.apply(to: event))
    }

    static var allTests = [
        ("testEventPasses", testEventPasses),
        ("testEventFailsWrongPageId", testEventFailsWrongPageId),
        ("testEventFailsWrongPageNamespaceText", testEventFailsWrongPageNamespaceText),
        ("testEventFailsWrongUserGroups", testEventFailsWrongUserGroups),
        ("testEventFailsUserNotLoggedIn", testEventFailsUserNotLoggedIn),
        ("testEventFailsWrongUserEditCountBucket", testEventFailsWrongUserEditCountBucket),
        ("testEventFailsWrongDevicePixelRatio", testEventFailsWrongDevicePixelRatio),
        ("testEmptyEventFailsButDoesNotThrow", testEmptyEventFailsButDoesNotThrow)
    ]

}
