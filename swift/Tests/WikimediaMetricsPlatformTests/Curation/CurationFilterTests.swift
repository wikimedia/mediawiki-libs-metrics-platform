import XCTest
@testable import WikimediaMetricsPlatform

final class CurationFilterTests: XCTestCase {

    var curationFilter: CurationFilter?

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
        let event = Event(stream: "test.event", name: "test.curation_filter")
        
        event.page = Event.Page()
        event.page?.id = 1
        event.page?.namespaceName = "Talk"
        
        event.performer = Event.Performer()
        event.performer?.groups = ["user", "autoconfirmed", "steward"]
        event.performer?.isLoggedIn = true
        event.performer?.editCountBucket = "1000+ edits"
        
        XCTAssertTrue(curationFilter!.apply(to: event))
    }

    func testEventFailsWrongPageId() {
        let event1 = Event(stream: "test.event", name: "test.curation_filter")

        event1.page = Event.Page()
        event1.page?.id = 42
        event1.page?.namespaceName = "Talk"

        event1.performer = Event.Performer()
        event1.performer?.groups = ["user", "autoconfirmed", "steward"]
        event1.performer?.isLoggedIn = true
        event1.performer?.editCountBucket = "1000+ edits"

        XCTAssertFalse(curationFilter!.apply(to: event1))

        let event2 = Event(stream: "test.event", name: "test.curation_filter")
        
        event2.page = Event.Page()
        event2.page?.id = 501
        event2.page?.namespaceName = "Talk"

        event2.performer = Event.Performer()
        event2.performer?.groups = ["user", "autoconfirmed", "steward"]
        event2.performer?.isLoggedIn = true
        event2.performer?.editCountBucket = "1000+ edits"

        XCTAssertFalse(curationFilter!.apply(to: event2))
    }

    func testEventFailsWrongPageNamespaceText() {
        let event1 = Event(stream: "test.event", name: "test.curation_filter")
        
        event1.page = Event.Page()
        event1.page?.id = 1
        event1.page?.namespaceName = "User"

        event1.performer = Event.Performer()
        event1.performer?.groups = ["user", "autoconfirmed", "steward"]
        event1.performer?.isLoggedIn = true
        event1.performer?.editCountBucket = "1000+ edits"

        XCTAssertFalse(curationFilter!.apply(to: event1))

        let event2 = Event(stream: "test.event", name: "test.curation_filter")

        event2.page = Event.Page()
        event2.page?.id = 1
        event2.page?.namespaceName = ""

        event2.performer = Event.Performer()
        event2.performer?.groups = ["user", "autoconfirmed", "steward"]
        event2.performer?.isLoggedIn = true
        event2.performer?.editCountBucket = "1000+ edits"

        XCTAssertFalse(curationFilter!.apply(to: event2))
    }

    func testEventFailsWrongUserGroups() {
        let event1 = Event(stream: "test.event", name: "test.curation_filter")
        
        event1.page = Event.Page()
        event1.page?.id = 1
        event1.page?.namespaceName = "Talk"
        
        event1.performer = Event.Performer()
        event1.performer?.groups = ["user", "autoconfirmed", "sysop"]
        event1.performer?.isLoggedIn = true
        event1.performer?.editCountBucket = "1000+ edits"

        XCTAssertFalse(curationFilter!.apply(to: event1))

        let event2 = Event(stream: "test.event", name: "test.curation_filter")
        
        event2.page = Event.Page()
        event2.page?.id = 1
        event2.page?.namespaceName = "Talk"
        
        event2.performer = Event.Performer()
        event2.performer?.groups = []
        event2.performer?.isLoggedIn = true
        event2.performer?.editCountBucket = "1000+ edits"

        XCTAssertFalse(curationFilter!.apply(to: event2))
    }

    func testEventFailsUserNotLoggedIn() {
        let event = Event(stream: "test.event", name: "test.curation_filter")
        
        event.page = Event.Page()
        event.page?.id = 1
        event.page?.namespaceName = "Talk"
        
        event.performer = Event.Performer()
        event.performer?.groups = ["user", "autoconfirmed", "steward"]
        event.performer?.isLoggedIn = false
        event.performer?.editCountBucket = "1000+ edits"

        XCTAssertFalse(curationFilter!.apply(to: event))
    }

    func testEventFailsWrongUserEditCountBucket() {
        let event = Event(stream: "test.event", name: "test.curation_filter")
        
        event.page = Event.Page()
        event.page?.id = 1
        event.page?.namespaceName = "Talk"

        event.performer = Event.Performer()
        event.performer?.groups = ["user", "autoconfirmed", "steward"]
        event.performer?.isLoggedIn = false
        event.performer?.editCountBucket = "5-99 edits"

        XCTAssertFalse(curationFilter!.apply(to: event))
    }

    func testEmptyEventFailsButDoesNotThrow() {
        let event = Event(stream: "test.event", name: "test.curation_filter")

        XCTAssertFalse(curationFilter!.apply(to: event))
    }

    static var allTests = [
        ("testEventPasses", testEventPasses),
        ("testEventFailsWrongPageId", testEventFailsWrongPageId),
        ("testEventFailsWrongPageNamespaceText", testEventFailsWrongPageNamespaceText),
        ("testEventFailsWrongUserGroups", testEventFailsWrongUserGroups),
        ("testEventFailsUserNotLoggedIn", testEventFailsUserNotLoggedIn),
        ("testEventFailsWrongUserEditCountBucket", testEventFailsWrongUserEditCountBucket),
        ("testEmptyEventFailsButDoesNotThrow", testEmptyEventFailsButDoesNotThrow)
    ]
}
