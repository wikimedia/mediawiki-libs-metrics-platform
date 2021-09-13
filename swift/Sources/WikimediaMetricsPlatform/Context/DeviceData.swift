import Foundation

struct DeviceData: Encodable {
    var pixelRatio: Float?
    var hardwareConcurrency: Int?
    var maxTouchPoints: Int?

    enum CodingKeys: String, CodingKey {
        case pixelRatio = "pixel_ratio"
        case hardwareConcurrency = "hardware_concurrency"
        case maxTouchPoints = "max_touch_points"
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        do {
            try container.encodeIfPresent(pixelRatio, forKey: .pixelRatio)
            try container.encodeIfPresent(hardwareConcurrency, forKey: .hardwareConcurrency)
            try container.encodeIfPresent(maxTouchPoints, forKey: .maxTouchPoints)
        } catch let error {
            NSLog("EPC: Error encoding event body: \(error)")
        }
    }
}
