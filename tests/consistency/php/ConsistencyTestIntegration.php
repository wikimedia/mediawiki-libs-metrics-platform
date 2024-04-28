<?php

namespace Wikimedia\MetricsPlatform\Tests\Consistency;

use Wikimedia\MetricsPlatform\Integration;

class ConsistencyTestIntegration implements Integration {

	/** @var array */
	private $data;

	public function __construct( $filename ) {
		$rawContents = file_get_contents( $filename );

		$this->data = json_decode( $rawContents, true );
	}

	public function getContextAttribute( string $name ) {
		list( $primaryKey, $secondaryKey ) = explode( '_', $name, 2 );

		return $this->data[$primaryKey][$secondaryKey] ?? null;
	}
}
