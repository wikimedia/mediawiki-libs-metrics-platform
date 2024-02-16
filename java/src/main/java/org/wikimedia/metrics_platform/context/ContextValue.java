package org.wikimedia.metrics_platform.context;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class ContextValue {
    public static final String AGENT_APP_INSTALL_ID = "agent_app_install_id";
    public static final String AGENT_CLIENT_PLATFORM = "agent_client_platform";
    public static final String AGENT_CLIENT_PLATFORM_FAMILY = "agent_client_platform_family";
    public static final String AGENT_APP_FLAVOR = "agent_app_flavor";
    public static final String AGENT_APP_THEME = "agent_app_theme";
    public static final String AGENT_APP_VERSION = "agent_app_version";
    public static final String AGENT_APP_VERSION_NAME = "agent_app_version_name";
    public static final String AGENT_DEVICE_LANGUAGE = "agent_device_language";
    public static final String AGENT_RELEASE_STATUS = "agent_release_status";

    public static final String MEDIAWIKI_DATABASE = "mediawiki_database";

    public static final String PAGE_ID = "page_id";
    public static final String PAGE_TITLE = "page_title";
    public static final String PAGE_NAMESPACE_ID = "page_namespace_id";
    public static final String PAGE_NAMESPACE_NAME = "page_namespace_name";
    public static final String PAGE_REVISION_ID = "page_revision_id";
    public static final String PAGE_WIKIDATA_QID = "page_wikidata_qid";
    public static final String PAGE_CONTENT_LANGUAGE = "page_content_language";

    public static final String PERFORMER_ID = "performer_id";
    public static final String PERFORMER_NAME = "performer_name";
    public static final String PERFORMER_IS_LOGGED_IN = "performer_is_logged_in";
    public static final String PERFORMER_IS_TEMP = "performer_is_temp";
    public static final String PERFORMER_SESSION_ID = "performer_session_id";
    public static final String PERFORMER_PAGEVIEW_ID = "performer_pageview_id";
    public static final String PERFORMER_GROUPS = "performer_groups";
    public static final String PERFORMER_LANGUAGE_GROUPS = "performer_language_groups";
    public static final String PERFORMER_LANGUAGE_PRIMARY = "performer_language_primary";
    public static final String PERFORMER_REGISTRATION_DT = "performer_registration_dt";

    private ContextValue() {
        // utility class should never be constructed
    }
}
