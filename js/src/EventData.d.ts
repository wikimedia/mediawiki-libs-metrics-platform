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
    performer?: EventPerformerData;
    custom_data?: Record<string, EventCustomDatum>;
}

interface EventAgentData {
    app_install_id?: string;
    client_platform?: string;
    client_platform_family?: string;
}

interface EventPageData {
    id?: number;
    title?: string;
    namespace?: number;
    namespace_name?: string;
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
    db_name?: string;
    site_content_language?: string;
    site_content_language_variant?: string;
}

interface EventPerformerData {
    is_logged_in?: boolean;
    id?: number;
    name?: string;
    session_id?: string;
    pageview_id?: string;
    groups?: string[];
    is_bot?: boolean;
    language?: string;
    language_variant?: string;
    can_probably_edit_page?: boolean;
    edit_count?: number;
    edit_count_bucket?: string;
    registration_dt?: number;
}

interface EventCustomDatum {
    data_type: string;
    value: string;
}
