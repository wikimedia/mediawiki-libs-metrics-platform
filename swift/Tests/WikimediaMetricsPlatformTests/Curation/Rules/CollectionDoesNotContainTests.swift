import XCTest
@testable import WikimediaMetricsPlatform

final class CollectionDoesNotContainTests: XCTestCase {

    func testStringCollectionDoesNotContainRule() {
        let rule = CollectionCurationRules<String>(doesNotContain: "a")
        XCTAssertFalse(rule.apply(to: ["a", "b", "c"]))
        XCTAssertTrue(rule.apply(to: ["c", "d", "e"]))
        XCTAssertTrue(rule.apply(to: []))
    }

    func testIntegerCollectionDoesNotContainRule() {
        let rule = CollectionCurationRules<Int>(doesNotContain: 1)
        XCTAssertFalse(rule.apply(to: [1, 2, 3]))
        XCTAssertTrue(rule.apply(to: [3, 4, 5]))
        XCTAssertTrue(rule.apply(to: []))
    }

    func testFloatCollectionDoesNotContainRule() {
        let rule = CollectionCurationRules<Float>(doesNotContain: 1.0)
        XCTAssertFalse(rule.apply(to: [1.0, 2.0, 3.0]))
        XCTAssertTrue(rule.apply(to: [3.0, 4.0, 5.0]))
        XCTAssertTrue(rule.apply(to: []))
    }

    static var allTests = [
        ("testStringCollectionDoesNotContainRule", testStringCollectionDoesNotContainRule),
        ("testIntegerCollectionDoesNotContainRule", testIntegerCollectionDoesNotContainRule),
        ("testFloatCollectionDoesNotContainRule", testFloatCollectionDoesNotContainRule),
    ]

}
