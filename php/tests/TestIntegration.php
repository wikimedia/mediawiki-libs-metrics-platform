<?php

namespace Wikimedia\Metrics\Test;

use Wikimedia\Metrics\Integration;

class TestIntegration implements Integration {

	/** @inheritDoc */
	public function getStreamConfigs() : array {
		return [];
	}

	/** @inheritDoc */
	public function getHostName() : string {
		return 'www.example.org';
	}

	/** @inheritDoc */
	public function getTimestamp() : string {
		return '1970-01-01T00:00:00.000Z';
	}

	/** @inheritDoc */
	public function send( array $event ) : void {
	}

}
