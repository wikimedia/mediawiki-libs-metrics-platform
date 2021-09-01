import XCTest
@testable import WikimediaMetricsPlatform

final class EqualsTests: XCTestCase {

    func testIntegerEqualsRule() {
        let rule = ComparableCurationRules<Int>(equals: 1)
        XCTAssertTrue(rule.apply(to: 1))
        XCTAssertFalse(rule.apply(to: 0))
        XCTAssertFalse(rule.apply(to: -1))
    }

    func testFloatEqualsRule() {
        let rule = ComparableCurationRules<Float>(equals: 1.0)
        XCTAssertTrue(rule.apply(to: 1.0))
        XCTAssertFalse(rule.apply(to: 0.0))
        XCTAssertFalse(rule.apply(to: -1.0))
    }

    func testBooleanEqualsRule() {
        let rule = EquatableCurationRules<Bool>(equals: true)
        XCTAssertTrue(rule.apply(to: true))
        XCTAssertFalse(rule.apply(to: false))
    }

    func testStringEqualsRule() {
        let rule = EquatableCurationRules<String>(equals: "foo")
        XCTAssertTrue(rule.apply(to: "foo"))
        XCTAssertFalse(rule.apply(to: "bar"))
        XCTAssertFalse(rule.apply(to: ""))
    }

    func testArrayEqualsRule() {
        let rule = EquatableCurationRules<[Int]>(equals: [1, 2, 3])
        XCTAssertTrue(rule.apply(to: [1, 2, 3]))
        XCTAssertFalse(rule.apply(to: [3, 2, 1]))
        XCTAssertFalse(rule.apply(to: [4, 5, 6]))
        XCTAssertFalse(rule.apply(to: []))
    }

    static var allTests = [
        ("testIntegerEqualsRule", testIntegerEqualsRule),
        ("testFloatEqualsRule", testFloatEqualsRule),
        ("testBooleanEqualsRule", testBooleanEqualsRule),
        ("testStringEqualsRule", testStringEqualsRule),
        ("testArrayEqualsRule", testArrayEqualsRule),
    ]

}
