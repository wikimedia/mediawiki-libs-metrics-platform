package org.wikimedia.metrics_platform;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
@ParametersAreNonnullByDefault
public interface ClientMetadata {
    String getAgentAppInstallId();
    String getAgentClientPlatform();
    String getAgentClientPlatformFamily();
    String getMediawikiSkin();
    String getMediawikiVersion();
    Boolean getMediawikiIsProduction();
    Boolean getMediawikiIsDebugMode();
    String getMediawikiDatabase();
    String getMediawikiSiteContentLanguage();
    String getMediawikiSiteContentLanguageVariant();
    Integer getPageId();
    Integer getPageNamespace();
    String getPageNamespaceName();
    String getPageTitle();
    Boolean getPageIsRedirect();
    Integer getPageRevisionId();
    String getPageWikidataItemId();
    String getPageContentLanguage();
    Collection<String> getPageUserGroupsAllowedToEdit();
    Collection<String> getPageUserGroupsAllowedToMove();

    Integer getPerformerId();
    String getPerformerName();
    String getPerformerSessionId();
    String getPerformerPageviewId();
    Boolean getPerformerIsLoggedIn();
    Collection<String> getPerformerGroups();
    Boolean getPerformerIsBot();
    Boolean getPerformerCanProbablyEditPage();
    Integer getPerformerEditCount();
    String getPerformerEditCountBucket();
    Long getPerformerRegistrationDt();
    String getPerformerLanguage();
    String getPerformerLanguageVariant();

    String getDomain();
}
