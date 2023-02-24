package org.wikimedia.metrics_platform;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConsistencyITClientMetadata implements ClientMetadata {
    public JsonObject agent;
    public JsonObject page;
    public JsonObject mediawiki;
    public JsonObject performer;

    public ConsistencyITClientMetadata(
            JsonObject agent,
            JsonObject page,
            JsonObject mediawiki,
            JsonObject performer
    ) {
        this.agent = agent;
        this.page = page;
        this.mediawiki = mediawiki;
        this.performer = performer;
    }

    public static ConsistencyITClientMetadata createConsistencyTestClientMetadata() {
        try {
            JsonObject data = getIntegrationData();
            JsonObject agent = data.getAsJsonObject("agent");
            JsonObject page = data.getAsJsonObject("page");
            JsonObject mediawiki = data.getAsJsonObject("mediawiki");
            JsonObject performer = data.getAsJsonObject("performer");

            return new ConsistencyITClientMetadata(
                 agent,
                 page,
                 mediawiki,
                 performer
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Agent

    @Override
    public String getAgentAppInstallId() {
        return agent.get("app_install_id").getAsString();
    }

    public String getAgentClientPlatform() {
        return agent.get("client_platform").getAsString();
    }

    public String getAgentClientPlatformFamily() {
        return agent.get("client_platform_family").getAsString();
    }

    // Page

    @Override
    public Integer getPageId() {
        return page.get("id").getAsInt(); }

    @Override
    public String getPageTitle() {
        return page.get("title").getAsString();
    }

    @Override
    public Integer getPageNamespace() {
        return page.get("namespace").getAsInt(); }

    @Override
    public String getPageNamespaceName() {
        return page.get("namespace_name").getAsString();
    }

    @Override
    public Integer getPageRevisionId() {
        return page.get("revision_id").getAsInt(); }

    @Override
    public String getPageWikidataItemQid() {
        return page.get("wikidata_qid").getAsString(); }

    @Override
    public String getPageContentLanguage() {
        return page.get("content_language").getAsString();
    }

    @Override
    public Boolean getPageIsRedirect() {
        return page.get("is_redirect").getAsBoolean(); }

    @Override
    public Collection<String> getPageUserGroupsAllowedToEdit() {
        JsonArray array = page.getAsJsonArray("user_groups_allowed_to_edit");
        return getArrayData(array);
    }

    @Override
    public Collection<String> getPageUserGroupsAllowedToMove() {
        JsonArray array = page.getAsJsonArray("user_groups_allowed_to_move");
        return getArrayData(array);
    }

    // Mediawiki

    @Override
    public String getMediawikiSkin() {
        return mediawiki.get("skin").getAsString();
    }

    @Override
    public String getMediawikiVersion() {
        return mediawiki.get("version").getAsString();
    }

    @Override
    public Boolean getMediawikiIsProduction() {
        return mediawiki.get("is_production").getAsBoolean();
    }

    @Override
    public Boolean getMediawikiIsDebugMode() {
        return mediawiki.get("is_debug_mode").getAsBoolean();
    }

    @Override
    public String getMediawikiDatabase() {
        return mediawiki.get("database").getAsString();
    }

    @Override
    public String getMediawikiSiteContentLanguage() {
        return mediawiki.has("site_content_language") ? mediawiki.get("site_content_language").getAsString() : null;
    }

    @Override
    public String getMediawikiSiteContentLanguageVariant() {
        return mediawiki.has("site_content_language_variant") ? mediawiki.get("site_content_language_variant").getAsString() : null;
    }

    // Performer

    @Override
    public Integer getPerformerId() {
        return performer.get("id").getAsInt();
    }

    @Override
    public String getPerformerName() {
        return performer.has("name") ? performer.get("name").getAsString() : null;
    }

    @Override
    public Boolean getPerformerIsLoggedIn() {
        return performer.get("is_logged_in").getAsBoolean();
    }

    @Override
    public String getPerformerSessionId() {
        return performer.get("session_id").getAsString();
    }

    @Override
    public String getPerformerPageviewId() {
        return performer.get("pageview_id").getAsString();
    }

    @Override
    public Collection<String> getPerformerGroups() {
        JsonArray array = performer.getAsJsonArray("groups");
        return getArrayData(array);
    }

    @Override
    public Boolean getPerformerIsBot() {
        return performer.get("is_bot").getAsBoolean();
    }

    @Override
    public String getPerformerLanguage() {
        return performer.get("language").getAsString();
    }

    @Override
    public String getPerformerLanguageVariant() {
        return performer.has("language_variant") ? performer.get("language_variant").getAsString() : null;
    }

    @Override
    public Boolean getPerformerCanProbablyEditPage() {
        return performer.has("can_probably_edit_page") && performer.get("can_probably_edit_page").getAsBoolean();
    }

    @Override
    public Integer getPerformerEditCount() {
        return performer.has("edit_count") ? performer.get("edit_count").getAsInt() : null;
    }

    @Override
    public String getPerformerEditCountBucket() {
        return performer.has("edit_count_bucket") ? performer.get("edit_count_bucket").getAsString() : null;
    }

    @Override
    public Instant getPerformerRegistrationDt() {
        Clock clock = Clock.systemUTC();
        return performer.has("registration_dt") ?
                Instant.parse(performer.get("registration_dt").getAsString()) : Instant.now(clock);
    }

    @Override
    public String getDomain() {
        return "en.wikipedia.org";
    }

    private static JsonObject getIntegrationData() throws IOException {
        Path pathIntegration = Paths.get("../tests/consistency/integration_data.json");
        try (BufferedReader reader = Files.newBufferedReader(pathIntegration)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return jsonElement.getAsJsonObject();
        }
    }

    private static Collection<String> getArrayData(JsonArray array) {
        return StreamSupport.stream(array.spliterator(), false)
                .map(JsonElement::getAsString)
                .collect(toList());
    }
}
