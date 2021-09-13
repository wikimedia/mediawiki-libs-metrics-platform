import XCTest
@testable import WikimediaMetricsPlatform

final class CollectionContainsAnyTests: XCTestCase {

    func testStringCollectionContainsAnyRule() {
        let rule = CollectionCurationRules<String>(containsAny: ["a", "b", "c"])
        XCTAssertTrue(rule.apply(to: ["a", "b", "c"]))
        XCTAssertTrue(rule.apply(to: ["c", "d", "e"]))
        XCTAssertFalse(rule.apply(to: ["d", "e", "f"]))
        XCTAssertFalse(rule.apply(to: []))
    }

    func testIntegerCollectionContainsAnyRule() {
        let rule = CollectionCurationRules<Int>(containsAny: [1, 2, 3])
        XCTAssertTrue(rule.apply(to: [1, 2, 3]))
        XCTAssertTrue(rule.apply(to: [3, 4, 5]))
        XCTAssertFalse(rule.apply(to: [4, 5, 6]))
        XCTAssertFalse(rule.apply(to: []))
    }

    func testFloatCollectionContainsAnyRule() {
        let rule = CollectionCurationRules<Float>(containsAny: [1.0, 2.0, 3.0])
        XCTAssertTrue(rule.apply(to: [1.0, 2.0, 3.0]))
        XCTAssertTrue(rule.apply(to: [3.0, 4.0, 5.0]))
        XCTAssertFalse(rule.apply(to: [4.0, 5.0, 6.0]))
        XCTAssertFalse(rule.apply(to: []))
    }

    static var allTests = [
        ("testStringCollectionContainsAnyRule", testStringCollectionContainsAnyRule),
        ("testIntegerCollectionContainsAnyRule", testIntegerCollectionContainsAnyRule),
        ("testFloatCollectionContainsAnyRule", testFloatCollectionContainsAnyRule),
    ]

}
