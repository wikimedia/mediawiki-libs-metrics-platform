import XCTest

#if !canImport(ObjectiveC)
public func allTests() -> [XCTestCaseEntry] {
    return [
        testCase(ContextControllerTests.allTests),
        testCase(EventTests.allTests),
        testCase(LimitedCapacityDequeTests.allTests),
        testCase(SamplingConfigTests.allTests),
        testCase(SamplingControllerTests.allTests),
        testCase(SessionControllerTests.allTests),
        testCase(StreamConfigTests.allTests),
    ]
}
#endif
