interface BaseEventData {
    $schema: string;
    meta?: EventMetaData;

    // Set by EventGate during ingestion.
    // http: ...;

    client_dt?: string;
    dt?: string;
}

interface EventMetaData {
    domain?: string;
    stream: string;

    // ...
}

interface EventData extends BaseEventData {
    [key: string]: any;
}

interface MetricsPlatformEventData extends BaseEventData {
    name?: string;

    agent?: EventAgentData;
    page?: EventPageData;
    mediawiki?: EventMediaWikiData;
    user?: EventUserData;
    custom_data?: Record<string, EventCustomDatum>;

    // NOTE: The return on tightening up the following types is minimal as they will be removed
    // in the immediate future.
    access_method?: string;
    platform?: string;
    platform_family?: string;
    is_debug_mode?: boolean;
    is_production?: boolean;
    device?: EventDeviceData;
}

interface EventAgentData {
    app_install_id?: string;
    client_platform?: string;
    client_platform_family?: string;
}

interface EventPageData {
    id?: number;
    title?: string;
    namespace_id?: number;
    namespace_text?: string;
    revision_id?: number;
    wikidata_id?: string;
    content_language?: string;
    is_redirect?: boolean;
    user_groups_allowed_to_move?: string[];
    user_groups_allowed_to_edit?: string[];
}

interface EventMediaWikiData {
    skin?: string;
    version?: string;
    is_production?: boolean;
    is_debug_mode?: boolean;
    site_content_language?: string;
    site_content_language_variant?: string;
}

interface EventUserData {
    id?: number;
    name?: string;
    is_logged_in?: boolean;
    is_bot?: boolean;
    groups?: string[];
    can_probably_edit_page?: boolean;
    edit_count?: number
    edit_count_bucket?: string;
    registration_timestamp?: number;
    language?: string
    language_variant?: string;
}

interface EventCustomDatum {
    data_type: string;
    value: string;
}

interface EventDeviceData {
    pixel_ratio?: number;
    hardware_concurrency?: number;
    max_touch_points?: number;
}
