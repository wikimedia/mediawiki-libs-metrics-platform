<?php

namespace Wikimedia\MetricsPlatform\Tests\Consistency;

require __DIR__ . '/../../../vendor/autoload.php';

use Wikimedia\MetricsPlatform\MetricsClient;

require __DIR__ . '/ConsistencyTestIntegration.php';
require __DIR__ . '/StreamConfigFactory.php';

$integration = new ConsistencyTestIntegration( __DIR__ . '/../integration_data.json' );
$streamConfigFactory = new StreamConfigFactory( __DIR__ . '/../stream_configs.json' );

( new MetricsClient( $integration, $streamConfigFactory ) )->dispatch(
	'test_consistency_event',
	[
		'test' => 'consistency',
	]
);
