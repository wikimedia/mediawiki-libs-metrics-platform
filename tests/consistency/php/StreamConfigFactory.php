<?php

namespace Wikimedia\Metrics\Tests\Consistency;

use Wikimedia\Metrics\StreamConfig\StreamConfigFactory as BaseStreamConfigFactory;

class StreamConfigFactory extends BaseStreamConfigFactory {
	public function __construct( $filename ) {
		$rawContents = file_get_contents( $filename );
		$contents = json_decode( $rawContents, true );

		parent::__construct( $contents );
	}
}
