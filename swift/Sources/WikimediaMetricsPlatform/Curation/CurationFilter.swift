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
    let userRegistrationTimestampRules: ComparableCurationRules<Int>?
    let userLanguageRules: EquatableCurationRules<String>?
    let userLanguageVariantRules: EquatableCurationRules<String>?

    let devicePixelRatioRules: ComparableCurationRules<Float>?
    let deviceHardwareConcurrencyRules: ComparableCurationRules<Int>?
    let deviceMaxTouchPointsRules: ComparableCurationRules<Int>?

    let accessMethodRules: EquatableCurationRules<String>?
    let platformRules: EquatableCurationRules<String>?
    let platformFamilyRules: EquatableCurationRules<String>?
    let isProductionRules: EquatableCurationRules<Bool>?

    init(
        pageIdRules: ComparableCurationRules<Int>? = nil,
        pageNamespaceIdRules: ComparableCurationRules<Int>? = nil,
        pageNamespaceTextRules: EquatableCurationRules<String>? = nil,
        pageTitleRules: EquatableCurationRules<String>? = nil,
        pageRevisionIdRules: ComparableCurationRules<Int>? = nil,
        pageWikidataIdRules: EquatableCurationRules<String>? = nil,
        pageIsRedirectRules: EquatableCurationRules<Bool>? = nil,
        pageContentLanguageRules: EquatableCurationRules<String>? = nil,
        pageUserGroupsAllowedToEditRules: CollectionCurationRules<String>? = nil,
        pageUserGroupsAllowedToMoveRules: CollectionCurationRules<String>? = nil,

        userIdRules: ComparableCurationRules<Int>? = nil,
        userNameRules: EquatableCurationRules<String>? = nil,
        userGroupsRules: CollectionCurationRules<String>? = nil,
        userIsLoggedInRules: EquatableCurationRules<Bool>? = nil,
        userIsBotRules: EquatableCurationRules<Bool>? = nil,
        userCanProbablyEditPageRules: EquatableCurationRules<Bool>? = nil,
        userEditCountRules: ComparableCurationRules<Int>? = nil,
        userEditCountBucketRules: EquatableCurationRules<String>? = nil,
        userRegistrationTimestampRules: ComparableCurationRules<Int>? = nil,
        userLanguageRules: EquatableCurationRules<String>? = nil,
        userLanguageVariantRules: EquatableCurationRules<String>? = nil,

        devicePixelRatioRules: ComparableCurationRules<Float>? = nil,
        deviceHardwareConcurrencyRules: ComparableCurationRules<Int>? = nil,
        deviceMaxTouchPointsRules: ComparableCurationRules<Int>? = nil,

        accessMethodRules: EquatableCurationRules<String>? = nil,
        platformRules: EquatableCurationRules<String>? = nil,
        platformFamilyRules: EquatableCurationRules<String>? = nil,
        isProductionRules: EquatableCurationRules<Bool>? = nil
    ) {
        self.pageIdRules = pageIdRules
        self.pageNamespaceIdRules = pageNamespaceIdRules
        self.pageNamespaceTextRules = pageNamespaceTextRules
        self.pageTitleRules = pageTitleRules
        self.pageRevisionIdRules = pageRevisionIdRules
        self.pageWikidataIdRules = pageWikidataIdRules
        self.pageIsRedirectRules = pageIsRedirectRules
        self.pageContentLanguageRules = pageContentLanguageRules
        self.pageUserGroupsAllowedToEditRules = pageUserGroupsAllowedToEditRules
        self.pageUserGroupsAllowedToMoveRules = pageUserGroupsAllowedToMoveRules

        self.userIdRules = userIdRules
        self.userNameRules = userNameRules
        self.userGroupsRules = userGroupsRules
        self.userIsLoggedInRules = userIsLoggedInRules
        self.userIsBotRules = userIsBotRules
        self.userCanProbablyEditPageRules = userCanProbablyEditPageRules
        self.userEditCountRules = userEditCountRules
        self.userEditCountBucketRules = userEditCountBucketRules
        self.userRegistrationTimestampRules = userRegistrationTimestampRules
        self.userLanguageRules = userLanguageRules
        self.userLanguageVariantRules = userLanguageVariantRules

        self.devicePixelRatioRules = devicePixelRatioRules
        self.deviceHardwareConcurrencyRules = deviceHardwareConcurrencyRules
        self.deviceMaxTouchPointsRules = deviceMaxTouchPointsRules

        self.accessMethodRules = accessMethodRules
        self.platformRules = platformRules
        self.platformFamilyRules = platformFamilyRules
        self.isProductionRules = isProductionRules
    }

    func apply(to event: Event) -> Bool {
        // Page
        if pageIdRules != nil {
            guard let pageId = event.pageData?.id else {
                return false
            }
            if !pageIdRules!.apply(to: pageId) {
                return false
            }
        }
        if pageNamespaceIdRules != nil {
            guard let pageNamespaceId = event.pageData?.namespaceId else {
                return false
            }
            if !pageNamespaceIdRules!.apply(to: pageNamespaceId) {
                return false
            }
        }
        if pageNamespaceTextRules != nil {
            guard let pageNamespaceText = event.pageData?.namespaceText else {
                return false
            }
            if !pageNamespaceTextRules!.apply(to: pageNamespaceText) {
                return false
            }
        }
        if pageTitleRules != nil {
            guard let pageTitle = event.pageData?.title else {
                return false
            }
            if !pageTitleRules!.apply(to: pageTitle) {
                return false
            }
        }
        if pageRevisionIdRules != nil {
            guard let pageRevisionId = event.pageData?.revisionId else {
                return false
            }
            if !pageRevisionIdRules!.apply(to: pageRevisionId) {
                return false
            }
        }
        if pageWikidataIdRules != nil {
            guard let pageWikidataId = event.pageData?.wikidataId else {
                return false
            }
            if !pageWikidataIdRules!.apply(to: pageWikidataId) {
                return false
            }
        }
        if pageIsRedirectRules != nil {
            guard let pageIsRedirect = event.pageData?.isRedirect else {
                return false
            }
            if !pageIsRedirectRules!.apply(to: pageIsRedirect) {
                return false
            }
        }
        if pageContentLanguageRules != nil {
            guard let pageContentLanguage = event.pageData?.contentLanguage else {
                return false
            }
            if !pageContentLanguageRules!.apply(to: pageContentLanguage) {
                return false
            }
        }
        if pageUserGroupsAllowedToEditRules != nil {
            guard let pageUserGroupsAllowedToEdit = event.pageData?.userGroupsAllowedToEdit else {
                return false
            }
            if !pageUserGroupsAllowedToEditRules!.apply(to: pageUserGroupsAllowedToEdit) {
                return false
            }
        }
        if pageUserGroupsAllowedToMoveRules != nil {
            guard let pageUserGroupsAllowedToMove = event.pageData?.userGroupsAllowedToMove else {
                return false
            }
            if !pageUserGroupsAllowedToMoveRules!.apply(to: pageUserGroupsAllowedToMove) {
                return false
            }
        }

        // User
        if userIdRules != nil {
            guard let userId = event.userData?.id else {
                return false
            }
            if !userIdRules!.apply(to: userId) {
                return false
            }
        }
        if userNameRules != nil {
            guard let userName = event.userData?.name else {
                return false
            }
            if !userNameRules!.apply(to: userName) {
                return false
            }
        }
        if userGroupsRules != nil {
            guard let userGroups = event.userData?.groups else {
                return false
            }
            if !userGroupsRules!.apply(to: userGroups) {
                return false
            }
        }
        if userIsLoggedInRules != nil {
            guard let userIsLoggedIn = event.userData?.isLoggedIn else {
                return false
            }
            if !userIsLoggedInRules!.apply(to: userIsLoggedIn) {
                return false
            }
        }
        if userIsBotRules != nil {
            guard let userIsBot = event.userData?.isBot else {
                return false
            }
            if !userIsBotRules!.apply(to: userIsBot) {
                return false
            }
        }
        if userCanProbablyEditPageRules != nil {
            guard let userCanProbablyEditPage = event.userData?.canProbablyEditPage else {
                return false
            }
            if !userCanProbablyEditPageRules!.apply(to: userCanProbablyEditPage) {
                return false
            }
        }
        if userEditCountRules != nil {
            guard let userEditCount = event.userData?.editCount else {
                return false
            }
            if !userEditCountRules!.apply(to: userEditCount) {
                return false
            }
        }
        if userEditCountBucketRules != nil {
            guard let userEditCountBucket = event.userData?.editCountBucket else {
                return false
            }
            if !userEditCountBucketRules!.apply(to: userEditCountBucket) {
                return false
            }
        }
        if userRegistrationTimestampRules != nil {
            guard let userRegistrationTimestamp = event.userData?.registrationTimestamp else {
                return false
            }
            if !userRegistrationTimestampRules!.apply(to: userRegistrationTimestamp) {
                return false
            }
        }
        if userLanguageRules != nil {
            guard let userLanguage = event.userData?.language else {
                return false
            }
            if !userLanguageRules!.apply(to: userLanguage) {
                return false
            }
        }
        if userLanguageVariantRules != nil {
            guard let userLanguageVariant = event.userData?.languageVariant else {
                return false
            }
            if !userLanguageVariantRules!.apply(to: userLanguageVariant) {
                return false
            }
        }

        // Device
        if devicePixelRatioRules != nil {
            guard let devicePixelRatio = event.deviceData?.pixelRatio else {
                return false
            }
            if !devicePixelRatioRules!.apply(to: devicePixelRatio) {
                return false
            }
        }
        if deviceHardwareConcurrencyRules != nil {
            guard let deviceHardwareConcurrency = event.deviceData?.hardwareConcurrency else {
                return false
            }
            if !deviceHardwareConcurrencyRules!.apply(to: deviceHardwareConcurrency) {
                return false
            }
        }
        if deviceMaxTouchPointsRules != nil {
            guard let deviceMaxTouchPoints = event.deviceData?.maxTouchPoints else {
                return false
            }
            if !deviceMaxTouchPointsRules!.apply(to: deviceMaxTouchPoints) {
                return false
            }
        }

        // Misc
        if accessMethodRules != nil {
            guard let accessMethod = event.accessMethod else {
                return false
            }
            if !accessMethodRules!.apply(to: accessMethod) {
                return false
            }
        }
        if platformRules != nil {
            guard let platform = event.platform else {
                return false
            }
            if !platformRules!.apply(to: platform) {
                return false
            }
        }
        if platformFamilyRules != nil {
            guard let platformFamily = event.platformFamily else {
                return false
            }
            if !platformFamilyRules!.apply(to: platformFamily) {
                return false
            }
        }
        if isProductionRules != nil {
            guard let isProduction = event.isProduction else {
                return false
            }
            if !isProductionRules!.apply(to: isProduction) {
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

        case devicePixelRatioRules = "device_pixel_ratio"
        case deviceHardwareConcurrencyRules = "device_hardware_concurrency"
        case deviceMaxTouchPointsRules = "device_max_touch_points"

        case accessMethodRules = "access_method"
        case platformRules = "platform"
        case platformFamilyRules = "platform_family"
        case isProductionRules = "is_production"
    }
}
