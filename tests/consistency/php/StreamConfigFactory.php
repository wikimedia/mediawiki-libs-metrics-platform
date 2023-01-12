<?php

namespace Wikimedia\MetricsPlatform\Tests\Consistency;

use Wikimedia\MetricsPlatform\StreamConfig\StreamConfigFactory as BaseStreamConfigFactory;

class StreamConfigFactory extends BaseStreamConfigFactory {
	public function __construct( $filename ) {
		$rawContents = file_get_contents( $filename );
		$contents = json_decode( $rawContents, true );

		parent::__construct( $contents["streams"] );
	}
}
