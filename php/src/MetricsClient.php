<?php

namespace Wikimedia\Metrics;

class MetricsClient {

	/** @var Integration */
	private $integration;

	/** @var ContextController */
	private $contextController;

	/** @var CurationController */
	private $curationController;

	/**
	 * @param Integration $integration
	 * @return MetricsClient
	 */
	public static function getInstance( Integration $integration ): MetricsClient {
		return new MetricsClient( $integration );
	}

	/**
	 * MetricsClient constructor.
	 *
	 * @param Integration $integration
	 */
	private function __construct( Integration $integration ) {
		$this->integration = $integration;
		$this->contextController = new ContextController( $integration );
		$this->curationController = new CurationController();
	}

	/**
	 * Submit an event according to the given stream's configuration.
	 *
	 * @param string $streamName
	 * @param array $event
	 * @return true if the event was submitted, otherwise false
	 */
	public function submit( string $streamName, array $event ): bool {
		if ( !isset( $event['$schema'] ) ) {
			return false;
		}
		$streamConfigs = $this->integration->getStreamConfigs();
		if ( !$streamConfigs ) {
			return false;
		}
		$streamConfig = $streamConfigs[$streamName];
		if ( !$streamConfig ) {
			return false;
		}
		$event = self::prepareEvent( $streamName, $event );
		$event = $this->contextController->addRequestedValues( $event, $streamConfig );
		if ( $this->curationController->shouldProduceEvent( $event, $streamConfig ) ) {
			$this->integration->send( $event );
			return true;
		}
		return false;
	}

	/**
	 * Prepares the event with extra data for submission.
	 * This will always set
	 * - `meta.stream` to $streamName
	 *
	 * This will optionally set (if not already in the event):
	 * - `dt` to the current time to represent the 'event time'
	 * - $eventDefaults
	 *
	 * @param string $streamName
	 * @param array $event
	 * @param array|null $eventDefaults
	 * @return array
	 */
	private function prepareEvent(
		string $streamName,
		array $event,
		array $eventDefaults = null
	): array {
		$requiredData = [
			// meta.stream should always be set to $streamName
			'meta' => [
				'stream' => $streamName
			]
		];

		$preparedEvent = array_merge_recursive(
			$eventDefaults ?? self::getEventDefaults(),
			$event,
			$requiredData
		);

		//
		// If this is a migrated legacy event, client_dt will have been set already by
		// EventLogging::encapsulate, and the dt field should be left unset so that it can be set
		// to the intake time by EventGate. If dt was set by a caller, we unset it here.
		//
		// If client_dt is absent, this schema is native to the Event Platform, and dt represents
		// the client-side event time. We set it here, overwriting any caller-provided value to
		// ensure consistency.
		//
		// https://phabricator.wikimedia.org/T277253
		// https://phabricator.wikimedia.org/T277330
		//
		if ( isset( $preparedEvent['client_dt'] ) ) {
			unset( $preparedEvent['dt'] );
		} else {
			$preparedEvent['dt'] = $this->integration->getTimestamp();
		}

		return $preparedEvent;
	}

	/**
	 * Returns values we always want set in events based on common
	 * schemas for all EventLogging events.  This sets:
	 *
	 * - meta.domain to the value of $config->get( 'ServerName' )
	 * - http.request_headers['user-agent'] to the value of $_SERVER( 'HTTP_USER_AGENT' ) ?? ''
	 *
	 * The returned object will be used as default values for the $event params passed
	 * to submit().
	 *
	 * @return array
	 */
	private function getEventDefaults(): array {
		return [
			'meta' => [
				'domain' => $this->integration->getHostName(),
			],
			'http' => [
				'request_headers' => [
					'user-agent' => $_SERVER['HTTP_USER_AGENT'] ?? ''
				]
			]
		];
	}

}
