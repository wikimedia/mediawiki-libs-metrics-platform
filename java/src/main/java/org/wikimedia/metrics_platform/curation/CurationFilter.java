package org.wikimedia.metrics_platform.curation;

import com.google.gson.annotations.SerializedName;
import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.curation.rules.CollectionCurationRules;
import org.wikimedia.metrics_platform.curation.rules.ComparableCurationRules;
import org.wikimedia.metrics_platform.curation.rules.CurationRules;

public class CurationFilter {
    @SerializedName("page_id") private ComparableCurationRules<Integer> pageIdRules;
    @SerializedName("page_namespace_id") private ComparableCurationRules<Integer> pageNamespaceIdRules;
    @SerializedName("page_namespace_text") private CurationRules<String> pageNamespaceTextRules;
    @SerializedName("page_title") private CurationRules<String> pageTitleRules;
    @SerializedName("page_revision_id") private ComparableCurationRules<Integer> pageRevisionIdRules;
    @SerializedName("page_wikidata_id") private CurationRules<String> pageWikidataIdRules;
    @SerializedName("page_is_redirect") private CurationRules<Boolean> pageIsRedirectRules;
    @SerializedName("page_content_language") private CurationRules<String> pageContentLanguageRules;
    @SerializedName("page_user_groups_allowed_to_edit")
    private CollectionCurationRules<String> pageUserGroupsAllowedToEditRules;
    @SerializedName("page_user_groups_allowed_to_move")
    private CollectionCurationRules<String> pageUserGroupsAllowedToMoveRules;

    @SerializedName("user_id") private ComparableCurationRules<Integer> userIdRules;
    @SerializedName("user_name") private CurationRules<String> userNameRules;
    @SerializedName("user_groups") private CollectionCurationRules<String> userGroupsRules;
    @SerializedName("user_is_logged_in") private CurationRules<Boolean> userIsLoggedInRules;
    @SerializedName("user_is_bot") private CurationRules<Boolean> userIsBotRules;
    @SerializedName("user_can_probably_edit_page") private CurationRules<Boolean> userCanProbablyEditPageRules;
    @SerializedName("user_edit_count") private ComparableCurationRules<Integer> userEditCountRules;
    @SerializedName("user_edit_count_bucket") private CurationRules<String> userEditCountBucketRules;
    @SerializedName("user_registration_timestamp") private ComparableCurationRules<Long> userRegistrationTimestampRules;
    @SerializedName("user_language") private CurationRules<String> userLanguageRules;
    @SerializedName("user_language_variant") private CurationRules<String> userLanguageVariantRules;

    @SerializedName("device_pixel_ratio") private ComparableCurationRules<Float> devicePixelRatioRules;
    @SerializedName("device_hardware_concurrency")
    private ComparableCurationRules<Integer> deviceHardwareConcurrencyRules;
    @SerializedName("device_max_touch_points") private ComparableCurationRules<Integer> deviceMaxTouchPointsRules;

    @SerializedName("access_method") private CurationRules<String> accessMethodRules;
    private CurationRules<String> platformRules;
    @SerializedName("platform_family") private CurationRules<String> platformFamilyRules;
    @SerializedName("is_production") private CurationRules<Boolean> isProductionRules;

    public CurationFilter(Builder builder) {
        this.pageIdRules = builder.pageIdRules;
        this.pageNamespaceIdRules = builder.pageNamespaceIdRules;
        this.pageNamespaceTextRules = builder.pageNamespaceTextRules;
        this.pageTitleRules = builder.pageTitleRules;
        this.pageRevisionIdRules = builder.pageRevisionIdRules;
        this.pageWikidataIdRules = builder.pageWikidataIdRules;
        this.pageIsRedirectRules = builder.pageIsRedirectRules;
        this.pageContentLanguageRules = builder.pageContentLanguageRules;
        this.pageUserGroupsAllowedToEditRules = builder.pageUserGroupsAllowedToEditRules;
        this.pageUserGroupsAllowedToMoveRules = builder.pageUserGroupsAllowedToMoveRules;

        this.userIdRules = builder.userIdRules;
        this.userNameRules = builder.userNameRules;
        this.userGroupsRules = builder.userGroupsRules;
        this.userIsLoggedInRules = builder.userIsLoggedInRules;
        this.userIsBotRules = builder.userIsBotRules;
        this.userCanProbablyEditPageRules = builder.userCanProbablyEditPageRules;
        this.userEditCountRules = builder.userEditCountRules;
        this.userEditCountBucketRules = builder.userEditCountBucketRules;
        this.userRegistrationTimestampRules = builder.userRegistrationTimestampRules;
        this.userLanguageRules = builder.userLanguageRules;
        this.userLanguageVariantRules = builder.userLanguageVariantRules;

        this.devicePixelRatioRules = builder.devicePixelRatioRules;
        this.deviceHardwareConcurrencyRules = builder.deviceHardwareConcurrencyRules;
        this.deviceMaxTouchPointsRules = builder.deviceMaxTouchPointsRules;

        this.accessMethodRules = builder.accessMethodRules;
        this.platformRules = builder.platformRules;
        this.platformFamilyRules = builder.platformFamilyRules;
        this.isProductionRules = builder.isProductionRules;
    }

    public ComparableCurationRules<Integer> getPageIdRules() {
        return pageIdRules;
    }

    public ComparableCurationRules<Integer> getPageNamespaceIdRules() {
        return pageNamespaceIdRules;
    }

    public CurationRules<String> getPageNamespaceTextRules() {
        return pageNamespaceTextRules;
    }

    public CurationRules<String> getPageTitleRules() {
        return pageTitleRules;
    }

    public ComparableCurationRules<Integer> getPageRevisionIdRules() {
        return pageRevisionIdRules;
    }

    public CurationRules<String> getPageWikidataIdRules() {
        return pageWikidataIdRules;
    }

    public CurationRules<Boolean> getPageIsRedirectRules() {
        return pageIsRedirectRules;
    }

    public CurationRules<String> getPageContentLanguageRules() {
        return pageContentLanguageRules;
    }

    public CollectionCurationRules<String> getPageUserGroupsAllowedToEditRules() {
        return pageUserGroupsAllowedToEditRules;
    }

    public CollectionCurationRules<String> getPageUserGroupsAllowedToMoveRules() {
        return pageUserGroupsAllowedToMoveRules;
    }

    public ComparableCurationRules<Integer> getUserIdRules() {
        return userIdRules;
    }

    public CurationRules<String> getUserNameRules() {
        return userNameRules;
    }

    public CollectionCurationRules<String> getUserGroupsRules() {
        return userGroupsRules;
    }

    public CurationRules<Boolean> getUserIsLoggedInRules() {
        return userIsLoggedInRules;
    }

    public CurationRules<Boolean> getUserIsBotRules() {
        return userIsBotRules;
    }

    public CurationRules<Boolean> getUserCanProbablyEditPageRules() {
        return userCanProbablyEditPageRules;
    }

    public ComparableCurationRules<Integer> getUserEditCountRules() {
        return userEditCountRules;
    }

    public CurationRules<String> getUserEditCountBucketRules() {
        return userEditCountBucketRules;
    }

    public ComparableCurationRules<Long> getUserRegistrationTimestampRules() {
        return userRegistrationTimestampRules;
    }

    public CurationRules<String> getUserLanguageRules() {
        return userLanguageRules;
    }

    public CurationRules<String> getUserLanguageVariantRules() {
        return userLanguageVariantRules;
    }

    public ComparableCurationRules<Float> getDevicePixelRatioRules() {
        return devicePixelRatioRules;
    }

    public ComparableCurationRules<Integer> getDeviceHardwareConcurrencyRules() {
        return deviceHardwareConcurrencyRules;
    }

    public ComparableCurationRules<Integer> getDeviceMaxTouchPointsRules() {
        return deviceMaxTouchPointsRules;
    }

    public CurationRules<String> getAccessMethodRules() {
        return accessMethodRules;
    }

    public CurationRules<String> getPlatformRules() {
        return platformRules;
    }

    public CurationRules<String> getPlatformFamilyRules() {
        return platformFamilyRules;
    }

    public CurationRules<Boolean> getIsProductionRules() {
        return isProductionRules;
    }

    public boolean apply(Event event) {
        // Page

        if (pageIdRules != null) {
            if (event.getPageData() == null || event.getPageData().getId() == null) {
                return false;
            }
            if (!pageIdRules.apply(event.getPageData().getId())) {
                return false;
            }
        }
        if (pageNamespaceIdRules != null) {
            if (event.getPageData() == null || event.getPageData().getNamespaceId() == null) {
                return false;
            }
            if (!pageNamespaceIdRules.apply(event.getPageData().getNamespaceId())) {
                return false;
            }
        }
        if (pageNamespaceTextRules != null) {
            if (event.getPageData() == null || event.getPageData().getNamespaceText() == null) {
                return false;
            }
            if (!pageNamespaceTextRules.apply(event.getPageData().getNamespaceText())) {
                return false;
            }
        }
        if (pageTitleRules != null) {
            if (event.getPageData() == null || event.getPageData().getTitle() == null) {
                return false;
            }
            if (!pageTitleRules.apply(event.getPageData().getTitle())) {
                return false;
            }
        }
        if (pageRevisionIdRules != null) {
            if (event.getPageData() == null || event.getPageData().getRevisionId() == null) {
                return false;
            }
            if (!pageRevisionIdRules.apply(event.getPageData().getRevisionId())) {
                return false;
            }
        }
        if (pageWikidataIdRules != null) {
            if (event.getPageData() == null || event.getPageData().getWikidataItemId() == null) {
                return false;
            }
            if (!pageWikidataIdRules.apply(event.getPageData().getWikidataItemId())) {
                return false;
            }
        }
        if (pageIsRedirectRules != null) {
            if (event.getPageData() == null || event.getPageData().isRedirect() == null) {
                return false;
            }
            if (!pageIsRedirectRules.apply(event.getPageData().isRedirect())) {
                return false;
            }
        }
        if (pageContentLanguageRules != null) {
            if (event.getPageData() == null || event.getPageData().getContentLanguage() == null) {
                return false;
            }
            if (!pageContentLanguageRules.apply(event.getPageData().getContentLanguage())) {
                return false;
            }
        }
        if (pageUserGroupsAllowedToEditRules != null) {
            if (event.getPageData() == null || event.getPageData().getGroupsAllowedToEdit() == null) {
                return false;
            }
            if (!pageUserGroupsAllowedToEditRules.apply(event.getPageData().getGroupsAllowedToEdit())) {
                return false;
            }
        }
        if (pageUserGroupsAllowedToMoveRules != null) {
            if (event.getPageData() == null || event.getPageData().getGroupsAllowedToMove() == null) {
                return false;
            }
            if (!pageUserGroupsAllowedToMoveRules.apply(event.getPageData().getGroupsAllowedToMove())) {
                return false;
            }
        }

        // User

        if (userIdRules != null) {
            if (event.getUserData() == null || event.getUserData().getId() == null) {
                return false;
            }
            if (!userIdRules.apply(event.getUserData().getId())) {
                return false;
            }
        }
        if (userNameRules != null) {
            if (event.getUserData() == null || event.getUserData().getName() == null) {
                return false;
            }
            if (!userNameRules.apply(event.getUserData().getName())) {
                return false;
            }
        }
        if (userGroupsRules != null) {
            if (event.getUserData() == null || event.getUserData().getGroups() == null) {
                return false;
            }
            if (!userGroupsRules.apply(event.getUserData().getGroups())) {
                return false;
            }
        }
        if (userIsLoggedInRules != null) {
            if (event.getUserData() == null || event.getUserData().isLoggedIn() == null) {
                return false;
            }
            if (!userIsLoggedInRules.apply(event.getUserData().isLoggedIn())) {
                return false;
            }
        }
        if (userIsBotRules != null) {
            if (event.getUserData() == null || event.getUserData().isBot() == null) {
                return false;
            }
            if (!userIsBotRules.apply(event.getUserData().isBot())) {
                return false;
            }
        }
        if (userCanProbablyEditPageRules != null) {
            if (event.getUserData() == null || event.getUserData().canProbablyEditPage() == null) {
                return false;
            }
            if (!userCanProbablyEditPageRules.apply(event.getUserData().canProbablyEditPage())) {
                return false;
            }
        }
        if (userEditCountRules != null) {
            if (event.getUserData() == null || event.getUserData().getEditCount() == null) {
                return false;
            }
            if (!userEditCountRules.apply(event.getUserData().getEditCount())) {
                return false;
            }
        }
        if (userEditCountBucketRules != null) {
            if (event.getUserData() == null || event.getUserData().getEditCountBucket() == null) {
                return false;
            }
            if (!userEditCountBucketRules.apply(event.getUserData().getEditCountBucket())) {
                return false;
            }
        }
        if (userRegistrationTimestampRules != null) {
            if (event.getUserData() == null || event.getUserData().getRegistrationTimestamp() == null) {
                return false;
            }
            if (!userRegistrationTimestampRules.apply(event.getUserData().getRegistrationTimestamp())) {
                return false;
            }
        }
        if (userLanguageRules != null) {
            if (event.getUserData() == null || event.getUserData().getLanguage() == null) {
                return false;
            }
            if (!userLanguageRules.apply(event.getUserData().getLanguage())) {
                return false;
            }
        }
        if (userLanguageVariantRules != null) {
            if (event.getUserData() == null || event.getUserData().getLanguageVariant() == null) {
                return false;
            }
            if (!userLanguageVariantRules.apply(event.getUserData().getLanguageVariant())) {
                return false;
            }
        }

        // Device

        if (devicePixelRatioRules != null) {
            if (event.getDeviceData() == null || event.getDeviceData().getPixelRatio() == null) {
                return false;
            }
            if (!devicePixelRatioRules.apply(event.getDeviceData().getPixelRatio())) {
                return false;
            }
        }
        if (deviceHardwareConcurrencyRules != null) {
            if (event.getDeviceData() == null || event.getDeviceData().getHardwareConcurrency() == null) {
                return false;
            }
            if (!deviceHardwareConcurrencyRules.apply(event.getDeviceData().getHardwareConcurrency())) {
                return false;
            }
        }
        if (deviceMaxTouchPointsRules != null) {
            if (event.getDeviceData() == null || event.getDeviceData().getMaxTouchPoints() == null) {
                return false;
            }
            if (!deviceMaxTouchPointsRules.apply(event.getDeviceData().getMaxTouchPoints())) {
                return false;
            }
        }

        // Misc

        if (accessMethodRules != null) {
            if (event.getAccessMethod() == null) {
                return false;
            }
            if (!accessMethodRules.apply(event.getAccessMethod())) {
                return false;
            }
        }
        if (platformRules != null) {
            if (event.getPlatform() == null) {
                return false;
            }
            if (!platformRules.apply(event.getPlatform())) {
                return false;
            }
        }
        if (platformFamilyRules != null) {
            if (event.getPlatformFamily() == null) {
                return false;
            }
            if (!platformFamilyRules.apply(event.getPlatformFamily())) {
                return false;
            }
        }
        if (isProductionRules != null) {
            if (event.isProduction() == null) {
                return false;
            }
            if (!isProductionRules.apply(event.isProduction())) {
                return false;
            }
        }
        return true;
    }

    public static class Builder {
        private ComparableCurationRules<Integer> pageIdRules;
        private ComparableCurationRules<Integer> pageNamespaceIdRules;
        private CurationRules<String> pageNamespaceTextRules;
        private CurationRules<String> pageTitleRules;
        private ComparableCurationRules<Integer> pageRevisionIdRules;
        private CurationRules<String> pageWikidataIdRules;
        private CurationRules<Boolean> pageIsRedirectRules;
        private CurationRules<String> pageContentLanguageRules;
        private CollectionCurationRules<String> pageUserGroupsAllowedToEditRules;
        private CollectionCurationRules<String> pageUserGroupsAllowedToMoveRules;
        private ComparableCurationRules<Integer> userIdRules;
        private CurationRules<String> userNameRules;
        private CollectionCurationRules<String> userGroupsRules;
        private CurationRules<Boolean> userIsLoggedInRules;
        private CurationRules<Boolean> userIsBotRules;
        private CurationRules<Boolean> userCanProbablyEditPageRules;
        private ComparableCurationRules<Integer> userEditCountRules;
        private CurationRules<String> userEditCountBucketRules;
        private ComparableCurationRules<Long> userRegistrationTimestampRules;
        private CurationRules<String> userLanguageRules;
        private CurationRules<String> userLanguageVariantRules;
        private ComparableCurationRules<Float> devicePixelRatioRules;
        private ComparableCurationRules<Integer> deviceHardwareConcurrencyRules;
        private ComparableCurationRules<Integer> deviceMaxTouchPointsRules;
        private CurationRules<String> accessMethodRules;
        private CurationRules<String> platformRules;
        private CurationRules<String> platformFamilyRules;
        private CurationRules<Boolean> isProductionRules;

        public Builder pageIdRules(ComparableCurationRules<Integer> pageIdRules) {
            this.pageIdRules = pageIdRules;
            return this;
        }

        public Builder pageNamespaceIdRules(ComparableCurationRules<Integer> pageNamespaceIdRules) {
            this.pageNamespaceIdRules = pageNamespaceIdRules;
            return this;
        }

        public Builder pageNamespaceTextRules(CurationRules<String> pageNamespaceTextRules) {
            this.pageNamespaceTextRules = pageNamespaceTextRules;
            return this;
        }

        public Builder pageTitleRules(CurationRules<String> pageTitleRules) {
            this.pageTitleRules = pageTitleRules;
            return this;
        }

        public Builder pageRevisionIdRules(ComparableCurationRules<Integer> pageRevisionIdRules) {
            this.pageRevisionIdRules = pageRevisionIdRules;
            return this;
        }

        public Builder pageWikidataIdRules(CurationRules<String> pageWikidataIdRules) {
            this.pageWikidataIdRules = pageWikidataIdRules;
            return this;
        }

        public Builder pageIsRedirectRules(CurationRules<Boolean> pageIsRedirectRules) {
            this.pageIsRedirectRules = pageIsRedirectRules;
            return this;
        }

        public Builder pageContentLanguageRules(CurationRules<String> pageContentLanguageRules) {
            this.pageContentLanguageRules = pageContentLanguageRules;
            return this;
        }

        public Builder pageUserGroupsAllowedToEditRules(
                CollectionCurationRules<String> pageUserGroupsAllowedToEditRules
        ) {
            this.pageUserGroupsAllowedToEditRules = pageUserGroupsAllowedToEditRules;
            return this;
        }

        public Builder pageUserGroupsAllowedToMoveRules(
                CollectionCurationRules<String> pageUserGroupsAllowedToMoveRules
        ) {
            this.pageUserGroupsAllowedToMoveRules = pageUserGroupsAllowedToMoveRules;
            return this;
        }

        public Builder userIdRules(ComparableCurationRules<Integer> userIdRules) {
            this.userIdRules = userIdRules;
            return this;
        }

        public Builder userNameRules(CurationRules<String> userNameRules) {
            this.userNameRules = userNameRules;
            return this;
        }

        public Builder userGroupsRules(CollectionCurationRules<String> userGroupsRules) {
            this.userGroupsRules = userGroupsRules;
            return this;
        }

        public Builder userIsLoggedInRules(CurationRules<Boolean> userIsLoggedInRules) {
            this.userIsLoggedInRules = userIsLoggedInRules;
            return this;
        }

        public Builder userIsBotRules(CurationRules<Boolean> userIsBotRules) {
            this.userIsBotRules = userIsBotRules;
            return this;
        }

        public Builder userCanProbablyEditPageRules(CurationRules<Boolean> userCanProbablyEditPageRules) {
            this.userCanProbablyEditPageRules = userCanProbablyEditPageRules;
            return this;
        }

        public Builder userEditCountRules(ComparableCurationRules<Integer> userEditCountRules) {
            this.userEditCountRules = userEditCountRules;
            return this;
        }

        public Builder userEditCountBucketRules(CurationRules<String> userEditCountBucketRules) {
            this.userEditCountBucketRules = userEditCountBucketRules;
            return this;
        }

        public Builder userRegistrationTimestampRules(ComparableCurationRules<Long> userRegistrationTimestampRules) {
            this.userRegistrationTimestampRules = userRegistrationTimestampRules;
            return this;
        }

        public Builder userLanguageRules(CurationRules<String> userLanguageRules) {
            this.userLanguageRules = userLanguageRules;
            return this;
        }

        public Builder userLanguageVariantRules(CurationRules<String> userLanguageVariantRules) {
            this.userLanguageVariantRules = userLanguageVariantRules;
            return this;
        }

        public Builder devicePixelRatioRules(ComparableCurationRules<Float> devicePixelRatioRules) {
            this.devicePixelRatioRules = devicePixelRatioRules;
            return this;
        }

        public Builder deviceHardwareConcurrencyRules(ComparableCurationRules<Integer> deviceHardwareConcurrencyRules) {
            this.deviceHardwareConcurrencyRules = deviceHardwareConcurrencyRules;
            return this;
        }

        public Builder deviceMaxTouchPointsRules(ComparableCurationRules<Integer> deviceMaxTouchPointsRules) {
            this.deviceMaxTouchPointsRules = deviceMaxTouchPointsRules;
            return this;
        }

        public Builder accessMethodRules(CurationRules<String> accessMethodRules) {
            this.accessMethodRules = accessMethodRules;
            return this;
        }

        public Builder platformRules(CurationRules<String> platformRules) {
            this.platformRules = platformRules;
            return this;
        }

        public Builder platformFamilyRules(CurationRules<String> platformFamilyRules) {
            this.platformFamilyRules = platformFamilyRules;
            return this;
        }

        public Builder isProductionRules(CurationRules<Boolean> isProductionRules) {
            this.isProductionRules = isProductionRules;
            return this;
        }

        public CurationFilter build() {
            return new CurationFilter(this);
        }
    }
}
