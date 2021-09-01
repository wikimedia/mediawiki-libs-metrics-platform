import XCTest
@testable import WikimediaMetricsPlatform

final class GreaterThanOrEqualsTests: XCTestCase {

    func testIntegerGreaterThanOrEqualsRule() {
        let rule = ComparableCurationRules<Int>(greaterThanOrEquals: 0)
        XCTAssertTrue(rule.apply(to: 1))
        XCTAssertTrue(rule.apply(to: 0))
        XCTAssertFalse(rule.apply(to: -1))
    }

    func testFloatGreaterThanOrEqualsRule() {
        let rule = ComparableCurationRules<Float>(greaterThanOrEquals: 0.0)
        XCTAssertTrue(rule.apply(to: 1.0))
        XCTAssertTrue(rule.apply(to: 0.0))
        XCTAssertFalse(rule.apply(to: -1.0))
    }

    static var allTests = [
        ("testIntegerGreaterThanOrEqualsRule", testIntegerGreaterThanOrEqualsRule),
        ("testFloatGreaterThanOrEqualsRule", testFloatGreaterThanOrEqualsRule),
    ]

}
