import XCTest
@testable import WikimediaMetricsPlatform

final class StreamConfigTests: XCTestCase {

    func testDecodeStreamConfig() {
        let json = """
          {
            \"producers\": {
              \"metrics_platform_client\": {
                \"sampling\": {
                  \"unit\": \"device\",
                  \"rate\": 0.1
                }
              }
            },
            \"schema_title\": \"test\\/event\",
            \"stream\": \"test.event\",
            \"destination_event_service\": \"eventgate-analytics-external\"
          }
          """
        guard let data = json.data(using: .utf8) else {
            XCTFail()
            return
        }
        do {
            let decoder = JSONDecoder()
            decoder.keyDecodingStrategy = .convertFromSnakeCase
            
            let streamConfig = try decoder.decode(StreamConfig.self, from: data)
            XCTAssertEqual(streamConfig.schemaTitle, "test/event")
            XCTAssertEqual(streamConfig.destinationEventService, .analytics)
            XCTAssertEqual(streamConfig.getSamplingConfig()?.rate, 0.1)
            XCTAssertEqual(streamConfig.getSamplingConfig()?.unit, "device")
        } catch {
            XCTFail("Failed to encode or decode stream config: \(error)")
        }
    }

    static var allTests = [
        ("testDecodeStreamConfig", testDecodeStreamConfig),
    ]

}
