package org.wikimedia.metrics_platform.curation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.curation.rules.CollectionCurationRules;
import org.wikimedia.metrics_platform.curation.rules.ComparableCurationRules;
import org.wikimedia.metrics_platform.curation.rules.CurationRules;

import com.google.gson.annotations.SerializedName;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder @AllArgsConstructor @EqualsAndHashCode @ToString
@ParametersAreNullableByDefault
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

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY", justification = "TODO: needs to be refactored!")
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:NPathComplexity"})
    public boolean apply(@Nonnull Event event) {
        // Page

        if (pageIdRules != null) {
            if (event.getPageData().getId() == null) {
                return false;
            }
            if (!pageIdRules.apply(event.getPageData().getId())) {
                return false;
            }
        }
        if (pageNamespaceIdRules != null) {
            if (event.getPageData().getNamespaceId() == null) {
                return false;
            }
            if (!pageNamespaceIdRules.apply(event.getPageData().getNamespaceId())) {
                return false;
            }
        }
        if (pageNamespaceTextRules != null) {
            if (event.getPageData().getNamespaceText() == null) {
                return false;
            }
            if (!pageNamespaceTextRules.apply(event.getPageData().getNamespaceText())) {
                return false;
            }
        }
        if (pageTitleRules != null) {
            if (event.getPageData().getTitle() == null) {
                return false;
            }
            if (!pageTitleRules.apply(event.getPageData().getTitle())) {
                return false;
            }
        }
        if (pageRevisionIdRules != null) {
            if (event.getPageData().getRevisionId() == null) {
                return false;
            }
            if (!pageRevisionIdRules.apply(event.getPageData().getRevisionId())) {
                return false;
            }
        }
        if (pageWikidataIdRules != null) {
            if (event.getPageData().getWikidataItemId() == null) {
                return false;
            }
            if (!pageWikidataIdRules.apply(event.getPageData().getWikidataItemId())) {
                return false;
            }
        }
        if (pageIsRedirectRules != null) {
            if (event.getPageData().getIsRedirect() == null) {
                return false;
            }
            if (!pageIsRedirectRules.apply(event.getPageData().getIsRedirect())) {
                return false;
            }
        }
        if (pageContentLanguageRules != null) {
            if (event.getPageData().getContentLanguage() == null) {
                return false;
            }
            if (!pageContentLanguageRules.apply(event.getPageData().getContentLanguage())) {
                return false;
            }
        }
        if (pageUserGroupsAllowedToEditRules != null) {
            if (event.getPageData().getGroupsAllowedToEdit() == null) {
                return false;
            }
            if (!pageUserGroupsAllowedToEditRules.apply(event.getPageData().getGroupsAllowedToEdit())) {
                return false;
            }
        }
        if (pageUserGroupsAllowedToMoveRules != null) {
            if (event.getPageData().getGroupsAllowedToMove() == null) {
                return false;
            }
            if (!pageUserGroupsAllowedToMoveRules.apply(event.getPageData().getGroupsAllowedToMove())) {
                return false;
            }
        }

        // User

        if (userIdRules != null) {
            if (event.getUserData().getId() == null) {
                return false;
            }
            if (!userIdRules.apply(event.getUserData().getId())) {
                return false;
            }
        }
        if (userNameRules != null) {
            if (event.getUserData().getName() == null) {
                return false;
            }
            if (!userNameRules.apply(event.getUserData().getName())) {
                return false;
            }
        }
        if (userGroupsRules != null) {
            if (event.getUserData().getGroups() == null) {
                return false;
            }
            if (!userGroupsRules.apply(event.getUserData().getGroups())) {
                return false;
            }
        }
        if (userIsLoggedInRules != null) {
            if (event.getUserData().getIsLoggedIn() == null) {
                return false;
            }
            if (!userIsLoggedInRules.apply(event.getUserData().getIsLoggedIn())) {
                return false;
            }
        }
        if (userIsBotRules != null) {
            if (event.getUserData().getIsBot() == null) {
                return false;
            }
            if (!userIsBotRules.apply(event.getUserData().getIsBot())) {
                return false;
            }
        }
        if (userCanProbablyEditPageRules != null) {
            if (event.getUserData().getCanProbablyEditPage() == null) {
                return false;
            }
            if (!userCanProbablyEditPageRules.apply(event.getUserData().getCanProbablyEditPage())) {
                return false;
            }
        }
        if (userEditCountRules != null) {
            if (event.getUserData().getEditCount() == null) {
                return false;
            }
            if (!userEditCountRules.apply(event.getUserData().getEditCount())) {
                return false;
            }
        }
        if (userEditCountBucketRules != null) {
            if (event.getUserData().getEditCountBucket() == null) {
                return false;
            }
            if (!userEditCountBucketRules.apply(event.getUserData().getEditCountBucket())) {
                return false;
            }
        }
        if (userRegistrationTimestampRules != null) {
            if (event.getUserData().getRegistrationTimestamp() == null) {
                return false;
            }
            if (!userRegistrationTimestampRules.apply(event.getUserData().getRegistrationTimestamp())) {
                return false;
            }
        }
        if (userLanguageRules != null) {
            if (event.getUserData().getLanguage() == null) {
                return false;
            }
            if (!userLanguageRules.apply(event.getUserData().getLanguage())) {
                return false;
            }
        }
        if (userLanguageVariantRules != null) {
            if (event.getUserData().getLanguageVariant() == null) {
                return false;
            }
            if (!userLanguageVariantRules.apply(event.getUserData().getLanguageVariant())) {
                return false;
            }
        }

        // Device

        if (devicePixelRatioRules != null) {
            if (event.getDeviceData().getPixelRatio() == null) {
                return false;
            }
            if (!devicePixelRatioRules.apply(event.getDeviceData().getPixelRatio())) {
                return false;
            }
        }
        if (deviceHardwareConcurrencyRules != null) {
            if (event.getDeviceData().getHardwareConcurrency() == null) {
                return false;
            }
            if (!deviceHardwareConcurrencyRules.apply(event.getDeviceData().getHardwareConcurrency())) {
                return false;
            }
        }
        if (deviceMaxTouchPointsRules != null) {
            if (event.getDeviceData().getMaxTouchPoints() == null) {
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
            if (event.getIsProduction() == null) {
                return false;
            }
            if (!isProductionRules.apply(event.getIsProduction())) {
                return false;
            }
        }
        return true;
    }
}
