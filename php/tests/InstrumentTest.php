<?php

namespace Wikimedia\MetricsPlatform\Tests;

require_once __DIR__ . '/TestIntegration.php';
require_once __DIR__ . '/TestEventSubmitter.php';

use PHPUnit\Framework\TestCase;
use Wikimedia\MetricsPlatform\ExperimentConfig\ExperimentConfigFactory;
use Wikimedia\MetricsPlatform\Instrument;
use Wikimedia\MetricsPlatform\MetricsClient;
use Wikimedia\MetricsPlatform\StreamConfig\StreamConfigFactory;

/**
 * @covers \Wikimedia\MetricsPlatform\Instrument
 */
class InstrumentTest extends TestCase {
	use TestHelperTrait;

	/** @var string */
	private const STREAM_NAME = 'product_metrics.web_base';

	/** @var TestEventSubmitter */
	private $eventSubmitter;

	/** @var TestIntegration */
	private $integration;

	/** @var StreamConfigFactory */
	private $config;

	/** @var Instrument */
	private $instrument;

	/** @var MetricsClient */
	private $client;

	/** @var string */
	private $eventName;

	/** @var ExperimentConfigFactory */
	private $experimentConfigFactory;

	/** @var array */
	private $streamConfigs = [
		'test.stream' => [],
		'product_metrics.web_base' => [
			'schema_title' => 'analytics/product_metrics/web/base',
			'destination_event_service' => 'eventgate-analytics-external',
			'producers' => [
				'metrics_platform_client' => [
					'provide_values' => [
						'mediawiki_skin',
					],
				],
			],
		],
	];

	/** @inheritDoc */
	protected function setUp(): void {
		parent::setUp();

		$this->eventSubmitter = new TestEventSubmitter();
		$this->integration = new TestIntegration();
		$this->config = new StreamConfigFactory( $this->streamConfigs );
		$this->experimentConfigFactory = new ExperimentConfigFactory( $this->experimentEnrollment );
		$this->client = new MetricsClient(
			$this->eventSubmitter,
			$this->integration,
			$this->config,
			null,
			null,
			null,
			$this->experimentConfigFactory,
		);
		$this->instrument = new Instrument(
			$this->client,
			self::STREAM_NAME,
			$this->client::BASE_SCHEMA,
		);
		$this->eventName = 'test_action';
	}

	public function testSubmitInteraction(): void {
		$this->instrument->submitInteraction(
			$this->eventName,
			$this->getInstrumentTestInteractionData()
		);

		[ $actualStreamName, $actualEvent ] = $this->eventSubmitter->getSubmissions()[0];
		$expectedEvent = $this->getInstrumentTestInteractionEvent( $this->eventName );

		$this->assertSame( self::STREAM_NAME, $actualStreamName );
		$this->assertEventSame(
			$expectedEvent,
			$actualEvent,
			true,
			'#submitInteraction() submits event correctly.'
		);
	}

	public function testSubmitClick(): void {
		$this->instrument->submitClick( $this->getInstrumentTestInteractionData() );
		[ $actualStreamName, $actualEvent ] = $this->eventSubmitter->getSubmissions()[0];
		$expectedEvent = $this->getInstrumentTestInteractionEvent( 'click' );

		$this->assertSame( self::STREAM_NAME, $actualStreamName );
		$this->assertEventSame(
			$expectedEvent,
			$actualEvent,
			true,
			'#submitClick() submits event correctly.'
		);
	}

}
