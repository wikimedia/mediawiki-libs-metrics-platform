<?php

namespace Wikimedia\MetricsPlatform\ExperimentConfig;

class ExperimentConfig {

	/** @var array */
	private $experimentConfig;

	/**
	 * @param array $experimentEnrollments
	 */
	public function __construct( array $experimentEnrollments ) {
		$this->experimentConfig = $experimentEnrollments;
	}

	public function getExperimentConfig(): array {
		return $this->experimentConfig;
	}
}
