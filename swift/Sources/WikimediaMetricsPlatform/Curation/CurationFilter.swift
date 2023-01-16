import Foundation

struct CurationFilter: Decodable {
    let pageIdRules: ComparableCurationRules<Int>?
    let pageNamespaceIdRules: ComparableCurationRules<Int>?
    let pageNamespaceTextRules: EquatableCurationRules<String>?
    let pageTitleRules: EquatableCurationRules<String>?
    let pageRevisionIdRules: ComparableCurationRules<Int>?
    let pageWikidataIdRules: EquatableCurationRules<String>?
    let pageIsRedirectRules: EquatableCurationRules<Bool>?
    let pageContentLanguageRules: EquatableCurationRules<String>?
    let pageUserGroupsAllowedToEditRules: CollectionCurationRules<String>?
    let pageUserGroupsAllowedToMoveRules: CollectionCurationRules<String>?

    let userIdRules: ComparableCurationRules<Int>?
    let userNameRules: EquatableCurationRules<String>?
    let userGroupsRules: CollectionCurationRules<String>?
    let userIsLoggedInRules: EquatableCurationRules<Bool>?
    let userIsBotRules: EquatableCurationRules<Bool>?
    let userCanProbablyEditPageRules: EquatableCurationRules<Bool>?
    let userEditCountRules: ComparableCurationRules<Int>?
    let userEditCountBucketRules: EquatableCurationRules<String>?
    let userRegistrationTimestampRules: ComparableCurationRules<Date>?
    let userLanguageRules: EquatableCurationRules<String>?
    let userLanguageVariantRules: EquatableCurationRules<String>?

    func apply(to event: Event) -> Bool {
        // Page
        if pageIdRules != nil {
            guard let pageId = event.page?.id else {
                return false
            }
            if !pageIdRules!.apply(to: pageId) {
                return false
            }
        }
        if pageNamespaceIdRules != nil {
            guard let pageNamespace = event.page?.namespace else {
                return false
            }
            if !pageNamespaceIdRules!.apply(to: pageNamespace) {
                return false
            }
        }
        if pageNamespaceTextRules != nil {
            guard let pageNamespaceName = event.page?.namespaceName else {
                return false
            }
            if !pageNamespaceTextRules!.apply(to: pageNamespaceName) {
                return false
            }
        }
        if pageTitleRules != nil {
            guard let pageTitle = event.page?.title else {
                return false
            }
            if !pageTitleRules!.apply(to: pageTitle) {
                return false
            }
        }
        if pageRevisionIdRules != nil {
            guard let pageRevisionId = event.page?.revisionId else {
                return false
            }
            if !pageRevisionIdRules!.apply(to: pageRevisionId) {
                return false
            }
        }
        if pageWikidataIdRules != nil {
            guard let pageWikidataId = event.page?.wikidataId else {
                return false
            }
            if !pageWikidataIdRules!.apply(to: pageWikidataId) {
                return false
            }
        }
        if pageIsRedirectRules != nil {
            guard let pageIsRedirect = event.page?.isRedirect else {
                return false
            }
            if !pageIsRedirectRules!.apply(to: pageIsRedirect) {
                return false
            }
        }
        if pageContentLanguageRules != nil {
            guard let pageContentLanguage = event.page?.contentLanguage else {
                return false
            }
            if !pageContentLanguageRules!.apply(to: pageContentLanguage) {
                return false
            }
        }
        if pageUserGroupsAllowedToEditRules != nil {
            guard let pageUserGroupsAllowedToEdit = event.page?.userGroupsAllowedToEdit else {
                return false
            }
            if !pageUserGroupsAllowedToEditRules!.apply(to: pageUserGroupsAllowedToEdit) {
                return false
            }
        }
        if pageUserGroupsAllowedToMoveRules != nil {
            guard let pageUserGroupsAllowedToMove = event.page?.userGroupsAllowedToMove else {
                return false
            }
            if !pageUserGroupsAllowedToMoveRules!.apply(to: pageUserGroupsAllowedToMove) {
                return false
            }
        }

        // User
        if userIdRules != nil {
            guard let userId = event.performer?.id else {
                return false
            }
            if !userIdRules!.apply(to: userId) {
                return false
            }
        }
        if userNameRules != nil {
            guard let userName = event.performer?.name else {
                return false
            }
            if !userNameRules!.apply(to: userName) {
                return false
            }
        }
        if userGroupsRules != nil {
            guard let userGroups = event.performer?.groups else {
                return false
            }
            if !userGroupsRules!.apply(to: userGroups) {
                return false
            }
        }
        if userIsLoggedInRules != nil {
            guard let userIsLoggedIn = event.performer?.isLoggedIn else {
                return false
            }
            if !userIsLoggedInRules!.apply(to: userIsLoggedIn) {
                return false
            }
        }
        if userIsBotRules != nil {
            guard let userIsBot = event.performer?.isBot else {
                return false
            }
            if !userIsBotRules!.apply(to: userIsBot) {
                return false
            }
        }
        if userCanProbablyEditPageRules != nil {
            guard let userCanProbablyEditPage = event.performer?.canProbablyEditPage else {
                return false
            }
            if !userCanProbablyEditPageRules!.apply(to: userCanProbablyEditPage) {
                return false
            }
        }
        if userEditCountRules != nil {
            guard let userEditCount = event.performer?.editCount else {
                return false
            }
            if !userEditCountRules!.apply(to: userEditCount) {
                return false
            }
        }
        if userEditCountBucketRules != nil {
            guard let userEditCountBucket = event.performer?.editCountBucket else {
                return false
            }
            if !userEditCountBucketRules!.apply(to: userEditCountBucket) {
                return false
            }
        }
        if userRegistrationTimestampRules != nil {
            guard let userRegistrationDt = event.performer?.registrationDt else {
                return false
            }
            if !userRegistrationTimestampRules!.apply(to: userRegistrationDt) {
                return false
            }
        }
        if userLanguageRules != nil {
            guard let userLanguage = event.performer?.language else {
                return false
            }
            if !userLanguageRules!.apply(to: userLanguage) {
                return false
            }
        }
        if userLanguageVariantRules != nil {
            guard let userLanguageVariant = event.performer?.languageVariant else {
                return false
            }
            if !userLanguageVariantRules!.apply(to: userLanguageVariant) {
                return false
            }
        }

        return true
    }

    enum CodingKeys: String, CodingKey {
        case pageIdRules = "page_id"
        case pageNamespaceIdRules = "page_namespace_id"
        case pageNamespaceTextRules = "page_namespace_text"
        case pageTitleRules = "page_title"
        case pageRevisionIdRules = "page_revision_id"
        case pageWikidataIdRules = "page_wikidata_id"
        case pageIsRedirectRules = "page_is_redirect"
        case pageContentLanguageRules = "page_content_language"
        case pageUserGroupsAllowedToEditRules = "page_user_groups_allowed_to_edit"
        case pageUserGroupsAllowedToMoveRules = "page_user_groups_allowed_to_move"

        case userIdRules = "user_id"
        case userNameRules = "user_name"
        case userGroupsRules = "user_groups"
        case userIsLoggedInRules = "user_is_logged_in"
        case userIsBotRules = "user_is_bot"
        case userCanProbablyEditPageRules = "user_can_probably_edit_page"
        case userEditCountRules = "user_edit_count"
        case userEditCountBucketRules = "user_edit_count_bucket"
        case userRegistrationTimestampRules = "user_registration_timestamp"
        case userLanguageRules = "user_language"
        case userLanguageVariantRules = "user_language_variant"
    }
}
