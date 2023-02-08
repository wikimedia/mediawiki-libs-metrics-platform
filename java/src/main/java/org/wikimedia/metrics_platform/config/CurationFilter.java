package org.wikimedia.metrics_platform.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.curation.CollectionCurationRules;
import org.wikimedia.metrics_platform.curation.ComparableCurationRules;
import org.wikimedia.metrics_platform.curation.CurationRules;
import org.wikimedia.metrics_platform.event.Event;

import com.google.gson.annotations.SerializedName;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder @AllArgsConstructor @EqualsAndHashCode @ToString
@ParametersAreNullableByDefault
public class CurationFilter {
    @SerializedName("agent_app_install_id") private CurationRules<String> agentAppInstallIdRules;
    @SerializedName("agent_client_platform") private CurationRules<String> agentClientPlatformRules;
    @SerializedName("agent_client_platform_family") private CurationRules<String> agentClientPlatformFamilyRules;

    @SerializedName("mediawiki_skin") private CurationRules<String> mediawikiSkin;
    @SerializedName("mediawiki_version") private CurationRules<String> mediawikiVersion;
    @SerializedName("mediawiki_is_production") private CurationRules<Boolean> mediawikiIsProduction;
    @SerializedName("mediawiki_is_debug_mode") private CurationRules<Boolean> mediawikiIsDebugMode;
    @SerializedName("mediawiki_database") private CurationRules<String> mediawikiDatabase;
    @SerializedName("mediawiki_site_content_language") private CurationRules<String> mediawikiSiteContentLanguage;
    @SerializedName("mediawiki_site_content_language_variant") private CurationRules<String> mediawikiSiteContentLanguageVariant;

    @SerializedName("page_id") private ComparableCurationRules<Integer> pageIdRules;
    @SerializedName("page_namespace") private ComparableCurationRules<Integer> pageNamespaceRules;
    @SerializedName("page_namespace_name") private CurationRules<String> pageNamespaceNameRules;
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
    @SerializedName("performer_session_id") private CurationRules<String> performerSessionIdRules;
    @SerializedName("performer_pageview_id") private CurationRules<String> performerPageviewIdRules;
    @SerializedName("performer_groups") private CollectionCurationRules<String> performerGroupsRules;
    @SerializedName("performer_is_logged_in") private CurationRules<Boolean> performerIsLoggedInRules;
    @SerializedName("performer_is_bot") private CurationRules<Boolean> performerIsBotRules;
    @SerializedName("performer_can_probably_edit_page") private CurationRules<Boolean> performerCanProbablyEditPageRules;
    @SerializedName("performer_edit_count") private ComparableCurationRules<Integer> performerEditCountRules;
    @SerializedName("performer_edit_count_bucket") private CurationRules<String> performerEditCountBucketRules;
    @SerializedName("performer_registration_dt") private ComparableCurationRules<Long> performerRegistrationDtRules;
    @SerializedName("performer_language") private CurationRules<String> performerLanguageRules;
    @SerializedName("performer_language_variant") private CurationRules<String> performerLanguageVariantRules;

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY", justification = "TODO: needs to be refactored!")
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:NPathComplexity"})
    public boolean apply(@Nonnull Event event) {
        // Agent

        if (agentAppInstallIdRules != null) {
            if (event.getAgentData().getAppInstallId() == null) {
                return false;
            }
            if (!agentAppInstallIdRules.apply(event.getAgentData().getAppInstallId())) {
                return false;
            }
        }
        if (agentClientPlatformRules != null) {
            if (event.getAgentData().getClientPlatform() == null) {
                return false;
            }
            if (!agentClientPlatformRules.apply(event.getAgentData().getClientPlatform())) {
                return false;
            }
        }
        if (agentClientPlatformFamilyRules != null) {
            if (event.getAgentData().getClientPlatformFamily() == null) {
                return false;
            }
            if (!agentClientPlatformFamilyRules.apply(event.getAgentData().getClientPlatformFamily())) {
                return false;
            }
        }

        // Mediawiki

        if (mediawikiSkin != null) {
            if (event.getMediawikiData().getSkin() == null) {
                return false;
            }
            if (!mediawikiSkin.apply(event.getMediawikiData().getSkin())) {
                return false;
            }
        }
        if (mediawikiVersion != null) {
            if (event.getMediawikiData().getVersion() == null) {
                return false;
            }
            if (!mediawikiVersion.apply(event.getMediawikiData().getVersion())) {
                return false;
            }
        }
        if (mediawikiIsProduction != null) {
            if (event.getMediawikiData().getIsProduction() == null) {
                return false;
            }
            if (!mediawikiIsProduction.apply(event.getMediawikiData().getIsProduction())) {
                return false;
            }
        }
        if (mediawikiIsDebugMode != null) {
            if (event.getMediawikiData().getIsProduction() == null) {
                return false;
            }
            if (!mediawikiIsDebugMode.apply(event.getMediawikiData().getIsDebugMode())) {
                return false;
            }
        }
        if (mediawikiVersion != null) {
            if (event.getMediawikiData().getVersion() == null) {
                return false;
            }
            if (!mediawikiVersion.apply(event.getMediawikiData().getVersion())) {
                return false;
            }
        }
        if (mediawikiDatabase != null) {
            if (event.getMediawikiData().getDatabase() == null) {
                return false;
            }
            if (!mediawikiDatabase.apply(event.getMediawikiData().getDatabase())) {
                return false;
            }
        }
        if (mediawikiSiteContentLanguage != null) {
            if (event.getMediawikiData().getSiteContentLanguage() == null) {
                return false;
            }
            if (!mediawikiSiteContentLanguage.apply(event.getMediawikiData().getSiteContentLanguage())) {
                return false;
            }
        }
        if (mediawikiSiteContentLanguageVariant != null) {
            if (event.getMediawikiData().getSiteContentLanguageVariant() == null) {
                return false;
            }
            if (!mediawikiSiteContentLanguageVariant.apply(event.getMediawikiData().getSiteContentLanguageVariant())) {
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
        if (pageNamespaceRules != null) {
            if (event.getPageData().getNamespace() == null) {
                return false;
            }
            if (!pageNamespaceRules.apply(event.getPageData().getNamespace())) {
                return false;
            }
        }
        if (pageNamespaceNameRules != null) {
            if (event.getPageData().getNamespaceName() == null) {
                return false;
            }
            if (!pageNamespaceNameRules.apply(event.getPageData().getNamespaceName())) {
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
        if (performerSessionIdRules != null) {
            if (event.getPerformerData().getSessionId() == null) {
                return false;
            }
            if (!performerSessionIdRules.apply(event.getPerformerData().getSessionId())) {
                return false;
            }
        }
        if (performerPageviewIdRules != null) {
            if (event.getPerformerData().getPageviewId() == null) {
                return false;
            }
            if (!performerPageviewIdRules.apply(event.getPerformerData().getPageviewId())) {
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

        return true;
    }
}
