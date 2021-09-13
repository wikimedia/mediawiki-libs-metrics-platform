/* eslint-disable no-use-before-define, no-unused-vars, jsdoc/require-returns-check */
/**
 * Default integration layer for the metrics client.
 *
 * This is intended as an interface. Client integration code should implement all stubbed methods.
 */
( function () {

	/**
	 * @constructor
	 */
	function IMetricsClientIntegration() {}

	/**
	 * Handle the actual event submission.
	 *
	 * @param {!Object} eventData
	 */
	IMetricsClientIntegration.prototype.enqueueEvent = function ( eventData ) {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getHostname = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * Get a random ID string in the format expected for Mediawiki clients.
	 *
	 * TODO: Replace this with a v4 UUID.
	 *
	 * @return {!string}
	 */
	IMetricsClientIntegration.prototype.generateRandomId = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @param {!string} string
	 */
	IMetricsClientIntegration.prototype.logWarning = function ( string ) {
		throw new Error( 'Stub!' );
	};

	// MediaWiki context accessors

	// Page

	/**
	 * @return {?number}
	 */
	IMetricsClientIntegration.prototype.getPageId = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?number}
	 */
	IMetricsClientIntegration.prototype.getPageNamespaceId = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getPageNamespaceText = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getPageTitle = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?boolean}
	 */
	IMetricsClientIntegration.prototype.getPageIsRedirect = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?number}
	 */
	IMetricsClientIntegration.prototype.getPageRevisionId = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
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
	 * @return {?Array}
	 */
	IMetricsClientIntegration.prototype.getPageRestrictionEdit = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?Array}
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
	 * @return {?boolean}
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
	 * @return {?Array}
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
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getUserLanguage = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getUserLanguageVariant = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?boolean}
	 */
	IMetricsClientIntegration.prototype.getUserIsBot = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?boolean}
	 */
	IMetricsClientIntegration.prototype.getUserCanProbablyEditPage = function () {
		throw new Error( 'Stub!' );
	};

	// MediaWiki/Site

	/**
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getMediaWikiSkin = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getMediaWikiVersion = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
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
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getAccessMethod = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getPlatform = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?string}
	 */
	IMetricsClientIntegration.prototype.getPlatformFamily = function () {
		throw new Error( 'Stub!' );
	};

	/**
	 * @return {?boolean}
	 */
	IMetricsClientIntegration.prototype.isProduction = function () {
		throw new Error( 'Stub!' );
	};

	// Utility methods

	/**
	 * @return {?boolean}
	 */
	IMetricsClientIntegration.prototype.isDebugMode = function () {
		throw new Error( 'Stub!' );
	};

	IMetricsClientIntegration.prototype.clone = function ( obj ) {
		throw new Error( 'Stub!' );
	};

	function defaultNull( val ) {
		return val === undefined ? null : val;
	}

	module.exports = IMetricsClientIntegration;

}() );
