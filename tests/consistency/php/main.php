<?php

namespace Wikimedia\Metrics\Tests\Consistency;

require __DIR__ . '/../../../vendor/autoload.php';

use Wikimedia\Metrics\MetricsClient;

require __DIR__ . '/Integration.php';
require __DIR__ . '/StreamConfigFactory.php';

$integration = new Integration( __DIR__ . '/../integration_data.json' );
$streamConfigFactory = new StreamConfigFactory( __DIR__ . '/../stream_configs.json' );

( new MetricsClient( $integration, $streamConfigFactory ) )->dispatch(
	'test_consistency_event',
	[
		'test' => 'consistency',
	]
);
