/* eslint-disable camelcase */

/**
 * NOTE:
 *
 * Before running this script the following stream configs MUST be available from the wiki that the
 * stream configs are fetched from (default: http://localhost:8080):
 *
 * $wgEventStreams = [
 *   'test.metrics_platform.metrics_events' => [
 *     'schema_title' => 'fragment/analytics/product_metrics/interaction/common',
 *     'destination_event_service' => 'eventgate-analytics-external',
 *     'producers' => [
 *     'metrics_platform_client' => [
 *       'events' => [
 *         'test.',
 *       ],
 *       'provide_values' => [
 *         'mediawiki_skin',
 *       ],
 *     ],
 *   ],
 *   'test.metrics_platform.interactions' => [
 *     'schema_title' => 'fragment/analytics/product_metrics/interaction/common',
 *     'destination_event_service' => 'eventgate-analytics-external',
 *   ],
 * ];
 * ```
 */

const MetricsClient = require( './../../src/ExternalMetricsClient' );
const Integration = require( './NodeIntegration' );

const integration = new Integration(
	'http://localhost:8080',
	'http://localhost:8192'
);

integration.setContextAttributes( {
	mediawiki: {
		skin: 'minerva'
	}
} );

const metricsClient = new MetricsClient( integration );

metricsClient.dispatch( 'test.init' );
metricsClient.dispatch( 'test.click', {
	element_id: 'ca-edit'
} );
metricsClient.dispatch( 'test.click', {
	element_id: 'ca-talk'
} );

// @ts-ignore TS2339
metricsClient.submitInteraction(
	'test.metrics_platform.interactions',
	'/fragment/analytics/product_metrics/interaction/common/1.0.0',
	{
		action: 'init'
	}
);

// @ts-ignore TS2339
metricsClient.submitInteraction(
	'test.metrics_platform.interactions',
	'/fragment/analytics/product_metrics/interaction/common/1.0.0',
	{
		action: 'click',
		element_id: 'ca-edit'
	}
);

// @ts-ignore TS2339
metricsClient.submitInteraction(
	'test.metrics_platform.interactions',
	'/fragment/analytics/product_metrics/interaction/common/1.0.0',
	{
		action: 'click',
		element_id: 'ca-talk'
	}
);
