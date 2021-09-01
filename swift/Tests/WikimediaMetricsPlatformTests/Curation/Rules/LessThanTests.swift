import XCTest
@testable import WikimediaMetricsPlatform

final class LessThanTests: XCTestCase {

    func testIntegerLessThanRule() {
        let rule = ComparableCurationRules<Int>(lessThan: 0)
        XCTAssertFalse(rule.apply(to: 1))
        XCTAssertFalse(rule.apply(to: 0))
        XCTAssertTrue(rule.apply(to: -1))
    }

    func testFloatLessThanRule() {
        let rule = ComparableCurationRules<Float>(lessThan: 0.0)
        XCTAssertFalse(rule.apply(to: 1.0))
        XCTAssertFalse(rule.apply(to: 0.0))
        XCTAssertTrue(rule.apply(to: -1.0))
    }

    static var allTests = [
        ("testIntegerLessThanRule", testIntegerLessThanRule),
        ("testFloatLessThanRule", testFloatLessThanRule),
    ]

}
