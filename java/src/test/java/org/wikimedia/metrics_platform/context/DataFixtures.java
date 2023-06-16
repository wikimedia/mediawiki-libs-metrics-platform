package org.wikimedia.metrics_platform.context;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.wikimedia.metrics_platform.json.GsonHelper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class DataFixtures {

    private DataFixtures() {
        // Utility class, should never be instantiated
    }

    public static ClientData getTestClientData() {
        return new ClientData(
                getTestAgentData(),
                getTestPageData(),
                getTestMediawikiData(),
                getTestPerformerData(),
                "en.wikipedia.org"
        );
    }

    public static ClientData getTestClientData(String expectedEvent) {
        Map<String, Object> dataMap = new HashMap<>();

        JsonElement jsonElement = JsonParser.parseString(expectedEvent);
        JsonObject expectedEventJson = jsonElement.isJsonArray() ? jsonElement.getAsJsonArray().get(0).getAsJsonObject() : jsonElement.getAsJsonObject();

        Set<String> dataObjectNames = Stream.of("agent", "page", "mediawiki", "performer")
                .collect(Collectors.toCollection(HashSet::new));

        for (String dataObjectName : dataObjectNames) {
            JsonObject metaData = expectedEventJson.getAsJsonObject(dataObjectName);
            Set<String> keys = metaData.keySet();
            Map<String, Object> dataMapEach = new HashMap<>();
            for (String key : keys) {
                dataMapEach.put(key, metaData.get(key));
            }
            dataMap.put(dataObjectName, dataMapEach);
        }

        Gson gson = GsonHelper.getGson();
        JsonElement jsonClientData = gson.toJsonTree(dataMap);
        return gson.fromJson(jsonClientData, ClientData.class);
    }

    public static AgentData getTestAgentData() {
        return AgentData.builder()
                .appInstallId("ffffffff-ffff-ffff-ffff-ffffffffffff")
                .clientPlatform("android")
                .clientPlatformFamily("app")
                .build();
    }

    public static PageData getTestPageData() {
        return PageData.builder()
                .id(1)
                .title("Test Page Title")
                .namespace(0)
                .namespaceName("Main")
                .revisionId(1L)
                .wikidataItemQid("Q123456")
                .contentLanguage("en")
                .isRedirect(false)
                .groupsAllowedToMove(Collections.singleton("*"))
                .groupsAllowedToEdit(Collections.singleton("*"))
                .build();
    }

    public static MediawikiData getTestMediawikiData() {
        return MediawikiData.builder()
                .skin("vector")
                .version("1.40.0-wmf.20")
                .isProduction(true)
                .isDebugMode(false)
                .database("enwiki")
                .siteContentLanguage("en")
                .siteContentLanguageVariant("en-zh")
                .build();
    }

    public static PerformerData getTestPerformerData() {
        return PerformerData.builder()
                .id(1)
                .name("TestPerformer")
                .isLoggedIn(true)
                .sessionId("eeeeeeeeeeeeeeeeeeee")
                .pageviewId("eeeeeeeeeeeeeeeeeeee")
                .groups(Collections.singletonList("*"))
                .isBot(false)
                .language("zh")
                .languageVariant("zh-tw")
                .canProbablyEditPage(true)
                .editCount(10)
                .editCountBucket("5-99 edits")
                .registrationDt(Instant.parse("2023-03-01T01:08:30Z"))
                .build();
    }
}
