'use strict';

/* eslint-disable camelcase */

const MetricsClient = require( './../../src/ExternalMetricsClient' );
const Integration = require( './NodeIntegration' );
const Logger = require( './NodeLogger' );
const NodeEventSubmitter = require( '../../src/NodeEventSubmitter' );

const integration = new Integration( 'http://localhost:8080' );
const logger = new Logger();
const eventSubmitter = new NodeEventSubmitter( 'http://localhost:8192' );

const metricsClient = new MetricsClient( integration, logger, eventSubmitter );

setTimeout(
	() => {
		metricsClient.submitInteraction(
			'test.metrics_platform.interactions',
			'/analytics/product_metrics/web/base/2.0.0',
			'init'
		);

		metricsClient.submitInteraction(
			'test.metrics_platform.interactions',
			'/analytics/product_metrics/web/base/2.0.0',
			'click',
			{
				element_id: 'ca-edit',
				element_friendly_name: 'edit'
			}
		);
		metricsClient.submitInteraction(
			'test.metrics_platform.interactions',
			'/analytics/product_metrics/web/base/2.0.0',
			'click',
			{
				element_id: 'ca-talk',
				element_friendly_name: 'edit'
			}
		);
	},
	1000
);
