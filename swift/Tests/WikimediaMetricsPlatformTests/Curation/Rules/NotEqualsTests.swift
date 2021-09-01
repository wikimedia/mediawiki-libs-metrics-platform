import XCTest
@testable import WikimediaMetricsPlatform

final class NotEqualsTests: XCTestCase {

    func testIntegerNotEqualsRule() {
        let rule = ComparableCurationRules<Int>(notEquals: 1)
        XCTAssertFalse(rule.apply(to: 1))
        XCTAssertTrue(rule.apply(to: 0))
        XCTAssertTrue(rule.apply(to: -1))
    }

    func testFloatNotEqualsRule() {
        let rule = ComparableCurationRules<Float>(notEquals: 1.0)
        XCTAssertFalse(rule.apply(to: 1.0))
        XCTAssertTrue(rule.apply(to: 0.0))
        XCTAssertTrue(rule.apply(to: -1.0))
    }

    func testBooleanNotEqualsRule() {
        let rule = EquatableCurationRules<Bool>(notEquals: true)
        XCTAssertFalse(rule.apply(to: true))
        XCTAssertTrue(rule.apply(to: false))
    }

    func testStringNotEqualsRule() {
        let rule = EquatableCurationRules<String>(notEquals: "foo")
        XCTAssertFalse(rule.apply(to: "foo"))
        XCTAssertTrue(rule.apply(to: "bar"))
        XCTAssertTrue(rule.apply(to: ""))
    }

    func testArrayNotEqualsRule() {
        let rule = EquatableCurationRules<[Int]>(notEquals: [1, 2, 3])
        XCTAssertFalse(rule.apply(to: [1, 2, 3]))
        XCTAssertTrue(rule.apply(to: [3, 2, 1]))
        XCTAssertTrue(rule.apply(to: [4, 5, 6]))
        XCTAssertTrue(rule.apply(to: []))
    }

    static var allTests = [
        ("testIntegerNotEqualsRule", testIntegerNotEqualsRule),
        ("testFloatNotEqualsRule", testFloatNotEqualsRule),
        ("testBooleanNotEqualsRule", testBooleanNotEqualsRule),
        ("testStringNotEqualsRule", testStringNotEqualsRule),
        ("testArrayNotEqualsRule", testArrayNotEqualsRule),
    ]

}
