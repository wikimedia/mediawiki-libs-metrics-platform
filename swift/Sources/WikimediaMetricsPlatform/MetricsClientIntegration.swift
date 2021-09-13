import Foundation
import FoundationNetworking

protocol MetricsClientIntegration {
    func loggingEnabled() -> Bool
    func appInstallId() -> String
    func httpGet(_ url: URL, callback: @escaping (Data?, URLResponse?, Error?) -> Void)
    func httpPost(_ url: URL, body: Data?, callback: @escaping (Result<Void, Error>) -> Void)
}
