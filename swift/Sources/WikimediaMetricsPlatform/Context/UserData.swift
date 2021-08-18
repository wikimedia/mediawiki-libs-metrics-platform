import Foundation

struct UserData: Encodable {
    var id: Int?
    var name: String?
    var groups: [String]?
    var isLoggedIn: Bool?
    var isBot: Bool?
    var canProbablyEditPage: Bool?
    var editCount: Int?
    var editCountBucket: String?
    var registrationTimestamp: Int?
    var language: String?
    var languageVariant: String?

    enum CodingKeys: String, CodingKey {
        case id
        case name
        case groups
        case isLoggedIn = "is_logged_in"
        case isBot = "is_bot"
        case canProbablyEditPage = "can_probably_edit_page"
        case editCount = "edit_count"
        case editCountBucket = "edit_count_bucket"
        case registrationTimestamp = "registration_timestamp"
        case language
        case languageVariant = "language_variant"
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        do {
            try container.encodeIfPresent(id, forKey: .id)
            try container.encodeIfPresent(name, forKey: .name)
            try container.encodeIfPresent(groups, forKey: .groups)
            try container.encodeIfPresent(isLoggedIn, forKey: .isLoggedIn)
            try container.encodeIfPresent(isBot, forKey: .isBot)
            try container.encodeIfPresent(canProbablyEditPage, forKey: .canProbablyEditPage)
            try container.encodeIfPresent(editCount, forKey: .editCount)
            try container.encodeIfPresent(editCountBucket, forKey: .editCountBucket)
            try container.encodeIfPresent(registrationTimestamp, forKey: .registrationTimestamp)
            try container.encodeIfPresent(language, forKey: .language)
            try container.encodeIfPresent(languageVariant, forKey: .languageVariant)
        } catch let error {
            NSLog("EPC: Error encoding event body: \(error)")
        }
    }
}
