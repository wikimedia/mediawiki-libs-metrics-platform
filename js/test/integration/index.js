/* eslint-disable camelcase */

const MetricsClient = require( './../../src/ExternalMetricsClient' );
const Integration = require( './NodeIntegration' );

const integration = new Integration(
	'https://test.wikipedia.org',
	'http://localhost:8192'
);

integration.setContextAttributes( {
	mediawiki: {
		skin: 'minerva'
	}
} );

const metricsClient = new MetricsClient( integration );

// FIXME: Create a test.metrics_platform.instrumentation stream, which is interested in the "test."
//  events.
metricsClient.dispatch( 'web.ui.init' );
metricsClient.dispatch( 'web.ui.click', {
	el_id: 'ca-edit'
} );
metricsClient.dispatch( 'web.ui.click', {
	el_id: 'ca-talk'
} );
