const STREAM_CONFIGS = require( '../stream_configs.json' ).streams;

const Integration = require( './Integration' );
const MetricsClient = require( '../../../js/src/MetricsClient' );

const integration = new Integration( '../integration_data.json' );

( new MetricsClient( integration, STREAM_CONFIGS ) ).dispatch(
	'test_consistency_event',
	{
		test: 'consistency'
	}
);
