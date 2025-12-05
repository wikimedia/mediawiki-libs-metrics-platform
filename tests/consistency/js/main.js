const STREAM_CONFIGS = require( '../stream_configs.json' ).streams;

const Integration = require( './Integration' );
const EventSubmitter = require( './EventSubmitter' );
const MetricsClient = require( '../../../js/src/MetricsClient' );

const integration = new Integration( '../integration_data.json' );
const eventSubmitter = new EventSubmitter();

( new MetricsClient( integration, STREAM_CONFIGS, eventSubmitter ) ).submitInteraction(
	'test.consistency',
	'/analytics/product_metrics/web/base/1.5.0',
	'test_consistency_event',
	{
		test: 'consistency'
	}
);
