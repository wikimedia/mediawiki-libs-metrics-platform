package org.wikimedia.metrics_platform.context;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class ContextValue {
    public static final String AGENT_APP_INSTALL_ID = "agent_app_install_id";
    public static final String AGENT_CLIENT_PLATFORM = "agent_client_platform";
    public static final String AGENT_CLIENT_PLATFORM_FAMILY = "agent_client_platform_family";
    public static final String PAGE_ID = "page_id";
    public static final String PAGE_NAMESPACE_ID = "page_namespace_id";
    public static final String PAGE_NAMESPACE_TEXT = "page_namespace_text";
    public static final String PAGE_TITLE = "page_title";
    public static final String PAGE_REVISION_ID = "page_revision_id";
    public static final String PAGE_WIKIDATA_ID = "page_wikidata_id";
    public static final String PAGE_IS_REDIRECT = "page_is_redirect";
    public static final String PAGE_CONTENT_LANGUAGE = "page_content_language";
    public static final String PAGE_PERFORMER_GROUPS_ALLOWED_TO_EDIT = "page_user_groups_allowed_to_edit";
    public static final String PAGE_PERFORMER_GROUPS_ALLOWED_TO_MOVE = "page_user_groups_allowed_to_move";

    public static final String PERFORMER_ID = "performer_id";
    public static final String PERFORMER_NAME = "performer_name";
    public static final String PERFORMER_IS_LOGGED_IN = "performer_is_logged_in";
    public static final String PERFORMER_SESSION_ID = "performer_session_id";
    public static final String PERFORMER_PAGEVIEW_ID = "performer_pageview_id";
    public static final String PERFORMER_GROUPS = "performer_groups";
    public static final String PERFORMER_IS_BOT = "performer_is_bot";
    public static final String PERFORMER_LANGUAGE = "performer_language";
    public static final String PERFORMER_LANGUAGE_VARIANT = "performer_language_variant";

    public static final String PERFORMER_CAN_PROBABLY_EDIT_PAGE = "performer_can_probably_edit_page";
    public static final String PERFORMER_EDIT_COUNT = "performer_edit_count";
    public static final String PERFORMER_EDIT_COUNT_BUCKET = "performer_edit_count_bucket";
    public static final String PERFORMER_REGISTRATION_DT = "performer_registration_dt";
    public static final String DEVICE_PIXEL_RATIO = "device_pixel_ratio";
    public static final String DEVICE_HARDWARE_CONCURRENCY = "device_hardware_concurrency";
    public static final String DEVICE_MAX_TOUCH_POINTS = "device_max_touch_points";

    public static final String ACCESS_METHOD = "access_method";
    public static final String IS_PRODUCTION = "is_production";

    private ContextValue() {
        // utility class should never be constructed
    }
}
