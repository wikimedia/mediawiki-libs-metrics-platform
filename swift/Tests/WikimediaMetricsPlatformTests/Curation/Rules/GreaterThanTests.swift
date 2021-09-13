import XCTest
@testable import WikimediaMetricsPlatform

final class GreaterThanTests: XCTestCase {

    func testIntegerGreaterThanRule() {
        let rule = ComparableCurationRules<Int>(greaterThan: 0)
        XCTAssertTrue(rule.apply(to: 1))
        XCTAssertFalse(rule.apply(to: 0))
        XCTAssertFalse(rule.apply(to: -1))
    }

    func testFloatGreaterThanRule() {
        let rule = ComparableCurationRules<Float>(greaterThan: 0.0)
        XCTAssertTrue(rule.apply(to: 1.0))
        XCTAssertFalse(rule.apply(to: 0.0))
        XCTAssertFalse(rule.apply(to: -1.0))
    }

    static var allTests = [
        ("testIntegerGreaterThanRule", testIntegerGreaterThanRule),
        ("testFloatGreaterThanRule", testFloatGreaterThanRule),
    ]

}
