package org.wikimedia.metrics_platform.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class PageDataFixtures {

    private PageDataFixtures() {
        // Utility class, should never be instantiated
    }

    /**
     * Convenience method for adding test page context metadata from string.
     */
    public static PageData getTestPageData(String expectedEvent) {
        Map<String, Object> pageDataMap = new HashMap<>();

        JsonObject expectedEventJson = JsonParser.parseString(expectedEvent).getAsJsonObject();
        JsonObject pageMetadata = expectedEventJson.getAsJsonObject("page");
        Set<String> keys = pageMetadata.keySet();

        for (String key : keys) {
            pageDataMap.put(key, pageMetadata.get(key));
        }
        return getTestPageData(pageDataMap);
    }

    /**
     * Convenience method for adding test page context metadata from map.
     */
    public static PageData getTestPageData(Map<String, Object> expectedEvent) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(expectedEvent);
        return gson.fromJson(jsonElement, PageData.class);
    }

    /**
     * Convenience method for adding test page data.
     */
    public static PageData getTestPageData() {
        Map<String, Object> pageData = new HashMap<>();

        pageData.put("id", 1);
        pageData.put("title", "Test Page Title");
        pageData.put("namespace", 0);
        pageData.put("namespace_name", "");
        pageData.put("is_redirect", false);
        pageData.put("revision_id", 1);
        pageData.put("wikidata_qid", "Q1");
        pageData.put("content_language", "zh");
        pageData.put("user_groups_allowed_to_move", Collections.emptySet());
        pageData.put("user_groups_allowed_to_edit", Collections.emptySet());

        return getTestPageData(pageData);
    }
}
