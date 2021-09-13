import Foundation

struct StreamConfigsJSON: Decodable {
    let streams: [String: StreamConfig]
}