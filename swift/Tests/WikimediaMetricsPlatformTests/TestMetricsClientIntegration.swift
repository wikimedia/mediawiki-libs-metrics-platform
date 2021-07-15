import Foundation
import FoundationNetworking
@testable import WikimediaMetricsPlatform

class TestMetricsClientIntegration: MetricsClientIntegration {

    func loggingEnabled() -> Bool {
        return true
    }

    func appInstallId() -> String {
        return "f44ed8f8-e9a7-492c-a163-be8bb0b9b379"
    }

    func httpGet(_ url: URL, callback: @escaping (Data?, URLResponse?, Error?) -> Void) {
        callback(nil, nil, nil)
    }

    func httpPost(_ url: URL, body: Data?, callback: @escaping (Result<Void, Error>) -> Void) {
        callback(Result() {})
    }

}
