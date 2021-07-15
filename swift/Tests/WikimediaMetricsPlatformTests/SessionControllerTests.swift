import XCTest
@testable import WikimediaMetricsPlatform

final class SessionControllerTests: XCTestCase {

    func testBeginNewSession() {
        let sessionController = SessionController(date: Date())
        let oldSessionId = sessionController.sessionId()
        sessionController.beginNewSession()
        XCTAssertNotEqual(sessionController.sessionId(), oldSessionId)
    }

    func testSessionExpiry() {
        let sessionController = SessionController(date: Date(timeIntervalSinceNow: -3600.0))
        XCTAssertTrue(sessionController.sessionExpired())
    }

    func testTouchSession() {
        let sessionController = SessionController(date: Date(timeIntervalSinceNow: -3600.0))
        sessionController.touchSession()
        XCTAssertFalse(sessionController.sessionExpired())
    }

    static var allTests = [
        ("testBeginNewSession", testBeginNewSession),
        ("testSessionExpiry", testSessionExpiry),
        ("testTouchSession", testTouchSession)
    ]

}
