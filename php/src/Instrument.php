<?php

namespace Wikimedia\MetricsPlatform;

class Instrument {

	/** @var MetricsClient */
	private MetricsClient $metricsClient;

	/** @var string */
	private string $streamName;

	/** @var string */
	private string $schemaId;

	/** @var int */
	private int $eventSequencePosition;

	/** @var ?string */
	private ?string $instrumentName;

	/**
	 * @param MetricsClient $metricsClient
	 * @param string $streamName
	 * @param string $schemaId
	 */
	public function __construct(
		MetricsClient $metricsClient,
		string $streamName,
		string $schemaId,
		?string $instrumentName = null
	) {
		$this->metricsClient = $metricsClient;
		$this->streamName = $streamName;
		$this->schemaId = $schemaId;
		$this->instrumentName = $instrumentName;
		$this->eventSequencePosition = 1;
	}

	public function submitInteraction(
		string $action,
		array $interactionData = []
	): void {
		$interactionData = array_merge(
			[
				'instrument_name' => $this->instrumentName,
				'action' => $action,
				'funnel_event_sequence_position' => $this->eventSequencePosition++,
			],
			$interactionData
		);

		$this->metricsClient->submitInteraction(
			$this->streamName,
			$this->schemaId,
			$action,
			$interactionData
		);
	}

	public function submitClick( array $interactionData = [] ): void {
		$interactionData['funnel_event_sequence_position'] = $this->eventSequencePosition++;
		$this->metricsClient->submitClick( $this->streamName, $interactionData );
	}
}
