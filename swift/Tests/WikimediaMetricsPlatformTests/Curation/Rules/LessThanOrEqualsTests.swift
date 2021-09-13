import XCTest
@testable import WikimediaMetricsPlatform

final class LessThanOrEqualsTests: XCTestCase {

    func testIntegerLessThanOrEqualsRule() {
        let rule = ComparableCurationRules<Int>(lessThanOrEquals: 0)
        XCTAssertFalse(rule.apply(to: 1))
        XCTAssertTrue(rule.apply(to: 0))
        XCTAssertTrue(rule.apply(to: -1))
    }

    func testFloatLessThanOrEqualsRule() {
        let rule = ComparableCurationRules<Float>(lessThanOrEquals: 0.0)
        XCTAssertFalse(rule.apply(to: 1.0))
        XCTAssertTrue(rule.apply(to: 0.0))
        XCTAssertTrue(rule.apply(to: -1.0))
    }

    static var allTests = [
        ("testIntegerLessThanOrEqualsRule", testIntegerLessThanOrEqualsRule),
        ("testFloatLessThanOrEqualsRule", testFloatLessThanOrEqualsRule),
    ]

}
