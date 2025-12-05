/* eslint-disable jsdoc/require-returns-check,no-unused-vars */

/**
 * @constructor
 * @implements {MetricsPlatform.Logger}
 * @memberof MetricsPlatform
 */
function DefaultLogger() {}

/**
 * Logs the warning to whatever logging backend that the execution environment, e.g. the
 * console.
 *
 * @param {string} message
 */
DefaultLogger.prototype.logWarning = function ( message ) {
	// eslint-disable-next-line no-console
	console.warn( message );
};

module.exports = DefaultLogger;
