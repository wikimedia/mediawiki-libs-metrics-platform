/* eslint-disable jsdoc/require-returns-check,no-unused-vars */

const DEFAULT_STREAM_CONFIGS_ORIGIN = 'https://meta.wikimedia.org';

/**
 * @ignore
 *
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
	].forEach( ( part ) => {
		result.searchParams.set( part[ 0 ], part[ 1 ] );
	} );

	return result.toString();
}

/**
 * @param {string} [streamConfigsOrigin] The origin of the MediaWiki instance to fetch the stream
 *  configs from. `https://meta.mediawiki.org` by default
 * @constructor
 * @implements {MetricsPlatform.Integration}
 * @memberof MetricsPlatform
 */
function DefaultIntegration( streamConfigsOrigin ) {
	this.streamConfigsUrl = getStreamConfigsUrl(
		streamConfigsOrigin || DEFAULT_STREAM_CONFIGS_ORIGIN
	);

	/** @type {MetricsPlatform.Context.ContextAttributes} */
	this.contextAttributes = {};
}

/**
 * Fetches stream configs from some source, remote or local.
 *
 * @return {Promise<EventPlatform.StreamConfigs>}
 */
DefaultIntegration.prototype.fetchStreamConfigs = function () {
	return fetch( this.streamConfigsUrl )
		.then( ( response ) => response.json() )
		.then( ( json ) => json.streams );
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
 * Gets the hostname of the current document.
 *
 * @param {Object} obj
 * @return {string}
 */
DefaultIntegration.prototype.clone = function ( obj ) {
	throw new Error( 'Not implemented!' );
};

/**
 * Gets the values for those context attributes that are available in the execution
 * environment.
 *
 * @return {MetricsPlatform.Context.ContextAttributes}
 */
DefaultIntegration.prototype.getContextAttributes = function () {
	return this.contextAttributes;
};

module.exports = DefaultIntegration;
