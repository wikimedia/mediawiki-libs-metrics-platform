package org.wikimedia.metrics_platform.context;

import java.time.Instant;
import java.util.Collections;

public final class ClientDataFixtures {

    private ClientDataFixtures() {
        // Utility class, should never be instantiated
    }

    public static ClientData getTestClientData() {
        return new ClientData(
                getTestAgentData(),
                getTestMediawikiData(),
                getTestPerformerData(),
                "en.wikipedia.org"
        );
    }

    public static AgentData getTestAgentData() {
        return AgentData.builder()
                .appInstallId("ffffffff-ffff-ffff-ffff-ffffffffffff")
                .clientPlatform("android")
                .clientPlatformFamily("app")
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
