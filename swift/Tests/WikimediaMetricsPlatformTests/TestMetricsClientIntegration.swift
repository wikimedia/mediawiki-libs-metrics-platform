import Foundation

/// Support macOS and Linux. See https://github.com/tensorflow/swift/issues/486#issuecomment-646083111
#if canImport(FoundationNetworking)
    import FoundationNetworking
#endif

@testable import WikimediaMetricsPlatform

class TestMetricsClientIntegration: MetricsClientIntegration {
    func getUserRegistrationTimestamp() -> Date? {
        return Date()
    }
    
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

    // Page

    func getPageId() -> Int? {
        return 1
    }

    func getPageNamespaceId() -> Int? {
        return 0
    }

    func getPageNamespaceText() -> String? {
        return ""
    }

    func getPageTitle() -> String? {
        return "Test"
    }

    func getPageIsRedirect() -> Bool? {
        return false
    }

    func getPageRevisionId() -> Int? {
        return 1
    }

    func getPageWikidataItemId() -> String? {
        return "Q1"
    }

    func getPageContentLanguage() -> String? {
        return "zh"
    }

    func getGroupsAllowedToEditPage() -> [String]? {
        return []
    }

    func getGroupsAllowedToMovePage() -> [String]? {
        return []
    }

    // User

    func getUserId() -> Int? {
        return 1
    }

    func getUserName() -> String? {
        return "TestUser"
    }

    func getUserGroups() -> [String]? {
        return ["*"]
    }

    func getUserIsLoggedIn() -> Bool? {
        return true
    }

    func getUserIsBot() -> Bool? {
        return false
    }

    func getUserCanProbablyEditPage() -> Bool? {
        return true
    }

    func getUserEditCount() -> Int? {
        return 10
    }

    func getUserEditCountBucket() -> String? {
        return "5-99 edits"
    }

    func getUserLanguage() -> String? {
        return "zh"
    }

    func getUserLanguageVariant() -> String? {
        return "zh-tw"
    }

    // Device

    func getDevicePixelRatio() -> Float? {
        return 1.0
    }

    func getDeviceHardwareConcurrency() -> Int? {
        return 1
    }

    func getDeviceMaxTouchPoints() -> Int? {
        return 1
    }

    // Other

    func isProduction() -> Bool? {
        return true
    }

}
