import Foundation

class SessionController {

    private let sessionTimeout = 900.0 // 15 minutes
    private var currentSessionId: String
    private var sessionTouched = Date()

    init(date: Date) {
        self.sessionTouched = date
        self.currentSessionId = SessionController.generateSessionId()
    }

    func sessionId() -> String {
        return currentSessionId
    }

    func touchSession() {
        sessionTouched = Date()
    }

    func sessionExpired() -> Bool {
        return Date().timeIntervalSince(sessionTouched) > sessionTimeout
    }

    func beginNewSession() {
        currentSessionId = SessionController.generateSessionId()
        touchSession()
    }

    static func generateSessionId() -> String {
        return UUID().uuidString
    }

}
