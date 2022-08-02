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

import org.wikimedia.metrics_platform.Event;
import org.wikimedia.metrics_platform.MetricsClientIntegration;
import org.wikimedia.metrics_platform.StreamConfig;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ContextController {

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
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setId(integration.getPageId());
                    break;
                case PAGE_NAMESPACE_ID:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setNamespaceId(integration.getPageNamespaceId());
                    break;
                case PAGE_NAMESPACE_TEXT:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setNamespaceText(integration.getPageNamespaceText());
                    break;
                case PAGE_TITLE:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setTitle(integration.getPageTitle());
                    break;
                case PAGE_IS_REDIRECT:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setIsRedirect(integration.getPageIsRedirect());
                    break;
                case PAGE_REVISION_ID:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setRevisionId(integration.getPageRevisionId());
                    break;
                case PAGE_WIKIDATA_ID:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setWikidataItemId(integration.getPageWikidataItemId());
                    break;
                case PAGE_CONTENT_LANGUAGE:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setContentLanguage(integration.getPageContentLanguage());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_EDIT:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setGroupsAllowedToEdit(integration.getPageGroupsAllowedToEdit());
                    break;
                case PAGE_USER_GROUPS_ALLOWED_TO_MOVE:
                    if (event.getPageData() == null) {
                        event.setPageData(new PageData());
                    }
                    event.getPageData().setGroupsAllowedToMove(integration.getPageGroupsAllowedToMove());
                    break;

                    // User
                case USER_ID:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setId(integration.getUserId());
                    break;
                case USER_IS_LOGGED_IN:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setIsLoggedIn(integration.getUserIsLoggedIn());
                    break;
                case USER_IS_BOT:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setIsBot(integration.getUserIsBot());
                    break;
                case USER_NAME:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setName(integration.getUserName());
                    break;
                case USER_GROUPS:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setGroups(integration.getUserGroups());
                    break;
                case USER_CAN_PROBABLY_EDIT_PAGE:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setCanProbablyEditPage(integration.getUserCanProbablyEditPage());
                    break;
                case USER_EDIT_COUNT:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setEditCount(integration.getUserEditCount());
                    break;
                case USER_EDIT_COUNT_BUCKET:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setEditCountBucket(integration.getUserEditCountBucket());
                    break;
                case USER_REGISTRATION_TIMESTAMP:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setRegistrationTimestamp(integration.getUserRegistrationTimestamp());
                    break;
                case USER_LANGUAGE:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setLanguage(integration.getUserLanguage());
                    break;
                case USER_LANGUAGE_VARIANT:
                    if (event.getUserData() == null) {
                        event.setUserData(new UserData());
                    }
                    event.getUserData().setLanguageVariant(integration.getUserLanguageVariant());
                    break;

                    // Device
                case DEVICE_PIXEL_RATIO:
                    if (event.getDeviceData() == null) {
                        event.setDeviceData(new DeviceData());
                    }
                    event.getDeviceData().setPixelRatio(integration.getDevicePixelRatio());
                    break;
                case DEVICE_HARDWARE_CONCURRENCY:
                    if (event.getDeviceData() == null) {
                        event.setDeviceData(new DeviceData());
                    }
                    event.getDeviceData().setHardwareConcurrency(integration.getDeviceHardwareConcurrency());
                    break;
                case DEVICE_MAX_TOUCH_POINTS:
                    if (event.getDeviceData() == null) {
                        event.setDeviceData(new DeviceData());
                    }
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
