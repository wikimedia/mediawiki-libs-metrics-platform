'use strict';

/* eslint-disable camelcase */

const MetricsClient = require( './../../src/ExternalMetricsClient' );
const Integration = require( './NodeIntegration' );
const NodeEventSubmitter = require( '../../src/NodeEventSubmitter' );

// eslint-disable-next-line no-unused-vars
const Instrument = require( '../../src/Instrument' );

const integration = new Integration( 'http://host.docker.internal:8080' );
const eventSubmitter = new NodeEventSubmitter( 'http://host.docker.internal:8192' );

integration.setContextAttributes( {
	agent: {
		client_platform_family: 'app'
	},
	mediawiki: {
		skin: 'minerva'
	}
} );

const metricsClient = new MetricsClient( integration, eventSubmitter );

metricsClient.dispatch( 'test.init' );
metricsClient.dispatch( 'test.click', {
	element_id: 'ca-edit'
} );
metricsClient.dispatch( 'test.click', {
	element_id: 'ca-talk'
} );

setTimeout(
	() => {
		/** @type {Instrument} */
		// @ts-ignore TS2339 TypeScript doesn't support ES5-style inheritance
		// (see https://github.com/microsoft/TypeScript/issues/18609)
		const i = metricsClient.newInstrument(
			'test.metrics_platform.interactions',
			'/analytics/product_metrics/web/base/1.3.0'
		);

		i.submitInteraction( 'init' );
		i.submitClick( {
			element_id: 'ca-edit',
			element_friendly_name: 'edit'
		} );
		i.submitClick( {
			element_id: 'ca-talk',
			element_friendly_name: 'talk'
		} );
	},
	1000
);
