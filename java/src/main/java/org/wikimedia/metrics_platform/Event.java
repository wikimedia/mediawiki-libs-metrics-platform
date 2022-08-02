package org.wikimedia.metrics_platform;

import org.wikimedia.metrics_platform.context.DeviceData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.UserData;

import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("$schema") private final String schema;
    private final Meta meta;
    private String dt;
    @SerializedName("app_install_id") private String appInstallId;
    @SerializedName("app_session_id") private String appSessionId;

    @SerializedName("page") private PageData pageData;
    @SerializedName("user") private UserData userData;
    @SerializedName("device") private DeviceData deviceData;

    @SerializedName("access_method") private String accessMethod;
    private String platform;
    @SerializedName("platform_family") private String platformFamily;
    @SerializedName("is_production") private Boolean isProduction;

    public Event(String schema, String stream) {
        this.schema = schema;
        this.meta = new Meta(stream);
    }

    String getStream() {
        return meta.getStream();
    }

    String getSchema() {
        return schema;
    }

    String getAppInstallId() {
        return appInstallId;
    }

    void setAppInstallId(String appInstallId) {
        this.appInstallId = appInstallId;
    }


    String getAppSessionId() {
        return appSessionId;
    }

    void setAppSessionId(String sessionId) {
        this.appSessionId = sessionId;
    }

    String getTimestamp() {
        return dt;
    }

    void setTimestamp(String dt) {
        this.dt = dt;
    }

    public PageData getPageData() {
        return pageData;
    }

    public void setPageData(PageData pageData) {
        this.pageData = pageData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public DeviceData getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }

    public String getAccessMethod() {
        return accessMethod;
    }

    public void setAccessMethod(String accessMethod) {
        this.accessMethod = accessMethod;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformFamily() {
        return platformFamily;
    }

    public void setPlatformFamily(String platformFamily) {
        this.platformFamily = platformFamily;
    }

    public Boolean isProduction() {
        return isProduction;
    }

    public void setIsProduction(Boolean production) {
        this.isProduction = production;
    }

    private static final class Meta {
        private final String stream;

        private Meta(String stream) {
            this.stream = stream;
        }

        private String getStream() {
            return stream;
        }
    }

}
