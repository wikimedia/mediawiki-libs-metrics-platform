import Foundation

struct PageData: Encodable {
    var id: Int?
    var namespaceId: Int?
    var namespaceText: String?
    var title: String?
    var isRedirect: Bool?
    var revisionId: Int?
    var wikidataId: String?
    var contentLanguage: String?
    var userGroupsAllowedToMove: [String]?
    var userGroupsAllowedToEdit: [String]?

    enum CodingKeys: String, CodingKey {
        case id
        case namespaceId = "namespace_id"
        case namespaceText = "namespace_text"
        case title
        case isRedirect = "is_redirect"
        case revisionId = "revision_id"
        case wikidataId = "wikidata_id"
        case contentLanguage = "content_language"
        case userGroupsAllowedToMove = "user_groups_allowed_to_move"
        case userGroupsAllowedToEdit = "user_groups_allowed_to_edit"
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        do {
            try container.encodeIfPresent(id, forKey: .id)
            try container.encodeIfPresent(namespaceId, forKey: .namespaceId)
            try container.encodeIfPresent(namespaceText, forKey: .namespaceText)
            try container.encodeIfPresent(title, forKey: .title)
            try container.encodeIfPresent(isRedirect, forKey: .isRedirect)
            try container.encodeIfPresent(revisionId, forKey: .revisionId)
            try container.encodeIfPresent(wikidataId, forKey: .wikidataId)
            try container.encodeIfPresent(contentLanguage, forKey: .contentLanguage)
            try container.encodeIfPresent(userGroupsAllowedToMove, forKey: .userGroupsAllowedToMove)
            try container.encodeIfPresent(userGroupsAllowedToEdit, forKey: .userGroupsAllowedToEdit)
        } catch let error {
            NSLog("EPC: Error encoding event body: \(error)")
        }
    }
}
