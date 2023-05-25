import Foundation

struct StreamConfigsJSON: Decodable {
    var streams: [String: StreamConfig]
}
