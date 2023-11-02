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

    @SerializedName("mediawiki_database") CurationRules<String> mediawikiDatabase;

    @SerializedName("page_id") ComparableCurationRules<Integer> pageIdRules;
    @SerializedName("page_namespace_id") ComparableCurationRules<Integer> pageNamespaceIdRules;
    @SerializedName("page_namespace_name") CurationRules<String> pageNamespaceNameRules;
    @SerializedName("page_title") CurationRules<String> pageTitleRules;
    @SerializedName("page_revision_id") ComparableCurationRules<Long> pageRevisionIdRules;
    @SerializedName("page_wikidata_qid") CurationRules<String> pageWikidataQidRules;
    @SerializedName("page_content_language") CurationRules<String> pageContentLanguageRules;

    @SerializedName("performer_id") ComparableCurationRules<Integer> performerIdRules;
    @SerializedName("performer_name") CurationRules<String> performerNameRules;
    @SerializedName("performer_session_id") CurationRules<String> performerSessionIdRules;
    @SerializedName("performer_pageview_id") CurationRules<String> performerPageviewIdRules;
    @SerializedName("performer_groups") CollectionCurationRules<String> performerGroupsRules;
    @SerializedName("performer_is_logged_in") CurationRules<Boolean> performerIsLoggedInRules;
    @SerializedName("performer_is_temp") CurationRules<Boolean> performerIsTempRules;
    @SerializedName("performer_registration_dt") ComparableCurationRules<Instant> performerRegistrationDtRules;
    @SerializedName("performer_language_groups") CurationRules<String> performerLanguageGroupsRules;
    @SerializedName("performer_language_primary") CurationRules<String> performerLanguagePrimaryRules;
}
