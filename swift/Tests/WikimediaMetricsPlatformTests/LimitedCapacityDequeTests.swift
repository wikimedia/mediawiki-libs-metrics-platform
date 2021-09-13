import XCTest
@testable import WikimediaMetricsPlatform

final class LimitedCapacityDequeTests: XCTestCase {

    func testAppend() {
        var buffer: LimitedCapacityDeque<Int> = LimitedCapacityDeque(capacity: 2)
        buffer.append(1)
        buffer.append(2)
        buffer.append(3)
        XCTAssertEqual(buffer.count(), 2);
        XCTAssertEqual(buffer.popFirst(), 2)
        XCTAssertEqual(buffer.popFirst(), 3)
    }

    static var allTests = [
        ("testAppend", testAppend),
    ]

}
