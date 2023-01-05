package org.wikimedia.metrics_platform;

import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestClientMetadata implements ClientMetadata {
    @Override
    public String getAgentAppInstallId() {
        return "6f31a4fa-0a77-4c65-9994-f242fa58ce94";
    }
    @Override
    public String getAgentClientPlatform() {
        return "android";
    }
    @Override
    public String getAgentClientPlatformFamily() {
        return "app";
    }
    @Override
    public String getMediawikiSkin() {
        return "vector";
    };
    @Override
    public String getMediawikiVersion() {
        return "1.40.0-wmf.19";
    };
    @Override
    public Boolean getMediawikiIsProduction() {
        return true;
    };
    @Override
    public Boolean getMediawikiIsDebugMode() {
        return false;
    };
    @Override
    public String getMediawikiDatabase() {
        return "enwiki";
    };
    @Override
    public String getMediawikiSiteContentLanguage() {
        return "en";
    };
    @Override
    public String getMediawikiSiteContentLanguageVariant() {
        return "en-zh";
    };
    @Override
    public Integer getPageId() {
        return 1;
    }

    @Override
    public Integer getPageNamespace() {
        return 0;
    }

    @Override
    public String getPageNamespaceName() {
        return "";
    }

    @Override
    public String getPageTitle() {
        return "Test";
    }

    @Override
    public Boolean getPageIsRedirect() {
        return false;
    }

    @Override
    public Integer getPageRevisionId() {
        return 1;
    }

    @Override
    public String getPageWikidataItemId() {
        return "Q1";
    }

    @Override
    public String getPageContentLanguage() {
        return "zh";
    }

    @Override
    public Collection<String> getPageUserGroupsAllowedToEdit() {
        return Collections.emptySet();
    }

    @Override
    public Collection<String> getPageUserGroupsAllowedToMove() {
        return Collections.emptySet();
    }

    // User

    @Override
    public Integer getPerformerId() {
        return 1;
    }

    @Override
    public String getPerformerName() {
        return "TestUser";
    }

    @Override
    public String getPerformerSessionId() {
        return "TestUser";
    }

    @Override
    public String getPerformerPageviewId() {
        return "TestUser";
    }

    @Override
    public Boolean getPerformerIsLoggedIn() {
        return true;
    }

    @Override
    public Boolean getPerformerIsBot() {
        return false;
    }

    @Override
    public List<String> getPerformerGroups() {
        return singletonList("*");
    }

    @Override
    public Boolean getPerformerCanProbablyEditPage() {
        return true;
    }

    @Override
    public Integer getPerformerEditCount() {
        return 10;
    }

    @Override
    public String getPerformerEditCountBucket() {
        return "5-99 edits";
    }

    @Override
    public Long getPerformerRegistrationDt() {
        return 1427224089000L;
    }

    @Override
    public String getPerformerLanguage() {
        return "zh";
    }

    @Override
    public String getPerformerLanguageVariant() {
        return "zh-tw";
    }

    @Override
    public String getDomain() {
        return "en.wikipedia.org";
    }
}
