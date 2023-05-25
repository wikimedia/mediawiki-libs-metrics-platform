import XCTest
@testable import WikimediaMetricsPlatform

final class EventTests: XCTestCase {

    func testEncodeEvent() {
        let encoder = JSONEncoder()

        encoder.outputFormatting = JSONEncoder.OutputFormatting(arrayLiteral: [.prettyPrinted, .sortedKeys])
        encoder.keyEncodingStrategy = .convertToSnakeCase
        encoder.dateEncodingStrategy = .iso8601

        let uuid = UUID().uuidString

        let event = Event(stream: "test.events", name: "test.event")
        event.meta.domain = "foo.wikipedia.org"
        event.agent.appInstallId = uuid
        
        let expectedDt = ISO8601DateFormatter().string(from: event.dt)

        do {
            let data = try encoder.encode(event)
            let json = String(decoding: data, as: UTF8.self)
            
            

            XCTAssertEqual(
                json,
                """
                {
                  "$schema" : "\\/analytics\\/mediawiki\\/client\\/metrics_event\\/1.1.0",
                  "agent" : {
                    "app_install_id" : "\(uuid)",
                    "client_platform" : "ios",
                    "client_platform_family" : "app"
                  },
                  "dt" : "\(expectedDt)",
                  "meta" : {
                    "domain" : "foo.wikipedia.org",
                    "stream" : "test.events"
                  }
                }
                """
            )
        } catch {
            XCTFail("Failed to encode event")
        }
    }

    static var allTests = [
        ("testEncodeEvent", testEncodeEvent),
    ]

}
