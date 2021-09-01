import XCTest
@testable import WikimediaMetricsPlatform

final class CollectionContainsAllTests: XCTestCase {

    func testStringCollectionContainsAllRule() {
        let rule = CollectionCurationRules<String>(containsAll: ["a", "b", "c"])
        XCTAssertTrue(rule.apply(to: ["a", "b", "c"]))
        XCTAssertFalse(rule.apply(to: ["c", "d", "e"]))
        XCTAssertFalse(rule.apply(to: ["d", "e", "f"]))
        XCTAssertFalse(rule.apply(to: []))
    }

    func testIntegerCollectionContainsAllRule() {
        let rule = CollectionCurationRules<Int>(containsAll: [1, 2, 3])
        XCTAssertTrue(rule.apply(to: [1, 2, 3]))
        XCTAssertFalse(rule.apply(to: [3, 4, 5]))
        XCTAssertFalse(rule.apply(to: [4, 5, 6]))
        XCTAssertFalse(rule.apply(to: []))
    }

    func testFloatCollectionContainsAllRule() {
        let rule = CollectionCurationRules<Float>(containsAll: [1.0, 2.0, 3.0])
        XCTAssertTrue(rule.apply(to: [1.0, 2.0, 3.0]))
        XCTAssertFalse(rule.apply(to: [3.0, 4.0, 5.0]))
        XCTAssertFalse(rule.apply(to: [4.0, 5.0, 6.0]))
        XCTAssertFalse(rule.apply(to: []))
    }

    static var allTests = [
        ("testStringCollectionContainsAllRule", testStringCollectionContainsAllRule),
        ("testIntegerCollectionContainsAllRule", testIntegerCollectionContainsAllRule),
        ("testFloatCollectionContainsAllRule", testFloatCollectionContainsAllRule),
    ]

}
