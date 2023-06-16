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
import org.wikimedia.metrics_platform.context.ClientData;
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

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY", justification = "TODO: needs to be refactored")
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
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
        for (String value : requestedValues) {
            switch (value) {
                // Agent
                case AGENT_APP_INSTALL_ID:
                    event.getAgentData().setAppInstallId(clientData.getAgentData().getAppInstallId());
                    break;
                case AGENT_CLIENT_PLATFORM:
                    event.getAgentData().setClientPlatform(clientData.getAgentData().getClientPlatform());
                    break;
                case AGENT_CLIENT_PLATFORM_FAMILY:
                    event.getAgentData().setClientPlatformFamily(clientData.getAgentData().getClientPlatformFamily());
                    break;

                // Application
                case MEDIAWIKI_SKIN:
                    event.getMediawikiData().setSkin(clientData.getMediawikiData().getSkin());
                    break;
                case MEDIAWIKI_VERSION:
                    event.getMediawikiData().setVersion(clientData.getMediawikiData().getVersion());
                    break;
                case MEDIAWIKI_IS_PRODUCTION:
                    event.getMediawikiData().setIsProduction(clientData.getMediawikiData().getIsProduction());
                    break;
                case MEDIAWIKI_IS_DEBUG_MODE:
                    event.getMediawikiData().setIsDebugMode(clientData.getMediawikiData().getIsDebugMode());
                    break;
                case MEDIAWIKI_DATABASE:
                    event.getMediawikiData().setDatabase(clientData.getMediawikiData().getDatabase());
                    break;
                case MEDIAWIKI_SITE_CONTENT_LANGUAGE:
                    event.getMediawikiData().setSiteContentLanguage(clientData.getMediawikiData().getSiteContentLanguage());
                    break;
                case MEDIAWIKI_SITE_CONTENT_LANGUAGE_VARIANT:
                    event.getMediawikiData().setSiteContentLanguageVariant(clientData.getMediawikiData().getSiteContentLanguageVariant());
                    break;

                // Page
                case PAGE_ID:
                    event.getPageData().setId(clientData.getPageData().getId());
                    break;
                case PAGE_TITLE:
                    event.getPageData().setTitle(clientData.getPageData().getTitle());
                    break;
                case PAGE_NAMESPACE:
                    event.getPageData().setNamespace(clientData.getPageData().getNamespace());
                    break;
                case PAGE_NAMESPACE_NAME:
                    event.getPageData().setNamespaceName(clientData.getPageData().getNamespaceName());
                    break;
                case PAGE_REVISION_ID:
                    event.getPageData().setRevisionId(clientData.getPageData().getRevisionId());
                    break;
                case PAGE_WIKIDATA_QID:
                    event.getPageData().setWikidataItemQid(clientData.getPageData().getWikidataItemQid());
                    break;
                case PAGE_CONTENT_LANGUAGE:
                    event.getPageData().setContentLanguage(clientData.getPageData().getContentLanguage());
                    break;
                case PAGE_IS_REDIRECT:
                    event.getPageData().setIsRedirect(clientData.getPageData().getIsRedirect());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_EDIT:
                    event.getPageData().setGroupsAllowedToEdit(clientData.getPageData().getGroupsAllowedToEdit());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_MOVE:
                    event.getPageData().setGroupsAllowedToMove(clientData.getPageData().getGroupsAllowedToMove());
                    break;

                // Performer
                case PERFORMER_ID:
                    event.getPerformerData().setId(clientData.getPerformerData().getId());
                    break;
                case PERFORMER_NAME:
                    event.getPerformerData().setName(clientData.getPerformerData().getName());
                    break;
                case PERFORMER_IS_LOGGED_IN:
                    event.getPerformerData().setIsLoggedIn(clientData.getPerformerData().getIsLoggedIn());
                    break;
                case PERFORMER_SESSION_ID:
                    event.getPerformerData().setSessionId(clientData.getPerformerData().getSessionId());
                    break;
                case PERFORMER_PAGEVIEW_ID:
                    event.getPerformerData().setPageviewId(clientData.getPerformerData().getPageviewId());
                    break;
                case PERFORMER_GROUPS:
                    event.getPerformerData().setGroups(clientData.getPerformerData().getGroups());
                    break;
                case PERFORMER_IS_BOT:
                    event.getPerformerData().setIsBot(clientData.getPerformerData().getIsBot());
                    break;
                case PERFORMER_LANGUAGE:
                    event.getPerformerData().setLanguage(clientData.getPerformerData().getLanguage());
                    break;
                case PERFORMER_LANGUAGE_VARIANT:
                    event.getPerformerData().setLanguageVariant(clientData.getPerformerData().getLanguageVariant());
                    break;
                case PERFORMER_CAN_PROBABLY_EDIT_PAGE:
                    event.getPerformerData().setCanProbablyEditPage(clientData.getPerformerData().getCanProbablyEditPage());
                    break;
                case PERFORMER_EDIT_COUNT:
                    event.getPerformerData().setEditCount(clientData.getPerformerData().getEditCount());
                    break;
                case PERFORMER_EDIT_COUNT_BUCKET:
                    event.getPerformerData().setEditCountBucket(clientData.getPerformerData().getEditCountBucket());
                    break;
                case PERFORMER_REGISTRATION_DT:
                    event.getPerformerData().setRegistrationDt(clientData.getPerformerData().getRegistrationDt());
                    break;

                default:
                    throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property %s", value));
            }
        }
    }
}
