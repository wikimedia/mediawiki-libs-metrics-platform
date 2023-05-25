import Foundation

/// Support macOS and Linux. See https://github.com/tensorflow/swift/issues/486#issuecomment-646083111
#if canImport(FoundationNetworking)
    import FoundationNetworking
#endif

protocol MetricsClientIntegration {
    func loggingEnabled() -> Bool
    func appInstallId() -> String
    func httpGet(_ url: URL, callback: @escaping (Data?, URLResponse?, Error?) -> Void)
    func httpPost(_ url: URL, body: Data?, callback: @escaping (Result<Void, Error>) -> Void)

    func getPageId() -> Int?
    func getPageNamespaceId() -> Int?
    func getPageNamespaceText() -> String?
    func getPageTitle() -> String?
    func getPageIsRedirect() -> Bool?
    func getPageRevisionId() -> Int?
    func getPageWikidataItemId() -> String?
    func getPageContentLanguage() -> String?
    func getGroupsAllowedToEditPage() -> [String]?
    func getGroupsAllowedToMovePage() -> [String]?

    func getUserId() -> Int?
    func getUserName() -> String?
    func getUserGroups() -> [String]?
    func getUserIsLoggedIn() -> Bool?
    func getUserIsBot() -> Bool?
    func getUserCanProbablyEditPage() -> Bool?
    func getUserEditCount() -> Int?
    func getUserEditCountBucket() -> String?
    func getUserRegistrationTimestamp() -> Date?
    func getUserLanguage() -> String?
    func getUserLanguageVariant() -> String?

    func getDevicePixelRatio() -> Float?
    func getDeviceHardwareConcurrency() -> Int?
    func getDeviceMaxTouchPoints() -> Int?

    func isProduction() -> Bool?
}
