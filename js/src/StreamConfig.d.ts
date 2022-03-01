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
    sampling?: StreamSamplingConfig;
    provide_values?: StreamProducerContextAttribute[];
    curation?: Record<string, StreamProducerCurationConfig>
}

type StreamProducerContextAttribute =
    | "page_id"
    | "page_namespace_id"
    | "page_namespace_text"
    | "page_title"
    | "page_is_redirect"
    | "page_revision_id"
    | "page_wikidata_id"
    | "page_content_language"
    | "page_user_groups_allowed_to_edit"
    | "page_user_groups_allowed_to_move"
    | "user_id"
    | "user_is_logged_in"
    | "user_is_bot"
    | "user_name"
    | "user_groups"
    | "user_can_probably_edit_page"
    | "user_edit_count"
    | "user_edit_count_bucket"
    | "user_registration_timestamp"
    | "user_language"
    | "user_language_variant"
    | "mediawiki_skin"
    | "mediawiki_version"
    | "mediawiki_site_content_language"
    | "device_pixel_ratio"
    | "device_hardware_concurrency"
    | "device_max_touch_points"
    | "access_method"
    | "platform"
    | "platform_family"
    | "is_debug_mode"
    | "is_production"
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
