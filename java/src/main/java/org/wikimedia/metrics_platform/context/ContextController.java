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
import org.wikimedia.metrics_platform.MetricsClientIntegration;
import org.wikimedia.metrics_platform.StreamConfig;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@ThreadSafe
@ParametersAreNonnullByDefault
public class ContextController {

    @Nonnull
    private final MetricsClientIntegration integration;

    public ContextController(MetricsClientIntegration integration) {
        this.integration = integration;
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
                    event.getPageData().setId(integration.getPageId());
                    break;
                case PAGE_NAMESPACE_ID:
                    event.getPageData().setNamespaceId(integration.getPageNamespaceId());
                    break;
                case PAGE_NAMESPACE_TEXT:
                    event.getPageData().setNamespaceText(integration.getPageNamespaceText());
                    break;
                case PAGE_TITLE:
                    event.getPageData().setTitle(integration.getPageTitle());
                    break;
                case PAGE_IS_REDIRECT:
                    event.getPageData().setIsRedirect(integration.getPageIsRedirect());
                    break;
                case PAGE_REVISION_ID:
                    event.getPageData().setRevisionId(integration.getPageRevisionId());
                    break;
                case PAGE_WIKIDATA_ID:
                    event.getPageData().setWikidataItemId(integration.getPageWikidataItemId());
                    break;
                case PAGE_CONTENT_LANGUAGE:
                    event.getPageData().setContentLanguage(integration.getPageContentLanguage());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_EDIT:
                    event.getPageData().setGroupsAllowedToEdit(integration.getPageGroupsAllowedToEdit());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_MOVE:
                    event.getPageData().setGroupsAllowedToMove(integration.getPageGroupsAllowedToMove());
                    break;

                    // User
                case USER_ID:
                    event.getUserData().setId(integration.getUserId());
                    break;
                case USER_IS_LOGGED_IN:
                    event.getUserData().setIsLoggedIn(integration.getUserIsLoggedIn());
                    break;
                case USER_IS_BOT:
                    event.getUserData().setIsBot(integration.getUserIsBot());
                    break;
                case USER_NAME:
                    event.getUserData().setName(integration.getUserName());
                    break;
                case USER_GROUPS:
                    event.getUserData().setGroups(integration.getUserGroups());
                    break;
                case USER_CAN_PROBABLY_EDIT_PAGE:
                    event.getUserData().setCanProbablyEditPage(integration.getUserCanProbablyEditPage());
                    break;
                case USER_EDIT_COUNT:
                    event.getUserData().setEditCount(integration.getUserEditCount());
                    break;
                case USER_EDIT_COUNT_BUCKET:
                    event.getUserData().setEditCountBucket(integration.getUserEditCountBucket());
                    break;
                case USER_REGISTRATION_TIMESTAMP:
                    event.getUserData().setRegistrationTimestamp(integration.getUserRegistrationTimestamp());
                    break;
                case USER_LANGUAGE:
                    event.getUserData().setLanguage(integration.getUserLanguage());
                    break;
                case USER_LANGUAGE_VARIANT:
                    event.getUserData().setLanguageVariant(integration.getUserLanguageVariant());
                    break;

                    // Device
                case DEVICE_PIXEL_RATIO:
                    event.getDeviceData().setPixelRatio(integration.getDevicePixelRatio());
                    break;
                case DEVICE_HARDWARE_CONCURRENCY:
                    event.getDeviceData().setHardwareConcurrency(integration.getDeviceHardwareConcurrency());
                    break;
                case DEVICE_MAX_TOUCH_POINTS:
                    event.getDeviceData().setMaxTouchPoints(integration.getDeviceMaxTouchPoints());
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
                    event.setIsProduction(integration.isProduction());
                    break;
                default:
                    throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property %s", value));
            }
        }
    }
}
