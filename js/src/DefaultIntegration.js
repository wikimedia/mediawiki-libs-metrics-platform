/* eslint-disable jsdoc/require-returns-check,no-unused-vars */

const copyAttributes = require( './ContextUtils.js' ).copyAttributes;

const DEFAULT_STREAM_CONFIGS_ORIGIN = 'https://meta.wikimedia.org';
const DEFAULT_EVENTGATE_ORIGIN = 'https://intake-analytics.wikimedia.org';

/**
 * @param {string} origin
 * @return {string}
 */
function getStreamConfigsUrl( origin ) {
	const result = new URL( origin );

	result.pathname = '/w/api.php';

	[
		[ 'action', 'streamconfigs' ],
		[ 'format', 'json' ],
		[ 'formatversion', '2' ],

		// Querystring parameters specific to the EventStreamConfigs MediaWiki extension:
		[ 'constraints', 'destination_event_service=eventgate-analytics-external' ]
	].forEach( function ( part ) {
		result.searchParams.set( part[ 0 ], part[ 1 ] );
	} );

	return result.toString();
}

/**
 * @param {string} origin
 * @return {string}
 */
function getEventGateUrl( origin ) {
	const result = new URL( origin );

	result.pathname = '/v1/events';
	result.searchParams.set( 'hasty', 'true' );

	return result.toString();
}

/**
 * @constructor
 * @param {string} [streamConfigsOrigin] The origin of the MediaWiki instance to fetch the stream
 *  configs from. `https://meta.mediawiki.org` by default
 * @param {string} [eventGateOrigin] The origin of the EventGate event intake service to send
 *  events to. `https://intake-analytics.wikimedia.org` by default
 */
function DefaultIntegration( streamConfigsOrigin, eventGateOrigin ) {
	this.streamConfigsUrl = getStreamConfigsUrl(
		streamConfigsOrigin || DEFAULT_STREAM_CONFIGS_ORIGIN
	);
	this.eventGateUrl = getEventGateUrl( eventGateOrigin || DEFAULT_EVENTGATE_ORIGIN );

	/** @type {ContextAttributes} */
	this.contextAttributes = {};
}

/**
 * Fetches stream configs from some source, remote or local.
 *
 * @return {Promise<StreamConfigs>}
 */
DefaultIntegration.prototype.fetchStreamConfigs = function () {
	return fetch( this.streamConfigsUrl )
		.then( function ( response ) {
			return response.json();
		} )
		.then( function ( json ) {
			return json.streams;
		} );
};

/**
 * Enqueues the event to be sent to the event intake service.
 *
 * @param {EventData} eventData
 */
DefaultIntegration.prototype.enqueueEvent = function ( eventData ) {
	navigator.sendBeacon(
		this.eventGateUrl,
		JSON.stringify( eventData )
	);
};

/**
 * Called when an event is enqueued to be submitted to the event ingestion service.
 *
 * @param {string} streamName
 * @param {EventData} eventData
 */
DefaultIntegration.prototype.onSubmit = function ( streamName, eventData ) {
	// eslint-disable-next-line no-console
	console.info( 'Submitted the following event to ' + streamName + ':', eventData );
};

/**
 * Gets the hostname of the current document.
 *
 * @return {string}
 */
DefaultIntegration.prototype.getHostname = function () {
	return location.hostname;
};

/**
 * Logs the warning to whatever logging backend that the execution environment, e.g. the
 * console.
 *
 * @param {string} message
 */
DefaultIntegration.prototype.logWarning = function ( message ) {
	// eslint-disable-next-line no-console
	console.warn( message );
};

/**
 * Gets a deep clone of the object.
 *
 * @param {Object} _obj
 */
DefaultIntegration.prototype.clone = function ( _obj ) {
	throw new Error( 'Not implemented!' );
};

/**
 * Gets the values for those context attributes that are available in the execution
 * environment.
 *
 * @return {ContextAttributes}
 */
DefaultIntegration.prototype.getContextAttributes = function () {
	return this.contextAttributes;
};

// NOTE: The following are required for compatibility with the current impl. but the
// information is also available via ::getContextualAttributes() above.

/**
 * Gets a pageview ID.
 *
 * A pageview ID is a token that should uniquely identify a pageview.
 *
 * @return {string}
 */
DefaultIntegration.prototype.getPageviewId = function () {
	throw new Error( 'Not implemented yet.' );
};

/**
 * Gets the session ID.
 *
 * A session ID is a token that should uniquely identify a browsing session.
 *
 * @return {string}
 */
DefaultIntegration.prototype.getSessionId = function () {
	throw new Error( 'Not implemented yet.' );
};

// ---

/**
 * Copies the given context attributes so that they can be mixed into events.
 *
 * @param {ContextAttributes} contextAttributes
 */
DefaultIntegration.prototype.setContextAttributes = function ( contextAttributes ) {
	copyAttributes( contextAttributes, this.contextAttributes );
};

module.exports = DefaultIntegration;
