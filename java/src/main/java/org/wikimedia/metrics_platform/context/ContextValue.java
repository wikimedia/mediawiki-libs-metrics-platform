package org.wikimedia.metrics_platform.context;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class ContextValue {
    public static final String PAGE_ID = "page_id";
    public static final String PAGE_NAMESPACE_ID = "page_namespace_id";
    public static final String PAGE_NAMESPACE_TEXT = "page_namespace_text";
    public static final String PAGE_TITLE = "page_title";
    public static final String PAGE_REVISION_ID = "page_revision_id";
    public static final String PAGE_WIKIDATA_ID = "page_wikidata_id";
    public static final String PAGE_IS_REDIRECT = "page_is_redirect";
    public static final String PAGE_CONTENT_LANGUAGE = "page_content_language";
    public static final String PAGE_USER_GROUPS_ALLOWED_TO_EDIT = "page_user_groups_allowed_to_edit";
    public static final String PAGE_USER_GROUPS_ALLOWED_TO_MOVE = "page_user_groups_allowed_to_move";

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_GROUPS = "user_groups";
    public static final String USER_IS_LOGGED_IN = "user_is_logged_in";
    public static final String USER_IS_BOT = "user_is_bot";
    public static final String USER_CAN_PROBABLY_EDIT_PAGE = "user_can_probably_edit_page";
    public static final String USER_EDIT_COUNT = "user_edit_count";
    public static final String USER_EDIT_COUNT_BUCKET = "user_edit_count_bucket";
    public static final String USER_REGISTRATION_TIMESTAMP = "user_registration_timestamp";
    public static final String USER_LANGUAGE = "user_language";
    public static final String USER_LANGUAGE_VARIANT = "user_language_variant";

    public static final String DEVICE_PIXEL_RATIO = "device_pixel_ratio";
    public static final String DEVICE_HARDWARE_CONCURRENCY = "device_hardware_concurrency";
    public static final String DEVICE_MAX_TOUCH_POINTS = "device_max_touch_points";

    public static final String ACCESS_METHOD = "access_method";
    public static final String PLATFORM = "platform";
    public static final String PLATFORM_FAMILY = "platform_family";
    public static final String IS_PRODUCTION = "is_production";

    private ContextValue() {
        // utility class should never be constructed
    }
}
