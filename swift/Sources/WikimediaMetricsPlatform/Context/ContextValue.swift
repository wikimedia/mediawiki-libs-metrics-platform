import Foundation

enum ContextValue: String, Codable {
    case pageId = "page_id"
    case pageNamespaceId = "page_namespace_id"
    case pageNamespaceText = "page_namespace_text"
    case pageTitle = "page_title"
    case pageRevisionId = "page_revision_id"
    case pageWikidataId = "page_wikidata_id"
    case pageIsRedirect = "page_is_redirect"
    case pageContentLanguage = "page_content_language"
    case pageUserGroupsAllowedToEdit = "page_user_groups_allowed_to_edit"
    case pageUserGroupsAllowedToMove = "page_user_groups_allowed_to_move"

    case userId = "user_id"
    case userName = "user_name"
    case userGroups = "user_groups"
    case userIsLoggedIn = "user_is_logged_in"
    case userIsBot = "user_is_bot"
    case userCanProbablyEditPage = "user_can_probably_edit_page"
    case userEditCount = "user_edit_count"
    case userEditCountBucket = "user_edit_count_bucket"
    case userRegistrationTimestamp = "user_registration_timestamp"
    case userLanguage = "user_language"
    case userLanguageVariant = "user_language_variant"

    case devicePixelRatio = "device_pixel_ratio"
    case deviceHardwareConcurrency = "device_hardware_concurrency"
    case deviceMaxTouchPoints = "device_max_touch_points"

    case accessMethod = "access_method"
    case platform = "platform"
    case platformFamily = "platform_family"
    case isProduction = "is_production"
}