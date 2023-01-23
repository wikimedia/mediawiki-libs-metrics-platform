import Foundation

/// Support macOS and Linux. See https://github.com/tensorflow/swift/issues/486#issuecomment-646083111
#if canImport(FoundationNetworking)
    import FoundationNetworking
#endif

protocol StreamConfigsFetching {

    func getStreamConfigs() -> [String : StreamConfig]?
    
    /**
     * Fetches the stream configs from the MediaWiki Action API streamconfigs query module.
     */
    func fetchStreamConfigs() -> Void;
}

/**
 * Fetches stream configs on a background thread.
 *
 * The stream configs are fetched from metawiki via the streamconfigs MediaWiki API query module. The first fetch
 * attempt starts at the end of initialization. If that fetch fails, then a another one is attempted after a delay and
 * so on up to a maximum of 10 attempts.
 *
 * The initial delay between fetch attempts is 30 seconds. After each failed attempt, the dleay is scaled. This is done
 * to reduce the load on the origin servers in the event of an error or outage.
 */
class StreamConfigsFetcher: StreamConfigsFetching {

    private let STREAMCONFIGS_URL: URL = URL(
        string: "https://meta.wikimedia.org/w/api.php?action=streamconfigs&format=json&all_settings"
    )!

    private let integration: MetricsClientIntegration;
    private let dispatcher: Dispatcher
    private let decoder: JSONDecoder;
    private var streamConfigs: [String: StreamConfig]? = nil
    
    init(integration: MetricsClientIntegration, dispatcher: Dispatcher? = nil) {
        self.integration = integration
        
        self.dispatcher = dispatcher ?? Dispatcher(
            dispatchQueue: DispatchQueue(label: "MetricsClient-DefaultStreamConfigsFetcher-" + UUID().uuidString)
        )
        
        self.decoder = JSONDecoder()
        self.decoder.keyDecodingStrategy = .convertFromSnakeCase
        self.decoder.dateDecodingStrategy = .iso8601

        fetchStreamConfigs()
    }
    
    func getStreamConfigs() -> [String : StreamConfig]? {
        return streamConfigs
    }
    
    func fetchStreamConfigs() {
        fetchStreamConfigs(10, 30)
    }

    private func fetchStreamConfigs(_ retries: Int, _ retryDelay: Double) {
        self.dispatcher.async {
            NSLog("MetricsClient: Fetching stream configs…")
            
            self.integration.httpGet(self.STREAMCONFIGS_URL) { (data, response, error) in
                let retries = retries - 1
                let retryDelay = retryDelay * 1.5
                let response = response as? HTTPURLResponse

                if response == nil {
                    NSLog("MetricsClient: Error fetching stream configs! Can't downcast response to HTTPURLResponse. Retrying in \(retryDelay) seconds…")
                    
                    return self.retryFetchStreamConfigs(retries, retryDelay)
                } else if response?.statusCode != 200 || data == nil {
                    NSLog("MetricsClient: Error fetching stream configs! HTTP \(response!.statusCode) received and/or data is nil. Retrying in \(retryDelay) seconds…")

                    return self.retryFetchStreamConfigs(retries, retryDelay)
                }
                
                do {
                    let json = try self.decoder.decode(StreamConfigsJSON.self, from: data!)
                    
                    self.streamConfigs = json.streams
                } catch {
                    NSLog("MetricsClient: Error decoding stream configs JSON! Error: \(error)")
                    
                    self.retryFetchStreamConfigs(retries, retryDelay)
                }
            }
        }
    }
    
    private func retryFetchStreamConfigs(_ retries: Int, _ retryDelay: Double) {
        if retries >= 0 {
            self.dispatcher.asyncAfter(deadline: .now() + retryDelay) {
                self.fetchStreamConfigs(retries, retryDelay)
            }
        } else {
            NSLog("MetricsClient: Ran out of retries when fetching stream configs!")
        }
    }
}
