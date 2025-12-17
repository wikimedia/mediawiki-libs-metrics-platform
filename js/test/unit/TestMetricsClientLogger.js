'use strict';

/**
 * @constructor
 */
function TestMetricsClientLogger() {}

/**
 * @param {string} string
 */
TestMetricsClientLogger.prototype.logWarning = function ( string ) {
	console.warn( string );
};

module.exports = TestMetricsClientLogger;
