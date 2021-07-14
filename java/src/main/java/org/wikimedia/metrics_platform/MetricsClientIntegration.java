package org.wikimedia.metrics_platform;

import java.util.Collection;
import java.util.Map;

public interface MetricsClientIntegration {
    Integer getPageId();
    Integer getPageNamespaceId();
    String getPageNamespaceText();
    String getPageTitle();
    Boolean getPageIsRedirect();
    Integer getPageRevisionId();
    String getPageWikidataItemId();
    String getPageContentLanguage();
    Collection<String> getPageGroupsAllowedToEdit();
    Collection<String> getPageGroupsAllowedToMove();

    Integer getUserId();
    Boolean getUserIsLoggedIn();
    Boolean getUserIsBot();
    String getUserName();
    Collection<String> getUserGroups();
    Boolean getUserCanProbablyEditPage();
    Integer getUserEditCount();
    String getUserEditCountBucket();
    Long getUserRegistrationTimestamp();
    String getUserLanguage();
    String getUserLanguageVariant();

    Float getDevicePixelRatio();
    Integer getDeviceHardwareConcurrency();
    Integer getDeviceMaxTouchPoints();

    Boolean isProduction();

    String getAppInstallId();

    void fetchStreamConfigs(FetchStreamConfigsCallback callback);

    void sendEvents(String baseUri, Collection<Event> events, SendEventsCallback callback);

    interface FetchStreamConfigsCallback {
        void onSuccess(Map<String, StreamConfig> streamConfigs);
        void onFailure();
    }

    interface SendEventsCallback {
        void onSuccess();
        void onFailure();
    }
}
