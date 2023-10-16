package org.wikimedia.metrics_platform.config;

import java.time.Instant;

import javax.annotation.ParametersAreNullableByDefault;

import org.wikimedia.metrics_platform.config.curation.CollectionCurationRules;
import org.wikimedia.metrics_platform.config.curation.ComparableCurationRules;
import org.wikimedia.metrics_platform.config.curation.CurationRules;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder @AllArgsConstructor @Value
@ParametersAreNullableByDefault
public class CurationFilter {
    @SerializedName("agent_app_install_id") CurationRules<String> agentAppInstallIdRules;
    @SerializedName("agent_client_platform") CurationRules<String> agentClientPlatformRules;
    @SerializedName("agent_client_platform_family") CurationRules<String> agentClientPlatformFamilyRules;

    @SerializedName("mediawiki_skin") CurationRules<String> mediawikiSkin;
    @SerializedName("mediawiki_version") CurationRules<String> mediawikiVersion;
    @SerializedName("mediawiki_is_production") CurationRules<Boolean> mediawikiIsProduction;
    @SerializedName("mediawiki_is_debug_mode") CurationRules<Boolean> mediawikiIsDebugMode;
    @SerializedName("mediawiki_database") CurationRules<String> mediawikiDatabase;
    @SerializedName("mediawiki_site_content_language") CurationRules<String> mediawikiSiteContentLanguage;
    @SerializedName("mediawiki_site_content_language_variant") CurationRules<String> mediawikiSiteContentLanguageVariant;

    @SerializedName("page_id") ComparableCurationRules<Integer> pageIdRules;
    @SerializedName("page_namespace") ComparableCurationRules<Integer> pageNamespaceRules;
    @SerializedName("page_namespace_name") CurationRules<String> pageNamespaceNameRules;
    @SerializedName("page_title") CurationRules<String> pageTitleRules;
    @SerializedName("page_revision_id") ComparableCurationRules<Long> pageRevisionIdRules;
    @SerializedName("page_wikidata_qid") CurationRules<String> pageWikidataQidRules;
    @SerializedName("page_is_redirect") CurationRules<Boolean> pageIsRedirectRules;
    @SerializedName("page_content_language") CurationRules<String> pageContentLanguageRules;
    @SerializedName("page_performer_groups_allowed_to_edit") CollectionCurationRules<String> pageUserGroupsAllowedToEditRules;
    @SerializedName("page_performer_groups_allowed_to_move") CollectionCurationRules<String> pageUserGroupsAllowedToMoveRules;

    @SerializedName("performer_id") ComparableCurationRules<Integer> performerIdRules;
    @SerializedName("performer_name") CurationRules<String> performerNameRules;
    @SerializedName("performer_session_id") CurationRules<String> performerSessionIdRules;
    @SerializedName("performer_pageview_id") CurationRules<String> performerPageviewIdRules;
    @SerializedName("performer_groups") CollectionCurationRules<String> performerGroupsRules;
    @SerializedName("performer_is_logged_in") CurationRules<Boolean> performerIsLoggedInRules;
    @SerializedName("performer_is_bot") CurationRules<Boolean> performerIsBotRules;
    @SerializedName("performer_can_probably_edit_page") CurationRules<Boolean> performerCanProbablyEditPageRules;
    @SerializedName("performer_edit_count") ComparableCurationRules<Integer> performerEditCountRules;
    @SerializedName("performer_edit_count_bucket") CurationRules<String> performerEditCountBucketRules;
    @SerializedName("performer_registration_dt") ComparableCurationRules<Instant> performerRegistrationDtRules;
    @SerializedName("performer_language") CurationRules<String> performerLanguageRules;
    @SerializedName("performer_language_variant") CurationRules<String> performerLanguageVariantRules;
}
