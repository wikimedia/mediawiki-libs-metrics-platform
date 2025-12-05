'use strict';

const NodeIntegration = require( './../../src/DefaultIntegration' );

/**
 * @implements {MetricsPlatform.Integration}
 * @memberof MetricsPlatform
 */

/**
 * @return {string}
 */
NodeIntegration.prototype.getHostname = function () {
	return require( 'os' ).hostname();
};

NodeIntegration.prototype.getContextAttributes = function () {
	return {
		agent: {
			client_platform_family: 'app'
		},
		mediawiki: {
			skin: 'minerva'
		}
	}
}

module.exports = NodeIntegration;
