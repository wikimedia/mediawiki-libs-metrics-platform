type StreamConfigs = Record<string, StreamConfig>;

interface StreamConfig {
    schema_title?: string;
    producers?: Record<StreamProducerName, StreamProducerConfig>;
    sample?: StreamSamplingConfig;
}

// ---

interface StreamSamplingConfig {
    unit: "pageview" | "session";
    rate: number;
}

// ---

type StreamProducerName = "metrics_platform_client";

interface StreamProducerConfig {
    events?: string|string[];

    // TODO (phuedx, 2024/09/05): Do we need to keep this?
    sampling?: StreamSamplingConfig;

    provide_values?: StreamProducerContextAttribute[];
    curation?: StreamProducerCurationConfigs;

    stream_name?: string;
}

type StreamProducerCurationConfigs = {
	[key in StreamProducerContextAttribute]?: StreamProducerCurationConfig;
}

type StreamProducerContextAttribute =

    // Agent
    | "agent_app_install_id"
    | "agent_client_platform"
    | "agent_client_platform_family"

    // Page
    | "page_id"
    | "page_title"
    | "page_namespace"
    | "page_namespace_name"
    | "page_revision_id"
    | "page_wikidata_id"
    | "page_wikidata_qid"
    | "page_content_language"
    | "page_is_redirect"
    | "page_user_groups_allowed_to_move"
    | "page_user_groups_allowed_to_edit"

    // MediaWiki
    | "mediawiki_skin"
    | "mediawiki_version"
    | "mediawiki_is_production"
    | "mediawiki_is_debug_mode"
    | "mediawiki_database"
    | "mediawiki_site_content_language"
    | "mediawiki_site_content_language_variant"

    // Performer
    | "performer_is_logged_in"
    | "performer_id"
    | "performer_name"
    | "performer_session_id"
    | "performer_active_browsing_session_token"
    | "performer_pageview_id"
    | "performer_groups"
    | "performer_is_bot"
    | "performer_is_temp"
    | "performer_language"
    | "performer_language_variant"
    | "performer_can_probably_edit_page"
    | "performer_edit_count"
    | "performer_edit_count_bucket"
    | "performer_registration_dt"
    ;

interface StreamProducerCurationConfig {
    equals?: StreamProducerCurationOperand;
    not_equals?: StreamProducerCurationOperand;
    greater_than?: StreamProducerCurationOperand;
    less_than?: number;
    greater_than_or_equals?: number;
    less_than_or_equals?: number;
    in?: StreamProducerCurationOperand[];
    not_in?: StreamProducerCurationOperand[];
    contains?: StreamProducerCurationOperand;
    does_not_contain?: StreamProducerCurationOperand;
    contains_all?: StreamProducerCurationOperand[];
    contains_any?: StreamProducerCurationOperand[];
}

type StreamProducerCurationOperand =
    string
    | number
    | boolean
    | null
    ;
