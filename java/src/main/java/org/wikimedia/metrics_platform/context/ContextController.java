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
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_USER_GROUPS_ALLOWED_TO_EDIT;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_USER_GROUPS_ALLOWED_TO_MOVE;
import static org.wikimedia.metrics_platform.context.ContextValue.PAGE_WIKIDATA_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.PLATFORM;
import static org.wikimedia.metrics_platform.context.ContextValue.PLATFORM_FAMILY;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_CAN_PROBABLY_EDIT_PAGE;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_EDIT_COUNT;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_EDIT_COUNT_BUCKET;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_GROUPS;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_ID;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_IS_BOT;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_IS_LOGGED_IN;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_LANGUAGE;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_LANGUAGE_VARIANT;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_NAME;
import static org.wikimedia.metrics_platform.context.ContextValue.USER_REGISTRATION_TIMESTAMP;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.ClientMetadata;
import org.wikimedia.metrics_platform.StreamConfig;

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
                case PAGE_USER_GROUPS_ALLOWED_TO_EDIT:
                    event.getPageData().setGroupsAllowedToEdit(clientMetadata.getPageGroupsAllowedToEdit());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_MOVE:
                    event.getPageData().setGroupsAllowedToMove(clientMetadata.getPageGroupsAllowedToMove());
                    break;

                    // User
                case USER_ID:
                    event.getUserData().setId(clientMetadata.getUserId());
                    break;
                case USER_IS_LOGGED_IN:
                    event.getUserData().setIsLoggedIn(clientMetadata.getUserIsLoggedIn());
                    break;
                case USER_IS_BOT:
                    event.getUserData().setIsBot(clientMetadata.getUserIsBot());
                    break;
                case USER_NAME:
                    event.getUserData().setName(clientMetadata.getUserName());
                    break;
                case USER_GROUPS:
                    event.getUserData().setGroups(clientMetadata.getUserGroups());
                    break;
                case USER_CAN_PROBABLY_EDIT_PAGE:
                    event.getUserData().setCanProbablyEditPage(clientMetadata.getUserCanProbablyEditPage());
                    break;
                case USER_EDIT_COUNT:
                    event.getUserData().setEditCount(clientMetadata.getUserEditCount());
                    break;
                case USER_EDIT_COUNT_BUCKET:
                    event.getUserData().setEditCountBucket(clientMetadata.getUserEditCountBucket());
                    break;
                case USER_REGISTRATION_TIMESTAMP:
                    event.getUserData().setRegistrationTimestamp(clientMetadata.getUserRegistrationTimestamp());
                    break;
                case USER_LANGUAGE:
                    event.getUserData().setLanguage(clientMetadata.getUserLanguage());
                    break;
                case USER_LANGUAGE_VARIANT:
                    event.getUserData().setLanguageVariant(clientMetadata.getUserLanguageVariant());
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
