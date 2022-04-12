<?php

namespace Wikimedia\Metrics;

use Psr\Log\LoggerInterface;
use Psr\Log\NullLogger;
use Wikimedia\Metrics\StreamConfig\StreamConfigException;
use Wikimedia\Metrics\StreamConfig\StreamConfigFactory;

class MetricsClient {

	/** @var Integration */
	private $integration;

	/** @var ContextController */
	private $contextController;

	/** @var CurationController */
	private $curationController;

	/** @var StreamConfigFactory */
	private $streamConfigFactory;

	/** @var LoggerInterface */
	private $logger;

	/**
	 * MetricsClient constructor.
	 *
	 * @param Integration $integration
	 * @param StreamConfigFactory $streamConfigFactory
	 * @param ?LoggerInterface $logger
	 * @param ?ContextController $contextController
	 * @param ?CurationController $curationController
	 */
	public function __construct(
		Integration $integration,
		StreamConfigFactory $streamConfigFactory,
		?LoggerInterface $logger = null,
		?ContextController $contextController = null,
		?CurationController $curationController = null
	) {
		$this->integration = $integration;
		$this->streamConfigFactory = $streamConfigFactory;
		$this->logger = $logger ?? new NullLogger();
		$this->contextController = $contextController ?? new ContextController( $integration );
		$this->curationController = $curationController ?? new CurationController();
	}

	/**
	 * Try to submit an event according to the configuration of the given stream.
	 *
	 * An event (E) will be submitted to stream (S) if:
	 *
	 * 1. E has the $schema property set;
	 * 2. S has a valid configuration
	 * 3. E passes the configured curation rules for S
	 *
	 * @param string $streamName
	 * @param array $event
	 * @return bool true if the event was submitted, otherwise false
	 */
	public function submit( string $streamName, array $event ): bool {
		if ( !isset( $event['$schema'] ) ) {
			return false;
		}
		try {
			$streamConfig = $this->streamConfigFactory->getStreamConfig( $streamName );
		} catch ( StreamConfigException $e ) {
			$this->logger->error(
				'The configuration for stream {streamName} is invalid: {validationError}',
				[
					'streamName' => $streamName,
					'validationError' => $e->getMessage(),
				]
			);

			return false;
		}
		if ( !$streamConfig ) {
			return false;
		}
		$event = $this->prepareEvent( $streamName, $event );
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
	 * @return array
	 */
	private function prepareEvent( string $streamName, array $event ): array {
		$requiredData = [
			// meta.stream should always be set to $streamName
			'meta' => [
				'stream' => $streamName
			]
		];

		$preparedEvent = array_merge_recursive(
			self::getEventDefaults(),
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
