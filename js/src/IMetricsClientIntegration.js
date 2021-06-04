/* eslint-disable no-unused-vars, jsdoc/require-returns-check */
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

	module.exports = IMetricsClientIntegration;

}() );
