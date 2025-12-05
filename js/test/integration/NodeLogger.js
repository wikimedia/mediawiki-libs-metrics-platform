'use strict';

/**
 * @implements {MetricsPlatform.Logger}
 * @memberof MetricsPlatform
 */

const NodeLogger = require( './../../src/DefaultLogger' );

/**
 * Logs the warning to whatever logging backend that the execution environment, e.g. the
 * console.
 *
 * @param {string} message
 */
NodeLogger.prototype.logWarning = function ( message ) {
	console.warn( message );
};

module.exports = NodeLogger;
