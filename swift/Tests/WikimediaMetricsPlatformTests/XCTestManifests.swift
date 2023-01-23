import XCTest

#if !canImport(ObjectiveC)
public func allTests() -> [XCTestCaseEntry] {
    return [
        testCase(EventTests.allTests),
        testCase(LimitedCapacityDequeTests.allTests),
        testCase(SamplingConfigTests.allTests),
        testCase(SamplingControllerTests.allTests),
        testCase(SessionControllerTests.allTests),
        testCase(StreamConfigTests.allTests),
        testCase(StreamConfigsFetcherTests.allTests),
        testCase(ContextControllerTests.allTests),
        testCase(CurationFilterTests.allTests),
        testCase(EqualsTests.allTests),
        testCase(NotEqualsTests.allTests),
        testCase(GreaterThanTests.allTests),
        testCase(LessThanTests.allTests),
        testCase(GreaterThanOrEqualsTests.allTests),
        testCase(LessThanOrEqualsTests.allTests),
        testCase(InCollectionTests.allTests),
        testCase(NotInCollectionTests.allTests),
        testCase(CollectionContainsTests.allTests),
        testCase(CollectionDoesNotContainTests.allTests),
        testCase(CollectionContainsAllTests.allTests),
        testCase(CollectionContainsAnyTests.allTests),
    ]
}
#endif
