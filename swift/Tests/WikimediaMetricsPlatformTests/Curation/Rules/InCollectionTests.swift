import XCTest
@testable import WikimediaMetricsPlatform

final class InCollectionTests: XCTestCase {

    func testStringInCollectionRule() {
        let rule = EquatableCurationRules<String>(inCollection: ["a", "b", "c"])
        XCTAssertTrue(rule.apply(to: "a"))
        XCTAssertFalse(rule.apply(to: "d"))
        XCTAssertFalse(rule.apply(to: ""))
    }

    func testIntegerInCollectionRule() {
        let rule = ComparableCurationRules<Int>(inCollection: [1, 2, 3])
        XCTAssertTrue(rule.apply(to: 1))
        XCTAssertFalse(rule.apply(to: 4))
        XCTAssertFalse(rule.apply(to: -1))
    }

    func testFloatInCollectionRule() {
        let rule = ComparableCurationRules<Float>(inCollection: [1.0, 2.0, 3.0])
        XCTAssertTrue(rule.apply(to: 1.0))
        XCTAssertFalse(rule.apply(to: 4.0))
        XCTAssertFalse(rule.apply(to: -1.0))
    }

    static var allTests = [
        ("testStringInCollectionRule", testStringInCollectionRule),
        ("testIntegerInCollectionRule", testIntegerInCollectionRule),
        ("testFloatInCollectionRule", testFloatInCollectionRule),
    ]

}
