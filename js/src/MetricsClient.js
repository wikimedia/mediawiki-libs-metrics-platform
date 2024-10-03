const ContextController = require( './ContextController.js' );
const SamplingController = require( './SamplingController.js' );
const CurationController = require( './CurationController.js' );
const DefaultEventSubmitter = require( './DefaultEventSubmitter.js' ).DefaultEventSubmitter;
const Instrument = require( './Instrument.js' );

const SCHEMA = '/analytics/mediawiki/client/metrics_event/2.1.0';

// ---

/**
 * An adaptor for the environment that the Metrics Platform Client is executing in.
 *
 * @interface Integration
 */

/**
 * Fetches stream configs from some source, remote or local.
 *
 * @method
 * @name Integration#fetchStreamConfigs
 * @returns {Promise<StreamConfigs>}
 */

/**
 * Gets the hostname of the current document.
 *
 * @method
 * @name Integration#getHostname
 * @returns {string}
 */

/**
 * Logs the warning to whatever logging backend that the execution environment provides, e.g.
 * the console.
 *
 * @method
 * @name Integration#logWarning
 * @param {string} message
 */

/**
 * Gets a deep clone of the object.
 *
 * @method
 * @name Integration#clone
 * @param {Object} obj
 * @returns {Object}
 */

/**
 * Gets the values for those context attributes that are available in the execution
 * environment.
 *
 * @method
 * @name Integration#getContextAttributes
 * @returns {ContextAttributes}
 */

// NOTE: The following are required for compatibility with the current impl. but the
// information is also available via ::getContextualAttributes() above.

/**
 * Gets a token unique to the current pageview within the execution environment.
 *
 * @method
 * @name Integration#getPageviewId
 * @returns {string}
 */

/**
 * Gets a token unique to the current session within the execution environment.
 *
 * @method
 * @name Integration#getSessionId
 * @returns {string}
 */

/**
 * Gets the experiment details for the current user.
 *
 * @method
 * @name Integration#getCurrentUserExperiments
 * @returns {Object}
 */

/**
 * @method
 * @name Integration#isCurrentUserEnrolled
 * @param {string} experimentName
 * @returns {boolean}
 */

// ---

/**
 * @typedef {object} BaseEventData
 * @property {string} $schema
 * @property {EventMetaData} [meta]
 * @property {string} [client_dt]
 * @property {string} [dt]
 */

/**
 * @typedef {object} EventMetaData
 * @property {string} [domain]
 * @property {string} stream
 *
 * …
 */

/**
 * @typedef {EventData} BaseEventData
 */

/**
 * @typedef {Object.<string,EventCustomDatum>} FormattedCustomData
 */

/**
 * @typedef {Object} EventCustomDatum
 * @property {string} data_type
 * @property {string} value
 */

/**
 * @typedef {BaseEventData|ContextAttributes} MetricsPlatformEventData
 * @property {string} name
 * @property {FormattedCustomData} [custom_data]
 */

/**
 * All the context attributes that can be provided by the JavaScript Metrics Platform Client.
 *
 * @see https://wikitech.wikimedia.org/wiki/Metrics_Platform/Contextual_attributes
 *
 * @typedef {Object} ContextAttributes
 * @property {EventAgentData} agent
 * @property {EventPageData} [page]
 * @property {EventMediaWikiData} [mediawiki]
 * @property {EventPerformerData} [performer]
 * @property {SampleData} [sample]
 */

/**
 * @typedef {Object} EventAgentData
 * @property {string} [client_platform]
 * @property {string} [client_platform_family]
 */

/**
 * @typedef {Object} EventPageData
 * @property {number} [id]
 * @property {string} [title]
 * @property {number} [namespace]
 * @property {string} [namespace_name]
 * @property {number} [revision_id]
 * @property {number} [wikidata_id]
 * @property {string} [wikidata_qid]
 * @property {string} [content_language]
 * @property {boolean} [is_redirect]
 * @property {string[]} [user_groups_allowed_to_move]
 * @property {string[]} [user_groups_allowed_to_edit]
 */

/**
 * @typedef {Object} EventMediaWikiData
 * @property {string} [skin]
 * @property {string} [version]
 * @property {boolean} [is_production]
 * @property {boolean} [is_debug_mode]
 * @property {string} [database]
 * @property {string} [site_content_language]
 * @property {string} [site_content_language_variant]
 */

/**
 * @typedef {Object} EventPerformerData
 * @property {boolean} [is_logged_in]
 * @property {string} [id]
 * @property {string} [name]
 * @property {string} [session_id]
 * @property {string} [active_browsing_session_token]
 * @property {string} [pageview_id]
 * @property {string[]} [groups]
 * @property {boolean} [is_bot]
 * @property {boolean} [is_temp]
 * @property {string} [language]
 * @property {string} [language_variant]
 * @property {boolean} [can_probably_edit_page]
 * @property {number} [edit_count]
 * @property {string} [edit_count_bucket]
 * @property {string} [registration_dt]
 */

/**
 * @typedef {StreamSamplingConfig} SampleData
 */

/**
 * Optional data related to the interaction.
 *
 * @typedef {Object} InteractionContextData
 * @property {string} action_subtype
 * @property {string} action_source
 * @property {string} action_context
 *
 * @property {number} funnel_event_sequence_position
 */

/**
 * @typedef {string} InteractionAction
 */

/**
 * Data for the interaction.
 *
 * Note well that this data cannot be submitted without being combined with some/all of the data
 * described above.
 *
 * This interface and the `InteractionContextData` interface allow for the creation of many
 * convenience methods that fill the `action` property (and/or other properties in future), e.g.
 * `MetricsClient#submitClick()`.
 *
 * @typedef {InteractionContextData} InteractionAction
 * @property {InteractionAction} action
 */

/**
 * @typedef {InteractionContextData} ElementInteractionData
 * @property {string} element_id
 * @property {string} element_friendly_name
 */

/**
 * @typedef {Object.<string,StreamConfig>} StreamConfigs
 */

/**
 * @typedef {Object} StreamConfig
 * @property {string} [schema_title]
 * @property {Object.<string,StreamProducerConfig>} [producers]
 * @property {StreamSamplingConfig} [sample]
 */

// ---

/**
 * @typedef {Object} StreamSamplingConfig
 * @property {string} unit
 * @property {number} rate
 */

// ---

/**
 * @typedef {Object} StreamProducerConfig
 * @property {string[]} [events]
 * @property {StreamSamplingConfig} [sampling]
 * @property {StreamProducerContextAttribute[]} [provide_values]
 * @property {StreamProducerCurationConfigs} [curation]
 */

/**
 * @typedef {Object.<string,StreamProducerContextAttribute>} StreamProducerCurationConfigs
 */

/**
 * @readonly
 * @enum {string}
 */
const StreamProducerContextAttribute = {

    // Agent
    agent_client_platform: 'agent_client_platform',
    agent_client_platform_family: 'agent_client_platform_family',

    // Page
    page_id: 'page_id',
    page_title: 'page_title',
    page_namespace: 'page_namespace',
    page_namespace_name: 'page_namespace_name',
    page_revision_id: 'page_revision_id',
    page_wikidata_id: 'page_wikidata_id',
    page_wikidata_qid: 'page_wikidata_id',
    page_content_language: 'page_content_language',
    page_is_redirect: 'page_is_redirect',
    page_user_groups_allowed_to_move: 'page_user_groups_allowed_to_move',
    page_user_groups_allowed_to_edit: 'page_user_groups_allowed_to_edit',

    // MediaWiki
    mediawiki_skin: 'mediawiki_skin',
    mediawiki_version: 'mediawiki_version',
    mediawiki_is_production: 'mediawiki_is_production',
    mediawiki_is_debug_mode: 'mediawiki_is_debug_mode',
    mediawiki_database: 'mediawiki_database',
    mediawiki_site_content_language: 'mediawiki_site_content_language',
    mediawiki_site_content_language_variant: 'mediawiki_site_content_language_variant',

    // Performer
    performer_is_logged_in: 'performer_is_logged_in',
    performer_id: 'performer_id',
    performer_name: 'performer_name',
    performer_session_id: 'performer_session_id',
    performer_active_browsing_session_token: 'performer_active_browsing_session_token',
    performer_pageview_id: 'performer_pageview_id',
    performer_groups: 'performer_groups',
    performer_is_bot: 'performer_is_bot',
    performer_is_temp: 'performer_is_temp',
    performer_language: 'performer_language',
    performer_language_variant: 'performer_language_variant',
    performer_can_probably_edit_page: 'performer_can_probably_edit_page',
    performer_edit_count: 'performer_edit_count',
    performer_edit_count_bucket: 'performer_edit_count_bucket',
    performer_registration_dt: 'performer_registration_dt'
};

/**
 * @typedef {Object} StreamProducerCurationConfig
 * @property {StreamProducerCurationOperand} [equals]
 * @property {StreamProducerCurationOperand} [not_equals]
 * @property {StreamProducerCurationOperand} [greater_than]
 * @property {number} [less_than]
 * @property {number} [less_than_or_equals]
 * @property {number} [greater_than_or_equals]
 * @property {StreamProducerCurationOperand[]} [in]
 * @property {StreamProducerCurationOperand[]} [not_in]
 * @property {StreamProducerCurationOperand} [contains]
 * @property {StreamProducerCurationOperand[]} [contains_all]
 * @property {StreamProducerCurationOperand[]} [contains_any]
 * @property {StreamProducerCurationOperand} [does_not_contain]
 */

/**
 * @typedef {string|number|boolean|null} StreamProducerCurationOperand
 */

/**
 * Client for producing events to [the Event Platform](https://wikitech.wikimedia.org/wiki/Event_Platform) and
 * [the Metrics Platform](https://wikitech.wikimedia.org/wiki/Metrics_Platform).
 *
 * @param {Integration} integration
 * @param {StreamConfigs|false} streamConfigs
 * @param {EventSubmitter} [eventSubmitter] An instance of {@link DefaultEventSubmitter} by default
 * @constructor
 * @class MetricsClient
 */
function MetricsClient(
	integration,
	streamConfigs,
	eventSubmitter
) {
	this.contextController = new ContextController( integration );
	this.samplingController = new SamplingController( integration );
	this.curationController = new CurationController();
	this.integration = integration;
	this.streamConfigs = streamConfigs;
	this.eventSubmitter = eventSubmitter || new DefaultEventSubmitter();
	this.eventNameToStreamNamesMap = null;
}

/**
 * @param {StreamConfigs|false} streamConfigs
 * @param {string} streamName
 * @return {StreamConfig|undefined}
 */
function getStreamConfigInternal( streamConfigs, streamName ) {
	// If streamConfigs are false, then stream config usage is not enabled.
	// Always return an empty object.
	//
	// FIXME
	//  The convention that disabling stream configuration results
	//  in enabling any caller to send any event, with no sampling,
	//  etc., is correct in the sense of the boolean logic, but
	//  counter-intuitive and likely hard to keep correct as more
	//  behavior is added. We should revisit.

	if ( streamConfigs === false ) {
		return {};
	}

	if ( !streamConfigs[ streamName ] ) {
		// In case no config has been assigned to the given streamName,
		// return undefined, so that the developer can discern between
		// a stream that is not configured, and a stream with config = {}.
		return undefined;
	}

	return streamConfigs[ streamName ];
}

/**
 * Gets a deep clone of the stream config.
 *
 * @param {string} streamName
 * @return {StreamConfig|undefined}
 */
MetricsClient.prototype.getStreamConfig = function ( streamName ) {
	const streamConfig = getStreamConfigInternal( this.streamConfigs, streamName );

	return streamConfig ? this.integration.clone( streamConfig ) : streamConfig;
};

/**
 * @param {StreamConfigs} streamConfigs
 * @return {Record<string, string[]>}
 */
function getEventNameToStreamNamesMap( streamConfigs ) {
	/** @type Record<string, string[]> */
	const result = {};

	for ( const streamName in streamConfigs ) {
		const streamConfig = streamConfigs[ streamName ];

		if (
			!streamConfig.producers ||
			!streamConfig.producers.metrics_platform_client ||
			!streamConfig.producers.metrics_platform_client.events
		) {
			continue;
		}

		let events = streamConfig.producers.metrics_platform_client.events;

		if ( typeof events === 'string' ) {
			events = [ events ];
		}

		for ( let i = 0; i < events.length; ++i ) {
			if ( !result[ events[ i ] ] ) {
				result[ events[ i ] ] = [];
			}

			result[ events[ i ] ].push( streamName );
		}
	}

	return result;
}

/**
 * Get the names of the streams associated with the event.
 *
 * A stream (S) can be associated with an event by configuring it as follows:
 *
 * ```
 * 'S' => [
 *   'producers' => [
 *     'metrics_platform_client' => [
 *       'events' => [
 *         'event1',
 *         'event2',
 *         // ...
 *       ],
 *     ],
 *   ],
 * ],
 * ```
 *
 * @param {string} eventName
 * @return {string[]}
 */
MetricsClient.prototype.getStreamNamesForEvent = function ( eventName ) {
	if ( this.streamConfigs === false ) {
		return [];
	}

	if ( !this.eventNameToStreamNamesMap ) {
		this.eventNameToStreamNamesMap = getEventNameToStreamNamesMap( this.streamConfigs );
	}

	/** @type string[] */
	let result = [];

	for ( const key in this.eventNameToStreamNamesMap ) {
		if ( eventName.indexOf( key ) === 0 ) {
			result = result.concat( this.eventNameToStreamNamesMap[ key ] );
		}
	}

	return result;
};

/**
 * Adds required fields:
 *
 * - `meta.stream`: the target stream name
 * - `meta.domain`: the domain associated with this event
 * - `dt`: the client-side timestamp (unless this is a migrated legacy event,
 *         in which case the timestamp will already be present as `client_dt`).
 *
 * @ignore
 *
 * @param {BaseEventData} eventData
 * @param {string} streamName
 * @return {BaseEventData}
 */
MetricsClient.prototype.addRequiredMetadata = function ( eventData, streamName ) {
	if ( eventData.meta ) {
		eventData.meta.stream = streamName;
		eventData.meta.domain = this.integration.getHostname();
	} else {
		eventData.meta = {
			stream: streamName,
			domain: this.integration.getHostname()
		};
	}

	//
	// The 'dt' field is reserved for the internal use of this library,
	// and should not be set by any other caller.
	//
	// (1) 'dt' is a client-side timestamp for new events
	//      and a server-side timestamp for legacy events.
	// (2) 'dt' will be provided by EventGate if omitted here,
	//      so it should be omitted for legacy events (and
	//      deleted if present).
	//
	// We detect legacy events by looking for the 'client_dt'.
	//
	if ( eventData.client_dt ) {
		delete eventData.dt;
	} else {
		eventData.dt = eventData.dt || new Date().toISOString();
	}

	return eventData;
};

/**
 * Submit an event to a stream.
 *
 * The event (E) is submitted to the stream (S) if E has the `$schema` property and S is in
 * sample. If E does not have the `$schema` property, then a warning is logged.
 *
 * @param {string} streamName The name of the stream to send the event data to
 * @param {BaseEventData} eventData The event data
 *
 * @stable
 */
MetricsClient.prototype.submit = function ( streamName, eventData ) {
	const result = this.validateSubmitCall( streamName, eventData );

	if ( result ) {
		this.processSubmitCall( new Date().toISOString(), streamName, eventData );
	}
};

/**
 * If `eventData` is falsy or does not have the `$schema` property set, then a warning is logged
 * and `false` is returned. Otherwise, `true` is returned.
 *
 * @ignore
 *
 * @param {string} streamName
 * @param {BaseEventData} eventData
 * @return {boolean}
 */
MetricsClient.prototype.validateSubmitCall = function ( streamName, eventData ) {
	if ( !eventData || !eventData.$schema ) {
		this.integration.logWarning(
			'submit( ' + streamName + ', eventData ) called with eventData missing required ' +
			'field "$schema". No event will be produced.'
		);

		return false;
	}

	return true;
};

/**
 * Processes the result of a call to {@link MetricsClient.prototype.submit}.
 *
 * @ignore
 *
 * @param {string} timestamp The ISO 8601 formatted timestamp of the original call
 * @param {string} streamName The name of the stream to send the event data to
 * @param {BaseEventData} eventData The event data
 */
MetricsClient.prototype.processSubmitCall = function ( timestamp, streamName, eventData ) {
	eventData.dt = timestamp;

	const streamConfig = getStreamConfigInternal( this.streamConfigs, streamName );

	if ( !streamConfig ) {
		return;
	}

	this.addRequiredMetadata( eventData, streamName );

	if ( this.samplingController.isStreamInSample( streamConfig ) ) {
		this.eventSubmitter.submitEvent( eventData );
	}
};

/**
 * Format the custom data so that it is compatible with the Metrics Platform Event schema.
 *
 * `customData` is considered valid if all of its keys are snake_case.
 *
 * @param {Record<string,any>|undefined} customData
 * @return {FormattedCustomData}
 * @throws {Error} If `customData` is invalid
 */
function getFormattedCustomData( customData ) {
	/** @type {Record<string,EventCustomDatum>} */
	const result = {};

	if ( !customData ) {
		return result;
	}

	for ( const key in customData ) {
		if ( !key.match( /^[$a-z]+[a-z0-9_]*$/ ) ) {
			throw new Error( 'The key "' + key + '" is not snake_case.' );
		}

		const value = customData[ key ];
		const type = value === null ? 'null' : typeof value;

		result[ key ] = {
			// eslint-disable-next-line camelcase
			data_type: type,
			value: String( value )
		};
	}

	return result;
}

/**
 * Construct and submits a Metrics Platform Event from the event name and custom data for each
 * stream that is interested in those events.
 *
 * The Metrics Platform Event for a stream (S) is constructed by first initializing the minimum
 * valid event (E) that can be submitted to S, and then mixing the context attributes requested
 * in the configuration for S into E.
 *
 * The Metrics Platform Event is submitted to a stream (S) if S is in sample and the event
 * is not filtered according to the filtering rules for S.
 *
 * @param {string} eventName
 * @param {Record<string, any>} [customData]
 *
 * @unstable
 * @deprecated
 */
MetricsClient.prototype.dispatch = function ( eventName, customData ) {
	const result = this.validateDispatchCall( eventName, customData );

	if ( result ) {
		this.processDispatchCall( new Date().toISOString(), eventName, result );
	}
};

/**
 * If `streamConfigs` is `false` or the custom data cannot be formatted with
 * {@link getFormattedCustomData}, then a warning is logged and `false` is returned. Otherwise, the
 * formatted custom data is returned.
 *
 * @ignore
 *
 * @param {string} eventName
 * @param {Record<string, any>} [customData]
 * @return {FormattedCustomData|false}
 */
MetricsClient.prototype.validateDispatchCall = function ( eventName, customData ) {
	// T309083
	if ( this.streamConfigs === false ) {
		this.integration.logWarning(
			'dispatch( ' + eventName + ', customData ) cannot dispatch events when stream configs are disabled.'
		);

		return false;
	}

	try {
		return getFormattedCustomData( customData );
	} catch ( e ) {
		this.integration.logWarning(
			'dispatch( ' + eventName + ', customData ) called with invalid customData: ' + e.message +
			'No event(s) will be produced.'
		);

		return false;
	}
};

/**
 * Processes the result of a call to {@link MetricsClient.prototype.dispatch}.
 *
 * NOTE: This method should only be called **after** the stream configs have been fetched via
 * {@link MetricsClient.prototype.fetchStreamConfigs}.
 *
 * @ignore
 *
 * @param {string} timestamp The ISO 8601 formatted timestamp of the original call
 * @param {string} eventName
 * @param {Record<string, any>} [formattedCustomData]
 */
MetricsClient.prototype.processDispatchCall = function (
	timestamp,
	eventName,
	formattedCustomData
) {
	const streamNames = this.getStreamNamesForEvent( eventName );

	// Produce the event(s)
	for ( let i = 0; i < streamNames.length; ++i ) {
		/* eslint-disable camelcase */
		/** @type {MetricsPlatformEventData} */
		const eventData = {
			$schema: SCHEMA,
			dt: timestamp,
			name: eventName
		};

		if ( formattedCustomData ) {
			eventData.custom_data = formattedCustomData;
		}
		/* eslint-enable camelcase */

		const streamName = streamNames[ i ];
		const streamConfig = getStreamConfigInternal( this.streamConfigs, streamName );

		if ( !streamConfig ) {
			// NOTE: This SHOULD never happen.
			continue;
		}

		this.addRequiredMetadata( eventData, streamName );
		this.contextController.addRequestedValues( eventData, streamConfig );

		if (
			this.samplingController.isStreamInSample( streamConfig ) &&
			this.curationController.shouldProduceEvent( eventData, streamConfig )
		) {
			this.eventSubmitter.submitEvent( eventData );
		}
	}
};

/**
 * Submit an interaction event to a stream.
 *
 * An interaction event is meant to represent a basic interaction with some target or some event
 * occurring, e.g. the user (**performer**) tapping/clicking a UI element, or an app notifying the
 * server of its current state.
 *
 * An interaction event (E) MUST validate against the
 * /analytics/product_metrics/web/base/1.0.0 schema. At the time of writing, this means that E
 * MUST have the `action` property and MAY have the following properties:
 *
 * `action_subtype`
 * `action_source`
 * `action_context`
 *
 * If E does not have the `action` property, then a warning is logged.
 *
 * @see https://wikitech.wikimedia.org/wiki/Metrics_Platform/JavaScript_API
 *
 * @unstable
 *
 * @param {string} streamName
 * @param {string} schemaID
 * @param {InteractionAction} action
 * @param {InteractionContextData} [interactionData]
 */
MetricsClient.prototype.submitInteraction = function (
	streamName,
	schemaID,
	action,
	interactionData
) {
	if ( !action ) {
		this.integration.logWarning(
			'submitInteraction( ' + streamName + ', ..., action ) ' +
			'called without required field "action". No event will be produced.'
		);

		return;
	}

	let currentUserExperiments = null;
	// The new experiments fragment is only available for web/base 1.3.0
	if ( schemaID === '/analytics/product_metrics/web/base/1.3.0' ) {
		currentUserExperiments = this.integration.getCurrentUserExperiments();
	}

	const eventData = Object.assign(
		{
			action
		},
		interactionData || {},
		{
			$schema: schemaID
		},
		currentUserExperiments
	);

	const streamConfig = getStreamConfigInternal( this.streamConfigs, streamName );

	if ( !streamConfig ) {
		return;
	}

	this.contextController.addRequestedValues( eventData, streamConfig );

	this.submit( streamName, eventData );
};

const WEB_BASE_SCHEMA_ID = '/analytics/product_metrics/web/base/1.3.0';
const WEB_BASE_STREAM_NAME = 'product_metrics.web_base';

/**
 * See `MetricsClient#submitInteraction()`.
 *
 * @unstable
 *
 * @param {string} streamName
 * @param {ElementInteractionData} interactionData
 *
 * @see https://wikitech.wikimedia.org/wiki/Metrics_Platform/JavaScript_API
 */
MetricsClient.prototype.submitClick = function ( streamName, interactionData ) {
	this.submitInteraction( streamName, WEB_BASE_SCHEMA_ID, 'click', interactionData );
};

/**
 *  Checks if a stream is in or out of sample.
 *
 * @param {string} streamName
 * @return {boolean}
 */
MetricsClient.prototype.isStreamInSample = function ( streamName ) {
	const streamConfig = getStreamConfigInternal( this.streamConfigs, streamName );

	return streamConfig ? this.samplingController.isStreamInSample( streamConfig ) : false;
};

/**
 * Creates a new {@link Instrument} instance, which is bound to this `MetricsClient` instance.
 *
 * @example
 * // Create a new instrument by name:
 *
 * const m = require( '/path/to/metrics-platform' ).createMetricsClient();
 * let i = m.newInstrument( 'my_instrument' );
 *
 * // … and by stream name/schema ID pair:
 *
 * i = m.newInstrument( 'my_stream_name', '/analytics/my/schema/id/1.0.0' );
 *
 * // … and by instrument name and stream name/schema ID pair:
 *
 * i = m.newInstrument( 'my_instrument', 'my_stream_name', '/analytics/my/schema/id/1.0.0' );
 *
 * @param {string} streamOrInstrumentName
 * @param {string} [streamNameOrSchemaID]
 * @param {string} [schemaID]
 * @return {Instrument}
 */
MetricsClient.prototype.newInstrument = function (
	streamOrInstrumentName,
	streamNameOrSchemaID,
	schemaID
) {
	let instrumentName;
	let streamName;

	if ( streamNameOrSchemaID === undefined ) {
		// #newInstrument( instrumentName )

		instrumentName = streamOrInstrumentName;

		const streamConfig = getStreamConfigInternal( this.streamConfigs, instrumentName );
		const overrideStreamName =
			streamConfig &&
			streamConfig.producers &&
			streamConfig.producers.metrics_platform_client &&
			streamConfig.producers.metrics_platform_client.stream_name;

		streamName = overrideStreamName || WEB_BASE_STREAM_NAME;
		schemaID = WEB_BASE_SCHEMA_ID;
	} else if ( schemaID === undefined ) {
		// #newInstrument( streamName, schemaID )

		streamName = streamOrInstrumentName;
		schemaID = streamNameOrSchemaID;
	} else {
		// #newInstrument( instrumentName, streamName, schemaID )

		instrumentName = streamOrInstrumentName;
		streamName = streamNameOrSchemaID;
	}

	const result = new Instrument( this, streamName, schemaID );

	if ( instrumentName ) {
		result.setInstrumentName( instrumentName );
	}

	return result;
};

/**
 *  Checks whether the user is enrolled in a specific experiment
 *
 * @param {string} experimentName
 * @return {boolean}
 */
MetricsClient.prototype.isCurrentUserEnrolled = function ( experimentName ) {
	return this.integration.isCurrentUserEnrolled( experimentName );
};

module.exports = MetricsClient;
module.exports.SCHEMA = SCHEMA;
