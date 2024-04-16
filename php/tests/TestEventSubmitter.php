<?php

namespace Wikimedia\MetricsPlatform\Tests;

use Wikimedia\MetricsPlatform\EventSubmitter;

class TestEventSubmitter implements EventSubmitter {

	/** @var array */
	private $submissions = [];

	/** @inheritDoc */
	public function submit( string $streamName, array $event ): void {
		$this->submissions[] = [ $streamName, $event ];
	}

	/**
	 * @return array
	 */
	public function getSubmissions(): array {
		return $this->submissions;
	}
}
