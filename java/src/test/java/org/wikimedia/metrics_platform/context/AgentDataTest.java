package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.json.GsonHelper;

import com.google.gson.Gson;

public class AgentDataTest {
    @Test
    void testAgentData() {
        AgentData agentData = AgentData.builder()
                .appFlavor("flamingo")
                .appInstallId("ffffffff-ffff-ffff-ffff-ffffffffffff")
                .appTheme("giraffe")
                .appVersion("elephant")
                .clientPlatform("android")
                .clientPlatformFamily("app")
                .deviceLanguage("en")
                .releaseStatus("beta")
                .build();

        assertThat(agentData.getAppFlavor()).isEqualTo("flamingo");
        assertThat(agentData.getAppInstallId()).isEqualTo("ffffffff-ffff-ffff-ffff-ffffffffffff");
        assertThat(agentData.getAppTheme()).isEqualTo("giraffe");
        assertThat(agentData.getAppVersion()).isEqualTo("elephant");
        assertThat(agentData.getClientPlatform()).isEqualTo("android");
        assertThat(agentData.getClientPlatformFamily()).isEqualTo("app");
        assertThat(agentData.getDeviceLanguage()).isEqualTo("en");
        assertThat(agentData.getReleaseStatus()).isEqualTo("beta");

        Gson gson = GsonHelper.getGson();
        String json = gson.toJson(agentData);
        assertThat(json).isEqualTo("{" +
                "\"app_flavor\":\"flamingo\"," +
                "\"app_install_id\":\"ffffffff-ffff-ffff-ffff-ffffffffffff\"," +
                "\"app_theme\":\"giraffe\"," +
                "\"app_version\":\"elephant\"," +
                "\"client_platform\":\"android\"," +
                "\"client_platform_family\":\"app\"," +
                "\"device_language\":\"en\"," +
                "\"release_status\":\"beta\"" +
                "}");
    }
}
