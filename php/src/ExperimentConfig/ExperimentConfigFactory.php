<?php

namespace Wikimedia\MetricsPlatform\ExperimentConfig;

class ExperimentConfigFactory {
	/** @var array|false */
	protected $experimentEnrollmentConfigs;

	/**
	 * @param array|false $experimentEnrollmentConfigs
	 */
	public function __construct( $experimentEnrollmentConfigs ) {
		$this->experimentEnrollmentConfigs = $experimentEnrollmentConfigs;
	}

	public function getExperimentConfig( string $experimentName ): ExperimentConfig {
		if ( $this->experimentEnrollmentConfigs === false ) {
			return new ExperimentConfig( [] );
		}

		return new ExperimentConfig( $this->experimentEnrollmentConfigs );
	}

	public function getCurrentUserExperiment( string $experimentName ): array {
		if ( in_array( $experimentName, $this->experimentEnrollmentConfigs['active_experiments'], true ) ||
			 in_array( $experimentName, $this->experimentEnrollmentConfigs['enrolled'], true )
		) {
			$otherAssigned = $this->experimentEnrollmentConfigs['assigned'];
			if ( array_key_exists( $experimentName, $otherAssigned ) ) {
				unset( $otherAssigned[$experimentName] );
			}

			return [
				'enrolled' => $experimentName,
				'assigned' => $this->experimentEnrollmentConfigs['assigned'][ $experimentName ],
				'subject_id' => $this->experimentEnrollmentConfigs['subject_ids'][ $experimentName ],
				'sampling_unit' => $this->experimentEnrollmentConfigs['sampling_units'][ $experimentName ],
				'other_assigned' => $otherAssigned,
				'coordinator' => $this->experimentEnrollmentConfigs['coordinators'][ $experimentName ]
			];
		}
		return [];
	}

	public function getExperimentName( array $interactionData ): ?string {
		if ( array_key_exists( 'experiment', $interactionData ) ) {
			$experiment = $interactionData['experiment'];
			return $experiment['enrolled'];
		}
		return null;
	}
}
