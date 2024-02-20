<?php

namespace Wikimedia\MetricsPlatform\Tests;

require_once __DIR__ . '/TestIntegration.php';
require_once __DIR__ . '/TestEventSubmitter.php';

use PHPUnit\Framework\TestCase;
use Wikimedia\MetricsPlatform\MetricsClient;
use Wikimedia\MetricsPlatform\StreamConfig\StreamConfigFactory;
use Wikimedia\TestingAccessWrapper;
use Wikimedia\Timestamp\ConvertibleTimestamp;

/**
 * @covers \Wikimedia\MetricsPlatform\MetricsClient
 */
class MetricsClientTest extends TestCase {
	use TestHelperTrait;

	/** @var TestEventSubmitter */
	private $eventSubmitter;

	/** @var TestIntegration */
	private $integration;

	/** @var StreamConfigFactory */
	private $config;

	/** @var MetricsClient */
	private $client;

	/** @var array */
	private $streamConfigs = [
		'test.event' => [],
		'test.event.legacy' => [],
		'test.event.mpc1' => [
			'producers' => [
				'metrics_platform_client' => [
					'events' => [
						'foo',
						'bar',
					],
				],
			],
		],
		'test.event.mpc2' => [
			'producers' => [
				'metrics_platform_client' => [
					'events' => [
						'bar',
					],
				],
			],
		],
		'test.metrics_platform.interactions' => [
			'schema_title' => 'analytics/product_metrics/web/base',
			'destination_event_service' => 'eventgate-analytics-external',
			'producers' => [
				'metrics_platform_client' => [
					'events' => [
						'test.',
					],
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
		$this->client = new MetricsClient(
			$this->eventSubmitter,
			$this->integration,
			$this->config
		);
	}

	public function testSubmit(): void {
		$streamName = 'test.event';
		$event = [
			'$schema' => '/test/event/1.0.0',
			'field_a' => 'A',
			'dt' => '2021-03-15T00:00:01Z',
		];

		$this->client->submit( $streamName, $event );

		$submission = $this->eventSubmitter->getSubmissions()[0];

		$this->assertEquals(
			[ $streamName, $event ],
			$submission,
			'#submit() should proxy to EventSubmitter#submit()'
		);
	}

	private function assertIsValidTimestamp( string $timestamp ) {
		$ts = TestingAccessWrapper::newFromClass( ConvertibleTimestamp::class );
		$this->assertMatchesRegularExpression( $ts->regexes['TS_ISO_8601'], $timestamp );
		$this->assertStringEndsWith( 'Z', $timestamp );
	}

	public function testSubmitInteraction(): void {
		$eventName = 'test_action';

		$this->client->submitInteraction(
			$this->testStreamName,
			MetricsClient::BASE_SCHEMA,
			$eventName,
			$this->getTestInteractionData()
		);
		$submission = $this->eventSubmitter->getSubmissions()[0];
		$event = $this->getTestInteractionEvent( $eventName );

		$this->assertEquals(
			$this->getFormattedTestInteractionEvent( $event, $submission ),
			$submission,
			'#submitInteraction() submits event correctly.'
		);
	}

	public function testSubmitClick(): void {
		$this->client->submitClick(
			$this->testStreamName,
			$this->getTestInteractionData()
		);
		$submission = $this->eventSubmitter->getSubmissions()[0];
		$event = $this->getTestInteractionEvent( 'click' );

		$this->assertEquals(
			$this->getFormattedTestInteractionEvent( $event, $submission ),
			$submission,
			'#submitClick() submits event correctly.'
		);
	}
}
