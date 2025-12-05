<?php

namespace Wikimedia\MetricsPlatform\Tests;

require_once __DIR__ . '/TestIntegration.php';
require_once __DIR__ . '/TestEventSubmitter.php';

use DateTimeImmutable;
use PHPUnit\Framework\TestCase;
use Wikimedia\MetricsPlatform\MetricsClient;
use Wikimedia\MetricsPlatform\StreamConfig\StreamConfigFactory;

/**
 * @covers \Wikimedia\MetricsPlatform\MetricsClient
 */
class MetricsClientTest extends TestCase {
	use TestHelperTrait;

	private TestEventSubmitter $eventSubmitter;

	private TestIntegration $integration;

	private StreamConfigFactory $config;

	private MetricsClient $client;

	private array $streamConfigs = [
		'test.event' => [],
		'test.metrics_platform.interactions' => [
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

		$this->assertSame(
			[ $streamName, $event ],
			$submission,
			'#submit() should proxy to EventSubmitter#submit()'
		);
	}

	private function assertIsValidTimestamp( string $timestamp ): void {
		$this->assertStringEndsWith( 'Z', $timestamp );
		$this->assertInstanceOf(
			DateTimeImmutable::class,
			DateTimeImmutable::createFromFormat( DateTimeImmutable::ATOM, $timestamp )
		);
	}

	public function testSubmitInteraction(): void {
		$eventName = 'test_action';

		$this->client->submitInteraction(
			$this->testStreamName,
			MetricsClient::BASE_SCHEMA,
			$eventName,
			$this->getTestInteractionData()
		);
		[ $actualStreamName, $actualEvent ] = $this->eventSubmitter->getSubmissions()[0];
		$expectedEvent = $this->getTestInteractionEvent( $eventName );

		$this->assertSame( $this->testStreamName, $actualStreamName );
		$this->assertEventSame(
			$expectedEvent,
			$actualEvent,
			true,
			'#submitInteraction() submits event correctly.'
		);
	}

	public function testSubmitInteractionCannotOverrideSchemaDt(): void {
		$eventName = 'test_action';

		$this->client->submitInteraction(
			$this->testStreamName,
			MetricsClient::BASE_SCHEMA,
			$eventName,
			[
				'$schema' => 'foo',
				'dt' => 'bar',
			]
		);
		[ , $actualEvent ] = $this->eventSubmitter->getSubmissions()[0];

		$this->assertSame( MetricsClient::BASE_SCHEMA, $actualEvent['$schema'] );

		$this->assertNotSame( 'bar', $actualEvent['dt'] );
		$this->assertIsValidTimestamp( $actualEvent['dt'] );
	}

	public function testSubmitInteractionWithUndefinedStream(): void {
		$this->client->submitInteraction(
			'undefined_stream',
			MetricsClient::BASE_SCHEMA,
			'test_action',
			[]
		);

		$this->assertSame( [], $this->eventSubmitter->getSubmissions() );
	}

	public function testSubmitInteractionAddsRequestedValues(): void {
		$this->client->submitInteraction(
			'test.metrics_platform.interactions',
			MetricsClient::BASE_SCHEMA,
			'test_action',
			[]
		);

		[ , $event ] = $this->eventSubmitter->getSubmissions()[0];

		$this->assertArrayHasKey( 'agent', $event );
		$this->assertArrayHasKey( 'mediawiki', $event );
	}

	public function testSubmitClick(): void {
		$this->client->submitClick(
			$this->testStreamName,
			$this->getTestInteractionData()
		);
		[ $actualStreamName, $actualEvent ] = $this->eventSubmitter->getSubmissions()[0];
		$expectedEvent = $this->getTestInteractionEvent( 'click' );

		$this->assertSame( $this->testStreamName, $actualStreamName );
		$this->assertEventSame(
			$expectedEvent,
			$actualEvent,
			true,
			'#submitClick() submits event correctly.'
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
