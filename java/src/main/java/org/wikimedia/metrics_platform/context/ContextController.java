package org.wikimedia.metrics_platform.context;

import static org.wikimedia.metrics_platform.context.ContextValue.ACCESS_METHOD;
import static org.wikimedia.metrics_platform.context.ContextValue.DEVICE_HARDWARE_CONCURRENCY;
import static org.wikimedia.metrics_platform.context.ContextValue.DEVICE_MAX_TOUCH_POINTS;
import static org.wikimedia.metrics_platform.context.ContextValue.DEVICE_PIXEL_RATIO;
import static org.wikimedia.metrics_platform.context.ContextValue.IS_PRODUCTION;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_CONTENT_LANGUAGE;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_IS_REDIRECT;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_NAMESPACE_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_NAMESPACE_TEXT;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_REVISION_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_TITLE;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_PERFORMER_GROUPS_ALLOWED_TO_EDIT;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_PERFORMER_GROUPS_ALLOWED_TO_MOVE;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_WIKIDATA_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PLATFORM;
import static org.wikimedia.metrics_platform.context.ContextValue.PLATFORM_FAMILY;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_NAME;
import static org.wikimedia.metrics_platform.context.ContextValue.PERFORMER_IS_LOGGED_IN;
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

import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.ClientMetadata;
import org.wikimedia.metrics_platform.config.StreamConfig;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@ThreadSafe
@ParametersAreNonnullByDefault
public class ContextController {

    @Nonnull
    private final ClientMetadata clientMetadata;

    public ContextController(ClientMetadata clientMetadata) {
        this.clientMetadata = clientMetadata;
    }

    @SuppressFBWarnings(value = "CC_CYCLOMATIC_COMPLEXITY", justification = "TODO: needs to be refactored")
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void addRequestedValues(Event event, StreamConfig streamConfig) {
        if (!streamConfig.hasRequestedContextValuesConfig()) {
            return;
        }
        Collection<String> requestedValues = streamConfig.getProducerConfig()
                .getMetricsPlatformClientConfig().getRequestedValues();
        for (String value : requestedValues) {
            switch (value) {
                // Page
                case PAGE_ID:
                    event.getPageData().setId(clientMetadata.getPageId());
                    break;
                case PAGE_NAMESPACE_ID:
                    event.getPageData().setNamespaceId(clientMetadata.getPageNamespaceId());
                    break;
                case PAGE_NAMESPACE_TEXT:
                    event.getPageData().setNamespaceText(clientMetadata.getPageNamespaceText());
                    break;
                case PAGE_TITLE:
                    event.getPageData().setTitle(clientMetadata.getPageTitle());
                    break;
                case PAGE_IS_REDIRECT:
                    event.getPageData().setIsRedirect(clientMetadata.getPageIsRedirect());
                    break;
                case PAGE_REVISION_ID:
                    event.getPageData().setRevisionId(clientMetadata.getPageRevisionId());
                    break;
                case PAGE_WIKIDATA_ID:
                    event.getPageData().setWikidataItemId(clientMetadata.getPageWikidataItemId());
                    break;
                case PAGE_CONTENT_LANGUAGE:
                    event.getPageData().setContentLanguage(clientMetadata.getPageContentLanguage());
                    break;
                case PAGE_PERFORMER_GROUPS_ALLOWED_TO_EDIT:
                    event.getPageData().setGroupsAllowedToEdit(clientMetadata.getPageGroupsAllowedToEdit());
                    break;
                case PAGE_PERFORMER_GROUPS_ALLOWED_TO_MOVE:
                    event.getPageData().setGroupsAllowedToMove(clientMetadata.getPageGroupsAllowedToMove());
                    break;

                // User
                case PERFORMER_ID:
                    event.getPerformerData().setId(clientMetadata.getPerformerId());
                    break;
                case PERFORMER_IS_LOGGED_IN:
                    event.getPerformerData().setIsLoggedIn(clientMetadata.getPerformerIsLoggedIn());
                    break;
                case PERFORMER_IS_BOT:
                    event.getPerformerData().setIsBot(clientMetadata.getPerformerIsBot());
                    break;
                case PERFORMER_NAME:
                    event.getPerformerData().setName(clientMetadata.getPerformerName());
                    break;
                case PERFORMER_GROUPS:
                    event.getPerformerData().setGroups(clientMetadata.getPerformerGroups());
                    break;
                case PERFORMER_CAN_PROBABLY_EDIT_PAGE:
                    event.getPerformerData().setCanProbablyEditPage(clientMetadata.getPerformerCanProbablyEditPage());
                    break;
                case PERFORMER_EDIT_COUNT:
                    event.getPerformerData().setEditCount(clientMetadata.getPerformerEditCount());
                    break;
                case PERFORMER_EDIT_COUNT_BUCKET:
                    event.getPerformerData().setEditCountBucket(clientMetadata.getPerformerEditCountBucket());
                    break;
                case PERFORMER_REGISTRATION_DT:
                    event.getPerformerData().setRegistrationDt(clientMetadata.getPerformerRegistrationDt());
                    break;
                case PERFORMER_LANGUAGE:
                    event.getPerformerData().setLanguage(clientMetadata.getPerformerLanguage());
                    break;
                case PERFORMER_LANGUAGE_VARIANT:
                    event.getPerformerData().setLanguageVariant(clientMetadata.getPerformerLanguageVariant());
                    break;

                // Device
                case DEVICE_PIXEL_RATIO:
                    event.getDeviceData().setPixelRatio(clientMetadata.getDevicePixelRatio());
                    break;
                case DEVICE_HARDWARE_CONCURRENCY:
                    event.getDeviceData().setHardwareConcurrency(clientMetadata.getDeviceHardwareConcurrency());
                    break;
                case DEVICE_MAX_TOUCH_POINTS:
                    event.getDeviceData().setMaxTouchPoints(clientMetadata.getDeviceMaxTouchPoints());
                    break;

                // Other
                case ACCESS_METHOD:
                    event.setAccessMethod("mobile app");
                    break;
                case PLATFORM:
                    event.setPlatform("android");
                    break;
                case PLATFORM_FAMILY:
                    event.setPlatformFamily("app");
                    break;
                case IS_PRODUCTION:
                    event.setIsProduction(clientMetadata.isProduction());
                    break;
                default:
                    throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property %s", value));
            }
        }
    }
}
