/* eslint-disable jsdoc/require-returns-check,no-unused-vars */

const copyAttributes = require( './ContextUtils.js' ).copyAttributes;

const DEFAULT_STREAM_CONFIGS_ORIGIN = 'https://meta.wikimedia.org';

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
	].forEach( ( part ) => {
		result.searchParams.set( part[ 0 ], part[ 1 ] );
	} );

	return result.toString();
}

/**
 * @constructor
 * @param {string} [streamConfigsOrigin] The origin of the MediaWiki instance to fetch the stream
 *  configs from. `https://meta.mediawiki.org` by default
 */
function DefaultIntegration( streamConfigsOrigin ) {
	this.streamConfigsUrl = getStreamConfigsUrl(
		streamConfigsOrigin || DEFAULT_STREAM_CONFIGS_ORIGIN
	);

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

/**
 * Gets the experiment enrollment and membership for the current user.
 *
 * A experiment detail.
 *
 * @return {object}
 */
DefaultIntegration.prototype.getCurrentUserExperiments = function () {
	throw new Error( 'Not implemented yet.' );
};

/**
 * check whether the current user is enrolled in a specific experimet.
 *
 * @param {string} experimentName
 * @return {boolean}
 */
DefaultIntegration.prototype.isCurrentUserEnrolled = function ( experimentName ) {
	throw new Error( 'Not implemented yet.' );
};

module.exports = DefaultIntegration;
