import XCTest
@testable import WikimediaMetricsPlatform

/// Support macOS and Linux. See https://github.com/tensorflow/swift/issues/486#issuecomment-646083111
#if canImport(FoundationNetworking)
    import FoundationNetworking
#endif

final class StreamConfigsFetcherTests: XCTestCase {

    class StubIntegration: TestMetricsClientIntegration {
        var urls: [String] = []
        
        private let data: Data?
        private let response: URLResponse?
        private let error: Error?
        
        init(
            data: Data? = nil,
            response: URLResponse? = nil,
            error: Error? = nil
        ) {
            self.data = data
            self.response = response
            self.error = error
        }
        
        override func httpGet(_ url: URL, callback: @escaping (Data?, URLResponse?, Error?) -> Void) {
            self.urls.append(url.absoluteString)
            
            callback(self.data, self.response, self.error)
        }
    }
    
    private let dispatcher: Dispatcher = StubDispatcher()
    
    func testInit() {
        let streamConfigsFetcher = StreamConfigsFetcher(
            integration: StubIntegration(),
            dispatcher: self.dispatcher
        )
        
        XCTAssertNil(streamConfigsFetcher.getStreamConfigs())
    }
    
    func testInitStartsFetchingStreamConfigs() {
        let integration = StubIntegration()
        let _ = StreamConfigsFetcher(integration: integration, dispatcher: self.dispatcher)
        
        XCTAssertEqual(
            integration.urls[0],
            "https://meta.wikimedia.org/w/api.php?action=streamconfigs&format=json&all_settings"
        )
        
        XCTAssertEqual(integration.urls.count, 11, "It should retry fetching 10 times")
    }
    
    func testItShouldRetryWhenStatusCodeIsNot200() {
        let integration = StubIntegration(
            response: HTTPURLResponse(
                url: URL(string: "https://meta.wikimedia.org/w/api.php?action=streamconfigs&format=json&all_settings")!,
                statusCode: 404,
                httpVersion: "HTTP/2",
                headerFields: [:]
            )
        )
        
        let streamConfigsFetcher = StreamConfigsFetcher(integration: integration, dispatcher: self.dispatcher)
        
        XCTAssertEqual(integration.urls.count, 11, "It should retry fetching 10 times")
        XCTAssertNil(streamConfigsFetcher.getStreamConfigs())
    }
    
    func testItShouldStopFetchingStreamConfigs() {
        let json = """
            {
              "streams": {
                "test.event": {
                  "schema_title": "test/event",
                  "destination_event_service": "eventgate-analytics-external"
                }
              }
            }
            """
        
        let integration = StubIntegration(
            data: json.data(using: .utf8),
            response: HTTPURLResponse(
                url: URL(string: "https://meta.wikimedia.org/w/api.php?action=streamconfigs&format=json&all_settings")!,
                statusCode: 200,
                httpVersion: "HTTP/2",
                headerFields: [:]
            )
        )
        
        let streamConfigsFetcher = StreamConfigsFetcher(integration: integration, dispatcher: self.dispatcher)
        
        XCTAssertEqual(integration.urls.count, 1, "It should stop fetching after the first successful fetch")
        
        let streamConfig = streamConfigsFetcher.getStreamConfigs()!["test.event"]!
        
        XCTAssertEqual(streamConfig.schemaTitle, "test/event")
        XCTAssertEqual(streamConfig.destinationEventService, DestinationEventService.analytics)
    }
    
    func testItShouldHandleDecodingErrors() {
        let json = """
            {
                "streams:
            """
        
        let integration = StubIntegration(
            data: json.data(using: .utf8),
            response: HTTPURLResponse(
                url: URL(string: "https://meta.wikimedia.org/w/api.php?action=streamconfigs&format=json&all_settings")!,
                statusCode: 200,
                httpVersion: "HTTP/2",
                headerFields: [:]
            )
        )
        
        let streamConfigsFetcher = StreamConfigsFetcher(integration: integration, dispatcher: self.dispatcher)
        
        XCTAssertEqual(integration.urls.count, 11, "It should retry fetching 10 times")
        XCTAssertNil(streamConfigsFetcher.getStreamConfigs())
    }
    
    static var allTests = [
        ("testInit", testInit),
        ("testInitStartsFetchingStreamConfigs", testInitStartsFetchingStreamConfigs),
        ("testItShouldRetryWhenStatusCodeIsNot200", testItShouldRetryWhenStatusCodeIsNot200),
        ("testItShouldStopFetchingStreamConfigs", testItShouldStopFetchingStreamConfigs),
        ("testItShouldHandleDecodingErrors", testItShouldHandleDecodingErrors),
    ]
}
