package org.wikimedia.metrics_platform;

import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestClientMetadata implements ClientMetadata {

    @Override
    public Integer getPageId() {
        return 1;
    }

    @Override
    public Integer getPageNamespaceId() {
        return 0;
    }

    @Override
    public String getPageNamespaceText() {
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
    public Collection<String> getPageGroupsAllowedToEdit() {
        return Collections.emptySet();
    }

    @Override
    public Collection<String> getPageGroupsAllowedToMove() {
        return Collections.emptySet();
    }

    // User

    @Override
    public Integer getPerformerId() {
        return 1;
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
    public String getPerformerName() {
        return "TestUser";
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

    // Device

    @Override
    public Float getDevicePixelRatio() {
        return 1.0f;
    }

    @Override
    public Integer getDeviceHardwareConcurrency() {
        return 1;
    }

    @Override
    public Integer getDeviceMaxTouchPoints() {
        return 1;
    }

    @Override
    public Boolean isProduction() {
        return true;
    }

    @Override
    public String getAppInstallId() {
        return "6f31a4fa-0a77-4c65-9994-f242fa58ce94";
    }
}
