package org.wikimedia.metrics_platform;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
@ParametersAreNonnullByDefault
public interface ClientMetadata {
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

    Integer getPerformerId();
    Boolean getPerformerIsLoggedIn();
    Boolean getPerformerIsBot();
    String getPerformerName();
    Collection<String> getPerformerGroups();
    Boolean getPerformerCanProbablyEditPage();
    Integer getPerformerEditCount();
    String getPerformerEditCountBucket();
    Long getPerformerRegistrationDt();
    String getPerformerLanguage();
    String getPerformerLanguageVariant();

    Float getDevicePixelRatio();
    Integer getDeviceHardwareConcurrency();
    Integer getDeviceMaxTouchPoints();

    Boolean isProduction();

    String getAppInstallId();
}
