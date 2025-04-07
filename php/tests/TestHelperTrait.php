<?php

namespace Wikimedia\MetricsPlatform\Tests;

use Wikimedia\MetricsPlatform\MetricsClient;
use Wikimedia\Timestamp\ConvertibleTimestamp;

trait TestHelperTrait {
	/** @var string */
	private string $testStreamName = 'test.metrics_platform.interactions';

	/** @var array */
	private $experimentEnrollment = [
		'active_experiments' => [
			'experiment1',
			'experiment2',
			'experiment3',
			'experiment4',
			'test_experiment',
		],
		'enrolled' => [
			'experiment1',
			'test_experiment',
		],
		'assigned' => [
			'experiment1' => 'A',
			'test_experiment' => 'test_assignment',
		],
		'subject_ids' => [
			'experiment1' => 'poaiusdfapoiupaosdf',
			'test_experiment' => 'test_subject_id',
		],
		'sampling_units' => [
			'experiment1' => 'mw-user',
			'test_experiment' => 'test_sampling_unit',
		],
		'coordinators' => [
			'experiment1' => 'xLab',
			'test_experiment' => 'xLab',
		]
	];

	private function createTestInteractionEvent(): array {
		return [
			'$schema' => MetricsClient::BASE_SCHEMA,
			'dt' => ConvertibleTimeStamp::now( TS_ISO_8601 )
		];
	}

	/**
	 * @param string $eventName
	 */
	private function getTestInteractionEvent( string $eventName ): array {
		$eventBase = $this->createTestInteractionEvent();
		$interactionData = $this->getTestInteractionData();
		$interactionData['action'] = $eventName;
		return array_merge( $eventBase, $interactionData );
	}

	private function getTestInteractionData(): array {
		return [
			'action_subtype' => 'test_action_subtype',
			'action_source' => 'test_action_source',
			'action_context' => 'test_action_context',
			'element_id' => 'test_element_id',
			'element_friendly_name' => 'test_element_friendly_name',
			'funnel_name' => 'test_funnel_name',
			'funnel_entry_token' => 'test_funnel_entry_token',
			'funnel_event_sequence_position' => 1
		];
	}

	/**
	 * @param string $eventName
	 */
	private function getTestInteractionWithExperimentDetailsEvent( string $eventName, string $experimentName ): array {
		return array_merge(
			$this->getTestInteractionEvent( $eventName ),
			$this->getExperimentData( $experimentName )
		);
	}

	private function getTestInteractionWithExperimentDetailsData( string $eventName, string $experimentName ): array {
		return array_merge(
			$this->getTestInteractionEvent( $eventName ),
			$this->getExperimentData( $experimentName )
		);
	}

	private function getExperimentData( string $experimentName ): array {
		return [
			'experiment' => [
				'enrolled' => $experimentName,
				'assigned' => 'test_assignment',
				'subject_id' => 'test_subject_id',
				'sampling_unit' => 'test_sampling_unit',
				'other_assigned' => [ 'experiment1' => 'A' ],
				'coordinator' => 'xLab'
			]
		];
	}

	/**
	 * @param string $eventName
	 */
	private function getInstrumentTestInteractionEvent( string $eventName ): array {
		$eventBase = $this->createTestInteractionEvent();
		$interactionData = $this->getInstrumentTestInteractionData();
		$interactionData['action'] = $eventName;
		return array_merge( $eventBase, $interactionData );
	}

	private function getInstrumentTestInteractionData(): array {
		return array_merge(
			$this->getTestInteractionData(),
			[ 'instrument_name' => 'test_instrument_name' ]
		);
	}

	private function assertEventSame(
		array $expectedEvent,
		array $actualEvent,
		bool $ignoreContextAttributes = false,
		string $message = ''
	): void {
		$keys = [ 'dt' ];

		if ( $ignoreContextAttributes ) {
			$keys = array_merge( $keys, [
				'agent',
				'page',
				'mediawiki',
				'performer',
			] );
		}

		foreach ( $keys as $key ) {
			unset( $expectedEvent[$key], $actualEvent[$key] );
		}

		$this->assertEquals( $expectedEvent, $actualEvent, $message );
	}
}
