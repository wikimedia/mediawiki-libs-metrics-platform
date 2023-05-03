package org.wikimedia.metrics_platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.wikimedia.metrics_platform.context.AgentData;
import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.MediawikiData;
import org.wikimedia.metrics_platform.context.PerformerData;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConsistencyITClientData extends ClientData {
    public JsonObject agentJson;
    public JsonObject pageJson;
    public JsonObject mediawikiJson;
    public JsonObject performerJson;
    public String hostname;

    public ConsistencyITClientData(
            JsonObject agent,
            JsonObject page,
            JsonObject mediawiki,
            JsonObject performer,
            String hostname
    ) {
        this.agentJson = agent;
        this.pageJson = page;
        this.mediawikiJson = mediawiki;
        this.performerJson = performer;
        this.hostname = hostname;

        AgentData agentData = AgentData.builder()
                .appInstallId(this.agentJson.get("app_install_id").getAsString())
                .clientPlatform(this.agentJson.get("client_platform").getAsString())
                .clientPlatformFamily(this.agentJson.get("client_platform_family").getAsString())
                .build();
        MediawikiData mediawikiData = MediawikiData.builder()
                .skin(this.mediawikiJson.get("skin").getAsString())
                .version(this.mediawikiJson.get("version").getAsString())
                .isProduction(this.mediawikiJson.get("is_production").getAsBoolean())
                .isDebugMode(this.mediawikiJson.get("is_debug_mode").getAsBoolean())
                .database(this.mediawikiJson.get("database").getAsString())
                .siteContentLanguage(this.mediawikiJson.get("site_content_language").getAsString())
                .build();
        PerformerData performerData = PerformerData.builder()
                .id(this.performerJson.get("id").getAsInt())
                .isLoggedIn(this.performerJson.get("is_logged_in").getAsBoolean())
                .sessionId(this.performerJson.get("session_id").getAsString())
                .pageviewId(this.performerJson.get("pageview_id").getAsString())
                .groups(Collections.singleton(this.performerJson.get("groups").getAsString()))
                .isBot(this.performerJson.get("is_bot").getAsBoolean())
                .language(this.performerJson.get("language").getAsString())
                .canProbablyEditPage(this.performerJson.get("can_probably_edit_page").getAsBoolean())
                .build();

        this.setAgentData(agentData);
        this.setMediawikiData(mediawikiData);
        this.setPerformerData(performerData);
        this.setDomain(this.hostname);
    }

    public static ConsistencyITClientData createConsistencyTestClientData() {
        try {
            JsonObject data = getIntegrationData();
            JsonObject agent = data.getAsJsonObject("agent");
            JsonObject page = data.getAsJsonObject("page");
            JsonObject mediawiki = data.getAsJsonObject("mediawiki");
            JsonObject performer = data.getAsJsonObject("performer");
            String hostname = data.get("hostname").getAsString();

            return new ConsistencyITClientData(
                    agent,
                    page,
                    mediawiki,
                    performer,
                    hostname
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonObject getIntegrationData() throws IOException {
        Path pathIntegration = Paths.get("../tests/consistency/integration_data.json");
        try (BufferedReader reader = Files.newBufferedReader(pathIntegration)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return jsonElement.getAsJsonObject();
        }
    }
}
