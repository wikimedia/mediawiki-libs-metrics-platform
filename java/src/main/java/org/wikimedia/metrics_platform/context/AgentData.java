package org.wikimedia.metrics_platform.context;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Agent context data fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Builder
@Data
@AllArgsConstructor
@ParametersAreNullableByDefault
public class AgentData {

    public static final AgentData NULL_AGENT_DATA = AgentData.builder().build();

    @SerializedName("app_install_id") private final String appInstallId;
    @SerializedName("client_platform") private final String clientPlatform;
    @SerializedName("client_platform_family") private final String clientPlatformFamily;

}
