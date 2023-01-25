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
    @SerializedName("app_install_id") private CurationRules<String> appInstallIdRules;
    @SerializedName("client_platform") private CurationRules<String> clientPlatformRules;
    @SerializedName("client_platform_family") private CurationRules<String> clientPlatformFamilyRules;
    @SerializedName("page_id") private ComparableCurationRules<Integer> pageIdRules;
    @SerializedName("page_namespace_id") private ComparableCurationRules<Integer> pageNamespaceIdRules;
    @SerializedName("page_namespace_text") private CurationRules<String> pageNamespaceTextRules;
    @SerializedName("page_title") private CurationRules<String> pageTitleRules;
    @SerializedName("page_revision_id") private ComparableCurationRules<Integer> pageRevisionIdRules;
    @SerializedName("page_wikidata_id") private CurationRules<String> pageWikidataIdRules;
    @SerializedName("page_is_redirect") private CurationRules<Boolean> pageIsRedirectRules;
    @SerializedName("page_content_language") private CurationRules<String> pageContentLanguageRules;
    @SerializedName("page_performer_groups_allowed_to_edit")
    private CollectionCurationRules<String> pageUserGroupsAllowedToEditRules;
    @SerializedName("page_performer_groups_allowed_to_move")
    private CollectionCurationRules<String> pageUserGroupsAllowedToMoveRules;

    @SerializedName("performer_id") private ComparableCurationRules<Integer> performerIdRules;
    @SerializedName("performer_name") private CurationRules<String> performerNameRules;
    @SerializedName("performer_groups") private CollectionCurationRules<String> performerGroupsRules;
    @SerializedName("performer_is_logged_in") private CurationRules<Boolean> performerIsLoggedInRules;
    @SerializedName("performer_is_bot") private CurationRules<Boolean> performerIsBotRules;
    @SerializedName("performer_can_probably_edit_page") private CurationRules<Boolean> performerCanProbablyEditPageRules;
    @SerializedName("performer_edit_count") private ComparableCurationRules<Integer> performerEditCountRules;
    @SerializedName("performer_edit_count_bucket") private CurationRules<String> performerEditCountBucketRules;
    @SerializedName("performer_registration_dt") private ComparableCurationRules<Long> performerRegistrationDtRules;
    @SerializedName("performer_language") private CurationRules<String> performerLanguageRules;
    @SerializedName("performer_language_variant") private CurationRules<String> performerLanguageVariantRules;

    @SerializedName("device_pixel_ratio") private ComparableCurationRules<Float> devicePixelRatioRules;
    @SerializedName("device_hardware_concurrency")
    private ComparableCurationRules<Integer> deviceHardwareConcurrencyRules;
    @SerializedName("device_max_touch_points") private ComparableCurationRules<Integer> deviceMaxTouchPointsRules;

    @SerializedName("access_method") private CurationRules<String> accessMethodRules;
    private CurationRules<String> platformRules;
    @SerializedName("is_production") private CurationRules<Boolean> isProductionRules;

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY", justification = "TODO: needs to be refactored!")
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:NPathComplexity"})
    public boolean apply(@Nonnull Event event) {
        // Agent

        if (appInstallIdRules != null) {
            if (event.getAgentData().getAppInstallId() == null) {
                return false;
            }
            if (!appInstallIdRules.apply(event.getAgentData().getAppInstallId())) {
                return false;
            }
        }
        if (clientPlatformRules != null) {
            if (event.getAgentData().getClientPlatform() == null) {
                return false;
            }
            if (!clientPlatformRules.apply(event.getAgentData().getClientPlatform())) {
                return false;
            }
        }
        if (clientPlatformFamilyRules != null) {
            if (event.getAgentData().getClientPlatformFamily() == null) {
                return false;
            }
            if (!clientPlatformFamilyRules.apply(event.getAgentData().getClientPlatformFamily())) {
                return false;
            }
        }


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

        // Performer

        if (performerIdRules != null) {
            if (event.getPerformerData().getId() == null) {
                return false;
            }
            if (!performerIdRules.apply(event.getPerformerData().getId())) {
                return false;
            }
        }
        if (performerNameRules != null) {
            if (event.getPerformerData().getName() == null) {
                return false;
            }
            if (!performerNameRules.apply(event.getPerformerData().getName())) {
                return false;
            }
        }
        if (performerGroupsRules != null) {
            if (event.getPerformerData().getGroups() == null) {
                return false;
            }
            if (!performerGroupsRules.apply(event.getPerformerData().getGroups())) {
                return false;
            }
        }
        if (performerIsLoggedInRules != null) {
            if (event.getPerformerData().getIsLoggedIn() == null) {
                return false;
            }
            if (!performerIsLoggedInRules.apply(event.getPerformerData().getIsLoggedIn())) {
                return false;
            }
        }
        if (performerIsBotRules != null) {
            if (event.getPerformerData().getIsBot() == null) {
                return false;
            }
            if (!performerIsBotRules.apply(event.getPerformerData().getIsBot())) {
                return false;
            }
        }
        if (performerCanProbablyEditPageRules != null) {
            if (event.getPerformerData().getCanProbablyEditPage() == null) {
                return false;
            }
            if (!performerCanProbablyEditPageRules.apply(event.getPerformerData().getCanProbablyEditPage())) {
                return false;
            }
        }
        if (performerEditCountRules != null) {
            if (event.getPerformerData().getEditCount() == null) {
                return false;
            }
            if (!performerEditCountRules.apply(event.getPerformerData().getEditCount())) {
                return false;
            }
        }
        if (performerEditCountBucketRules != null) {
            if (event.getPerformerData().getEditCountBucket() == null) {
                return false;
            }
            if (!performerEditCountBucketRules.apply(event.getPerformerData().getEditCountBucket())) {
                return false;
            }
        }
        if (performerRegistrationDtRules != null) {
            if (event.getPerformerData().getRegistrationDt() == null) {
                return false;
            }
            if (!performerRegistrationDtRules.apply(event.getPerformerData().getRegistrationDt())) {
                return false;
            }
        }
        if (performerLanguageRules != null) {
            if (event.getPerformerData().getLanguage() == null) {
                return false;
            }
            if (!performerLanguageRules.apply(event.getPerformerData().getLanguage())) {
                return false;
            }
        }
        if (performerLanguageVariantRules != null) {
            if (event.getPerformerData().getLanguageVariant() == null) {
                return false;
            }
            if (!performerLanguageVariantRules.apply(event.getPerformerData().getLanguageVariant())) {
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
