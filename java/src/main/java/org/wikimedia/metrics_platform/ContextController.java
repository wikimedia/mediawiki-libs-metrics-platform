package org.wikimedia.metrics_platform;

import static org.wikimedia.metrics_platform.context.ContextValue.AGENT_APP_INSTALL_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.AGENT_CLIENT_PLATFORM;
import static org.wikimedia.metrics_platform.context.ContextValue.AGENT_CLIENT_PLATFORM_FAMILY;
import static org.wikimedia.metrics_platform.context.ContextValue.MEDIAWIKI_SKIN;
import static org.wikimedia.metrics_platform.context.ContextValue.MEDIAWIKI_VERSION;
import static org.wikimedia.metrics_platform.context.ContextValue.MEDIAWIKI_IS_PRODUCTION;
import static org.wikimedia.metrics_platform.context.ContextValue.MEDIAWIKI_IS_DEBUG_MODE;
import static org.wikimedia.metrics_platform.context.ContextValue.MEDIAWIKI_DATABASE;
import static org.wikimedia.metrics_platform.context.ContextValue.MEDIAWIKI_SITE_CONTENT_LANGUAGE;
import static org.wikimedia.metrics_platform.context.ContextValue.MEDIAWIKI_SITE_CONTENT_LANGUAGE_VARIANT;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_TITLE;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_NAMESPACE;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_NAMESPACE_NAME;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_REVISION_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_WIKIDATA_QID;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_CONTENT_LANGUAGE;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_IS_REDIRECT;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_USER_GROUPS_ALLOWED_TO_EDIT;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_USER_GROUPS_ALLOWED_TO_MOVE;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_NAME;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_IS_LOGGED_IN;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_SESSION_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_PAGEVIEW_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_GROUPS;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_IS_BOT;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_LANGUAGE;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_LANGUAGE_VARIANT;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_CAN_PROBABLY_EDIT_PAGE;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_EDIT_COUNT;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_EDIT_COUNT_BUCKET;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_REGISTRATION_DT;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import org.wikimedia.metrics_platform.config.StreamConfig;
import org.wikimedia.metrics_platform.context.AgentData;
import org.wikimedia.metrics_platform.context.ClientData;
import org.wikimedia.metrics_platform.context.MediawikiData;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.PerformerData;
import org.wikimedia.metrics_platform.event.EventProcessed;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@ThreadSafe
@ParametersAreNonnullByDefault
public class ContextController {

    @Nonnull
    private final ClientData clientData;

    public ContextController(ClientData clientData) {
        this.clientData = clientData;
    }

    public void enrichEvent(EventProcessed event, StreamConfig streamConfig) {
        // Add required metadata to the event.
        event.setDomain(clientData.getDomain());
        event.setClientData(clientData);

        if (!streamConfig.hasRequestedContextValuesConfig()) {
            return;
        }

        // Check stream config for which contextual values should be added to the event.
        Collection<String> requestedValues = streamConfig.getProducerConfig()
                .getMetricsPlatformClientConfig().getRequestedValues();
        ClientData filteredData = filterClientData(clientData, requestedValues);
        event.setClientData(filteredData);
    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY", justification = "TODO: needs to be refactored")
    private ClientData filterClientData(ClientData clientData, Collection<String> requestedValues) {
        AgentData.AgentDataBuilder agentBuilder = AgentData.builder();
        PageData.PageDataBuilder pageBuilder = PageData.builder();
        MediawikiData.MediawikiDataBuilder mediawikiBuilder = MediawikiData.builder();
        PerformerData.PerformerDataBuilder performerBuilder = PerformerData.builder();

        AgentData agentData = clientData.getAgentData();
        PageData pageData = clientData.getPageData();
        MediawikiData mediawikiData = clientData.getMediawikiData();
        PerformerData performerData = clientData.getPerformerData();

        for (String requestedValue : requestedValues) {
            switch (requestedValue) {
                case AGENT_APP_INSTALL_ID:
                    agentBuilder.appInstallId(agentData.getAppInstallId());
                    break;
                case AGENT_CLIENT_PLATFORM:
                    agentBuilder.clientPlatform(agentData.getClientPlatform());
                    break;
                case AGENT_CLIENT_PLATFORM_FAMILY:
                    agentBuilder.clientPlatformFamily(agentData.getClientPlatformFamily());
                    break;
                case PAGE_ID:
                    pageBuilder.id(pageData.getId());
                    break;
                case PAGE_TITLE:
                    pageBuilder.title(pageData.getTitle());
                    break;
                case PAGE_NAMESPACE:
                    pageBuilder.namespace(pageData.getNamespace());
                    break;
                case PAGE_NAMESPACE_NAME:
                    pageBuilder.namespaceName(pageData.getNamespaceName());
                    break;
                case PAGE_REVISION_ID:
                    pageBuilder.revisionId(pageData.getRevisionId());
                    break;
                case PAGE_WIKIDATA_QID:
                    pageBuilder.wikidataItemQid(pageData.getWikidataItemQid());
                    break;
                case PAGE_CONTENT_LANGUAGE:
                    pageBuilder.contentLanguage(pageData.getContentLanguage());
                    break;
                case PAGE_IS_REDIRECT:
                    pageBuilder.isRedirect(pageData.getIsRedirect());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_MOVE:
                    pageBuilder.groupsAllowedToMove(pageData.getGroupsAllowedToMove());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_EDIT:
                    pageBuilder.groupsAllowedToEdit(pageData.getGroupsAllowedToEdit());
                    break;
                case MEDIAWIKI_SKIN:
                    mediawikiBuilder.skin(mediawikiData.getSkin());
                    break;
                case MEDIAWIKI_VERSION:
                    mediawikiBuilder.version(mediawikiData.getVersion());
                    break;
                case MEDIAWIKI_IS_PRODUCTION:
                    mediawikiBuilder.isProduction(mediawikiData.getIsProduction());
                    break;
                case MEDIAWIKI_IS_DEBUG_MODE:
                    mediawikiBuilder.isDebugMode(mediawikiData.getIsDebugMode());
                    break;
                case MEDIAWIKI_DATABASE:
                    mediawikiBuilder.database(mediawikiData.getDatabase());
                    break;
                case MEDIAWIKI_SITE_CONTENT_LANGUAGE:
                    mediawikiBuilder.siteContentLanguage(mediawikiData.getSiteContentLanguage());
                    break;
                case MEDIAWIKI_SITE_CONTENT_LANGUAGE_VARIANT:
                    mediawikiBuilder.siteContentLanguageVariant(mediawikiData.getSiteContentLanguageVariant());
                    break;
                case PERFORMER_ID:
                    performerBuilder.id(performerData.getId());
                    break;
                case PERFORMER_NAME:
                    performerBuilder.name(performerData.getName());
                    break;
                case PERFORMER_IS_LOGGED_IN:
                    performerBuilder.isLoggedIn(performerData.getIsLoggedIn());
                    break;
                case PERFORMER_SESSION_ID:
                    performerBuilder.sessionId(performerData.getSessionId());
                    break;
                case PERFORMER_PAGEVIEW_ID:
                    performerBuilder.pageviewId(performerData.getPageviewId());
                    break;
                case PERFORMER_GROUPS:
                    performerBuilder.groups(performerData.getGroups());
                    break;
                case PERFORMER_IS_BOT:
                    performerBuilder.isBot(performerData.getIsBot());
                    break;
                case PERFORMER_LANGUAGE:
                    performerBuilder.language(performerData.getLanguage());
                    break;
                case PERFORMER_LANGUAGE_VARIANT:
                    performerBuilder.languageVariant(performerData.getLanguageVariant());
                    break;
                case PERFORMER_CAN_PROBABLY_EDIT_PAGE:
                    performerBuilder.canProbablyEditPage(performerData.getCanProbablyEditPage());
                    break;
                case PERFORMER_EDIT_COUNT:
                    performerBuilder.editCount(performerData.getEditCount());
                    break;
                case PERFORMER_EDIT_COUNT_BUCKET:
                    performerBuilder.editCountBucket(performerData.getEditCountBucket());
                    break;
                case PERFORMER_REGISTRATION_DT:
                    performerBuilder.registrationDt(performerData.getRegistrationDt());
                    break;

                default:
                    throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property %s", requestedValue));
            }
        }

        ClientData.ClientDataBuilder clientDataBuilder = ClientData.builder();
        clientDataBuilder.agentData(agentBuilder.build());
        clientDataBuilder.pageData(pageBuilder.build());
        clientDataBuilder.mediawikiData(mediawikiBuilder.build());
        clientDataBuilder.performerData(performerBuilder.build());
        return clientDataBuilder.build();
    }
}
