package org.wikimedia.metrics_platform.context;

import javax.annotation.ParametersAreNullableByDefault;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent context data fields.
 *
 * All fields are nullable, and boxed types are used in place of their equivalent primitive types to avoid
 * unexpected default values from being used where the true value is null.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ParametersAreNullableByDefault
public class AgentData {
    @SerializedName("app_install_id") private String appInstallId;
    @SerializedName("client_platform") private String clientPlatform;
    @SerializedName("client_platform_family") private String clientPlatformFamily;
}
