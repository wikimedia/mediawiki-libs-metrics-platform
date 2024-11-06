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

type FormattedCustomData = Record<string, EventCustomDatum>;

interface EventCustomDatum {
    data_type: string;
    value: string;
}

interface MetricsPlatformEventData extends BaseEventData, ContextAttributes {
    name?: string;
    custom_data?: FormattedCustomData;
}

/**
 * All the context attributes that can be provided by the Metrics Platform Client.
 *
 * @see https://wikitech.wikimedia.org/wiki/Metrics_Platform/Contextual_attributes
 */
interface ContextAttributes {
	agent?: EventAgentData;
	page?: EventPageData;
	mediawiki?: EventMediaWikiData;
	performer?: EventPerformerData;
	sample?: SampleData;
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
    wikidata_id?: number;
    wikidata_qid?: string;
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
    database?: string;
    site_content_language?: string;
    site_content_language_variant?: string;
}

interface EventPerformerData {
    is_logged_in?: boolean;
    id?: number;
    name?: string;
    session_id?: string;
    active_browsing_session_token?: string;
    pageview_id?: string;
    groups?: string[];
    is_bot?: boolean;
    is_temp?: boolean;
    language?: string;
    language_variant?: string;
    can_probably_edit_page?: boolean;
    edit_count?: number;
    edit_count_bucket?: string;
    registration_dt?: number;
}

type SampleData = StreamSamplingConfig;

/**
 * Optional contextual data for the interaction.
 */
interface InteractionContextData {
    action_subtype?: string;
    action_source?: string;
    action_context?: string;

    funnel_event_sequence_position?: number;

    instrument_name?: string;
}

// TODO: Could this be limited?
type InteractionAction = string;

/**
 * Data for the interaction.
 *
 * Note well that this data cannot be submitted without being combined with some/all of the data
 * described above.
 *
 * This interface and the `InteractionContextData` interface allow for the creation of many
 * convenience methods that fill the `action` property (and/or other properties in future), e.g.
 * `MetricsClient#submitClick()`.
 */
interface InteractionData extends InteractionContextData {
    action: InteractionAction;
}

interface ElementInteractionData extends InteractionContextData {
    element_id: string;
    element_friendly_name: string;
}
