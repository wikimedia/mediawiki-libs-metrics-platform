<?php

namespace Wikimedia\MetricsPlatform\Tests;

use Wikimedia\MetricsPlatform\MetricsClient;
use Wikimedia\Timestamp\ConvertibleTimestamp;

trait TestHelperTrait {
	/** @var string */
	private string $testStreamName = 'test.metrics_platform.interactions';

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
			'funnel_event_sequence_position' => 108
		];
	}
}
