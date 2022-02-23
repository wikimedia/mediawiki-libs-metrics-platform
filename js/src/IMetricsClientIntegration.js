/* eslint-disable no-unused-vars, jsdoc/require-returns-check */

/**
 * @param {*} val
 * @return {null|*}
 */
function defaultNull( val ) {
	return val === undefined ? null : val;
}

/**
 * Default integration layer for the metrics client.
 *
 * @interface
 */
function IMetricsClientIntegration() {}

/**
 * Handle the actual event submission.
 *
 * @param {EventData} _eventData
 * @return {void}
 */
IMetricsClientIntegration.prototype.enqueueEvent = function ( _eventData ) {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getHostname = function () {
	throw new Error( 'Stub!' );
};

/**
 * Get a random ID string in the format expected for Mediawiki clients.
 *
 * TODO: Replace this with a v4 UUID.
 *
 * @return {string}
 */
IMetricsClientIntegration.prototype.generateRandomId = function () {
	throw new Error( 'Stub!' );
};

/**
 * @param {string} _string
 * @return {void}
 */
IMetricsClientIntegration.prototype.logWarning = function ( _string ) {
	throw new Error( 'Stub!' );
};

// MediaWiki context accessors

// Page

/**
 * @return {number}
 */
IMetricsClientIntegration.prototype.getPageId = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {number}
 */
IMetricsClientIntegration.prototype.getPageNamespaceId = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getPageNamespaceText = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getPageTitle = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {boolean}
 */
IMetricsClientIntegration.prototype.getPageIsRedirect = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {number}
 */
IMetricsClientIntegration.prototype.getPageRevisionId = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getPageContentLanguage = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {?string}
 */
IMetricsClientIntegration.prototype.getPageWikidataId = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string[]}
 */
IMetricsClientIntegration.prototype.getPageRestrictionEdit = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string[]}
 */
IMetricsClientIntegration.prototype.getPageRestrictionMove = function () {
	throw new Error( 'Stub!' );
};

// User

/**
 * @return {?number}
 */
IMetricsClientIntegration.prototype.getUserId = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {boolean}
 */
IMetricsClientIntegration.prototype.getUserIsLoggedIn = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {?string}
 */
IMetricsClientIntegration.prototype.getUserName = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string[]}
 */
IMetricsClientIntegration.prototype.getUserGroups = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {?number}
 */
IMetricsClientIntegration.prototype.getUserEditCount = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {?string}
 */
IMetricsClientIntegration.prototype.getUserEditCountBucket = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {?number}
 */
IMetricsClientIntegration.prototype.getUserRegistrationTimestamp = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getUserLanguage = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getUserLanguageVariant = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {boolean}
 */
IMetricsClientIntegration.prototype.getUserIsBot = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {boolean}
 */
IMetricsClientIntegration.prototype.getUserCanProbablyEditPage = function () {
	throw new Error( 'Stub!' );
};

// MediaWiki/Site

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getMediaWikiSkin = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getMediaWikiVersion = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getMediaWikiSiteContentLanguage = function () {
	throw new Error( 'Stub!' );
};

// Device

/**
 * @return {?number}
 */
IMetricsClientIntegration.prototype.getDevicePixelRatio = function () {
	return defaultNull( window.devicePixelRatio );
};

/**
 * @return {?number}
 */
IMetricsClientIntegration.prototype.getDeviceHardwareConcurrency = function () {
	return defaultNull( navigator.hardwareConcurrency );
};

/**
 * @return {?number}
 */
IMetricsClientIntegration.prototype.getDeviceMaxTouchPoints = function () {
	// eslint-disable-next-line compat/compat
	return defaultNull( navigator.maxTouchPoints );
};

// Other

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getAccessMethod = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getPlatform = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {string}
 */
IMetricsClientIntegration.prototype.getPlatformFamily = function () {
	throw new Error( 'Stub!' );
};

/**
 * @return {boolean}
 */
IMetricsClientIntegration.prototype.isProduction = function () {
	throw new Error( 'Stub!' );
};

// Utility methods

/**
 * @return {boolean}
 */
IMetricsClientIntegration.prototype.isDebugMode = function () {
	throw new Error( 'Stub!' );
};

/**
 * @param {Object} _obj
 * @return {Object}
 */
IMetricsClientIntegration.prototype.clone = function ( _obj ) {
	throw new Error( 'Stub!' );
};

/**
 * Get the unique identifier for the current pageview.
 *
 * @return {!string}
 */
IMetricsClientIntegration.prototype.getPageviewId = function () {
	throw new Error( 'Stub!' );
};

/**
 * Get the unique identifier for the current session.
 *
 * @return {!string}
 */
IMetricsClientIntegration.prototype.getSessionId = function () {
	throw new Error( 'Stub!' );
};

module.exports = IMetricsClientIntegration;
