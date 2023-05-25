import XCTest
@testable import WikimediaMetricsPlatform

final class SamplingConfigTests: XCTestCase {

    func testDecodeSamplingConfig() {
        let json = "{\"unit\":\"session\",\"rate\":0.75}"
        guard let data = json.data(using: .utf8) else {
            XCTFail()
            return
        }
        do {
            let samplingConfig = try JSONDecoder().decode(SamplingConfig.self, from: data)
            XCTAssertEqual(samplingConfig.unit, "session")
            XCTAssertEqual(samplingConfig.rate, 0.75)
        } catch {
            XCTFail("Failed to decode sampling config")
        }
    }

    static var allTests = [
        ("testDecodeSamplingConfig", testDecodeSamplingConfig),
    ]

}
