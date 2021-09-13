import XCTest
@testable import WikimediaMetricsPlatform

final class CollectionContainsTests: XCTestCase {

    func testStringCollectionContainsRule() {
        let rule = CollectionCurationRules<String>(contains: "a")
        XCTAssertTrue(rule.apply(to: ["a", "b", "c"]))
        XCTAssertFalse(rule.apply(to: ["c", "d", "e"]))
        XCTAssertFalse(rule.apply(to: []))
    }

    func testIntegerCollectionContainsRule() {
        let rule = CollectionCurationRules<Int>(contains: 1)
        XCTAssertTrue(rule.apply(to: [1, 2, 3]))
        XCTAssertFalse(rule.apply(to: [3, 4, 5]))
        XCTAssertFalse(rule.apply(to: []))
    }

    func testFloatCollectionContainsRule() {
        let rule = CollectionCurationRules<Float>(contains: 1.0)
        XCTAssertTrue(rule.apply(to: [1.0, 2.0, 3.0]))
        XCTAssertFalse(rule.apply(to: [3.0, 4.0, 5.0]))
        XCTAssertFalse(rule.apply(to: []))
    }

    static var allTests = [
        ("testStringCollectionContainsRule", testStringCollectionContainsRule),
        ("testIntegerCollectionContainsRule", testIntegerCollectionContainsRule),
        ("testFloatCollectionContainsRule", testFloatCollectionContainsRule),
    ]

}
