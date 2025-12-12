'use strict';

/**
 * @implements {MetricsPlatform.ExternalIntegration}
 * @memberof MetricsPlatform
 */

const NodeIntegration = require( './../../src/DefaultIntegration' );

/**
 * @return {string}
 */
NodeIntegration.prototype.getHostname = function () {
	return require( 'os' ).hostname();
};

NodeIntegration.prototype.getContextAttributes = function () {
	/* eslint-disable camelcase */
	return {
		agent: {
			client_platform_family: 'app'
		},
		mediawiki: {
			skin: 'minerva'
		}
	};
};

NodeIntegration.prototype.clone = function ( obj ) {
	return JSON.parse( JSON.stringify( obj ) );
};

module.exports = NodeIntegration;
