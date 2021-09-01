import XCTest
@testable import WikimediaMetricsPlatform

final class NotInCollectionTests: XCTestCase {

    func testStringNotInCollectionRule() {
        let rule = EquatableCurationRules<String>(notInCollection: ["a", "b", "c"])
        XCTAssertFalse(rule.apply(to: "a"))
        XCTAssertTrue(rule.apply(to: "d"))
        XCTAssertTrue(rule.apply(to: ""))
    }

    func testIntegerNotInCollectionRule() {
        let rule = ComparableCurationRules<Int>(notInCollection: [1, 2, 3])
        XCTAssertFalse(rule.apply(to: 1))
        XCTAssertTrue(rule.apply(to: 4))
        XCTAssertTrue(rule.apply(to: -1))
    }

    func testFloatNotInCollectionRule() {
        let rule = ComparableCurationRules<Float>(notInCollection: [1.0, 2.0, 3.0])
        XCTAssertFalse(rule.apply(to: 1.0))
        XCTAssertTrue(rule.apply(to: 4.0))
        XCTAssertTrue(rule.apply(to: -1.0))
    }

    static var allTests = [
        ("testStringNotInCollectionRule", testStringNotInCollectionRule),
        ("testIntegerNotInCollectionRule", testIntegerNotInCollectionRule),
        ("testFloatNotInCollectionRule", testFloatNotInCollectionRule),
    ]

}
