package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.GsonHelper;

import com.google.gson.Gson;

public class AgentDataTest {
    @Test
    void testAgentData() {
        AgentData agentData = AgentData.builder()
                .appInstallId("ffffffff-ffff-ffff-ffff-ffffffffffff")
                .clientPlatform("android")
                .clientPlatformFamily("app")
                .build();

        assertThat(agentData.getAppInstallId()).isEqualTo("ffffffff-ffff-ffff-ffff-ffffffffffff");
        assertThat(agentData.getClientPlatform()).isEqualTo("android");
        assertThat(agentData.getClientPlatformFamily()).isEqualTo("app");

        Gson gson = GsonHelper.getGson();
        String json = gson.toJson(agentData);
        assertThat(json).isEqualTo("{" +
                "\"app_install_id\":\"ffffffff-ffff-ffff-ffff-ffffffffffff\"," +
                "\"client_platform\":\"android\"," +
                "\"client_platform_family\":\"app\"" +
                "}");
    }
}
