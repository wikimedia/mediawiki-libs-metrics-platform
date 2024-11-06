import Foundation

/**
 * @see https://wikitech.wikimedia.org/wiki/Metrics_Platform/Contextual_attributes
 */
enum ContextAttribute: String, Codable, CaseIterable {
    case agentAppInstallId = "agent_app_install_id"
    case agentClientPlatform = "agent_client_platform"
    case agentClientPlatformFamily = "agent_client_platform_family"
    
    case pageId = "page_id"
    case pageTitle = "page_title"
    case pageNamespace = "page_namespace"
    case pageNamespaceName = "page_namespace_name"
    case pageRevisionId = "page_revision_id"
    case pageWikidataId = "page_wikidata_id"
    case pageContentLanguage = "page_content_language"
    case pageIsRedirect = "page_is_redirect"
    case pageUserGroupsAllowedToEdit = "page_user_groups_allowed_to_edit"
    case pageUserGroupsAllowedToMove = "page_user_groups_allowed_to_move"

    case mediawikiSkin = "mediawiki_skin"
    case mediawikiVersion = "mediawiki_version"
    case mediawikiIsProduction = "mediawiki_is_production"
    case mediawikiIsDebugMode = "mediawiki_is_debug_mode"
    case mediawikiDatabase = "mediawiki_database"
    case mediawikiSiteContentLanguage = "mediawiki_site_content_language"
    case mediawikiSiteContentLanguageVariant = "mediawiki_site_content_language_variant"

    case performerIsLoggedIn = "performer_is_logged_in"
    case performerId = "performer_id"
    case performerName = "performer_name"
    case performerSessionId = "performer_session_id"
    case performerPageviewId = "performer_pageview_id"
    case performerGroups = "performer_groups"
    case performerIsBot = "performer_is_bot"
    case performerLanguage = "performer_language"
    case performerLanguageVariant = "performer_language_variant"
    case performerCanProbablyEditPage = "performer_can_probably_edit_page"
    case performerEditCount = "performer_edit_count"
    case performerEditCountBucket = "performer_edit_count_bucket"
    case performerRegistrationDt = "performer_registration_dt"
}
