<?php

namespace Wikimedia\MetricsPlatform\Tests;

use Wikimedia\MetricsPlatform\EventSubmitter;

class TestEventSubmitter implements EventSubmitter {

	private array $submissions = [];

	/** @inheritDoc */
	public function submit( string $streamName, array $event ): void {
		$this->submissions[] = [ $streamName, $event ];
	}

	public function getSubmissions(): array {
		return $this->submissions;
	}
}
