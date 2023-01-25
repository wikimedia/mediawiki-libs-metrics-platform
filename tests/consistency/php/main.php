<?php

namespace Wikimedia\MetricsPlatform\Tests\Consistency;

require __DIR__ . '/../../../vendor/autoload.php';

use Wikimedia\MetricsPlatform\MetricsClient;

require __DIR__ . '/ConsistencyTestEventSubmitter.php';
require __DIR__ . '/ConsistencyTestIntegration.php';
require __DIR__ . '/StreamConfigFactory.php';

$eventSubmitter = new ConsistencyTestEventSubmitter( __DIR__ . '/../integration_data.json' );
$integration = new ConsistencyTestIntegration( __DIR__ . '/../integration_data.json' );
$streamConfigFactory = new StreamConfigFactory( __DIR__ . '/../stream_configs.json' );

( new MetricsClient( $eventSubmitter, $integration, $streamConfigFactory ) )->dispatch(
	'test_consistency_event',
	[
		'test' => 'consistency',
	]
);
