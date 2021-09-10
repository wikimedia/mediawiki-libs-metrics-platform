import Foundation

enum DestinationEventService: String, Decodable {
    case analytics = "eventgate-analytics-external"
    case errorLogging = "eventgate-logging-external"
}
