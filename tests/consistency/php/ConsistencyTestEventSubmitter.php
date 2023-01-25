<?php

namespace Wikimedia\MetricsPlatform\Tests\Consistency;

use Wikimedia\MetricsPlatform\EventSubmitter;

class ConsistencyTestEventSubmitter implements EventSubmitter {

	/** @var string */
	private $hostName;

	public function __construct( $filename ) {
		$rawContents = file_get_contents( $filename );
		$contents = json_decode( $rawContents, true );

		$this->hostName = $contents['hostname'];
	}

	/** @inheritDoc */
	public function submit( string $streamName, array $event ): void {
		$event['meta'] = [
			'stream' => $streamName,
			'domain' => $this->hostName,
		];

		// Per ConsistencyTest#dispatch, only the PHP Event Platform Client sets the http property
		// of the event and so it is out of scope for the consistency test.

		echo json_encode( $event );
	}
}
