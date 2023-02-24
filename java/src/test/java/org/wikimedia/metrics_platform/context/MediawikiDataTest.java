package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.GsonHelper;

import com.google.gson.Gson;

public class MediawikiDataTest {
    @Test
    void testMediawikiData() {
        MediawikiData mediawikiData = MediawikiData.builder()
                .skin("vector")
                .version("1.40.0-wmf.20")
                .isProduction(true)
                .isDebugMode(false)
                .database("enwiki")
                .siteContentLanguage("en")
                .siteContentLanguageVariant("en-zh")
                .build();

        assertThat(mediawikiData.getSkin()).isEqualTo("vector");
        assertThat(mediawikiData.getVersion()).isEqualTo("1.40.0-wmf.20");
        assertThat(mediawikiData.getIsProduction()).isTrue();
        assertThat(mediawikiData.getIsDebugMode()).isFalse();
        assertThat(mediawikiData.getDatabase()).isEqualTo("enwiki");
        assertThat(mediawikiData.getSiteContentLanguage()).isEqualTo("en");
        assertThat(mediawikiData.getSiteContentLanguageVariant()).isEqualTo("en-zh");

        Gson gson = GsonHelper.getGson();
        String json = gson.toJson(mediawikiData);
        assertThat(json).isEqualTo("{" +
                "\"skin\":\"vector\"," +
                "\"version\":\"1.40.0-wmf.20\"," +
                "\"is_production\":true," +
                "\"is_debug_mode\":false," +
                "\"database\":\"enwiki\"," +
                "\"site_content_language\":\"en\"," +
                "\"site_content_language_variant\":\"en-zh\"" +
                "}");
    }
}
