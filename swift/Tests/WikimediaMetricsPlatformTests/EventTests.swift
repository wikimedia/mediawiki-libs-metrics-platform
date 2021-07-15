import XCTest
@testable import WikimediaMetricsPlatform

final class EventTests: XCTestCase {

    func testEncodeEvent() {
        let encoder = JSONEncoder()
        encoder.outputFormatting = .sortedKeys
        let uuid = UUID()
        let dateFormatter = ISO8601DateFormatter()

        let event = Event(stream: "test.event", schema: "test/event")
        event.meta.id = uuid.uuidString
        event.meta.domain = "foo.wikipedia.org"
        event.appInstallId = uuid.uuidString
        event.appSessionId = uuid.uuidString
        event.dt = dateFormatter.string(from: Date(timeIntervalSinceReferenceDate: 0.0))

        do {
            let data = try encoder.encode(event)
            let json = String(decoding: data, as: UTF8.self)
            XCTAssertEqual(json, "{\"$schema\":\"test\\/event\",\"app_install_id\":" +
                    "\"\(uuid.uuidString)\",\"app_session_id\":" +
                    "\"\(uuid.uuidString)\",\"dt\":\"2001-01-01T00:00:00Z\"," +
                    "\"meta\":{\"domain\":\"foo.wikipedia.org\"," +
                    "\"id\":\"\(uuid.uuidString)\",\"stream\":\"test.event\"}}")
        } catch {
            XCTFail("Failed to encode event")
        }
    }

    static var allTests = [
        ("testEncodeEvent", testEncodeEvent),
    ]

}
