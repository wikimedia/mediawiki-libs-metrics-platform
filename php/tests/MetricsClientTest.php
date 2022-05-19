<?php

namespace Wikimedia\Metrics\Tests;

require_once __DIR__ . '/TestIntegration.php';

use Generator;
use PHPUnit\Framework\TestCase;
use Psr\Log\LoggerInterface;
use Wikimedia\Metrics\ContextController;
use Wikimedia\Metrics\CurationController;
use Wikimedia\Metrics\MetricsClient;
use Wikimedia\Metrics\StreamConfig\StreamConfig;
use Wikimedia\Metrics\StreamConfig\StreamConfigException;
use Wikimedia\Metrics\StreamConfig\StreamConfigFactory;
use Wikimedia\TestingAccessWrapper;
use Wikimedia\Timestamp\ConvertibleTimestamp;

/** @covers \Wikimedia\Metrics\MetricsClient */
class MetricsClientTest extends TestCase {

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
	];

	/** @inheritDoc */
	protected function setUp(): void {
		parent::setUp();

		$this->integration = new TestIntegration();
		$this->config = new StreamConfigFactory( $this->streamConfigs );
		$this->client = new MetricsClient( $this->integration, $this->config );
	}

	public function provideEvents(): Generator {
		yield [
			'stream' => 'test.event',
			'event' => [
				'$schema' => '/test/event/1.0.0',
				'field_a' => 'A'
			],
		];
		yield [
			'stream' => 'test.event',
			'event' => [
				'$schema' => '/test/event/1.0.0',
				'field_a' => 'A',
				'dt' => '2021-03-15T00:00:01Z',
			],
		];
		yield [
			'stream' => 'test.event.legacy',
			'event' => [
				'$schema' => '/test/event/legacy/1.0.0',
				'field_a' => 'A',
				'client_dt' => '2021-03-16T00:00:01Z',
			],
		];

		// If the event is a migrated legacy EventLogging event, then the dt property is unset.
		yield [
			'stream' => 'test.event.legacy',
			'event' => [
				'$schema' => '/test/event/legacy/1.0.0',
				'field_a' => 'A',
				'client_dt' => '2021-03-17T00:00:01Z',
				'dt' => '2021-03-17T00:00:01Z',
			]
		];
	}

	/**
	 * @dataProvider provideEvents
	 *
	 * @param string $stream
	 * @param array $event
	 */
	public function testSubmit( string $stream, array $event ): void {
		$this->client->submit( $stream, $event );
		$sentEvent = $this->integration->getSentEvents()[0];

		$this->assertEquals(
			[
				'domain' => $this->integration->getHostName(),
				'stream' => $stream,
			],
			$sentEvent['meta']
		);
		$this->assertEquals(
			[
				'request_headers' => [
					'user-agent' => '',
				],
			],
			$sentEvent['http']
		);

		$isLegacy = isset( $event[ 'client_dt'] );

		if ( $isLegacy ) {
			$this->assertArrayHasKey( 'client_dt', $sentEvent );
			$this->assertArrayNotHasKey( 'dt', $sentEvent );
			$this->assertIsValidTimestamp( $sentEvent['client_dt'] );
			$this->assertEquals( $sentEvent['client_dt'], $event['client_dt'], 'client_dt is not overridden' );
		} else {
			$this->assertArrayNotHasKey( 'client_dt', $sentEvent );
			$this->assertArrayHasKey( 'dt', $sentEvent );
			$this->assertIsValidTimestamp( $sentEvent['dt'] );

			if ( isset( $event['dt'] ) ) {
				$this->assertNotEquals( $sentEvent['dt'], $event['dt'], 'dt is overridden' );
			}
		}

		// Now that we have made assertions about the client_dt and dt properties, we can safely
		// unset them.
		unset( $event['client_dt'] );
		unset( $event['dt'] );

		foreach ( $event as $key => $value ) {
			$this->assertEquals( $value, $sentEvent[$key] );
		}
	}

	private function assertIsValidTimestamp( string $timestamp ) {
		$ts = TestingAccessWrapper::newFromClass( ConvertibleTimestamp::class );
		$this->assertRegExp( $ts->regexes['TS_ISO_8601'], $timestamp );
		$this->assertStringEndsWith( 'Z', $timestamp );
	}

	public function testSubmitDoesNotSendInvalidEvents() {
		$streamName = 'test.event';
		$event = [];

		$logger = $this->createMock( LoggerInterface::class );
		$logger->expects( $this->once() )
			->method( 'warning' )
			->with(
				'The event submitted to stream {streamName} is missing the required "$schema" property: {event}',
				[
					'streamName' => $streamName,
					'event' => $event,
				]
			);

		// @phan-suppress-next-line PhanTypeMismatchArgument
		$client = new MetricsClient( $this->integration, $this->config, $logger );

		$this->assertFalse( $client->submit( 'test.event', [] ) );
		$this->assertEmpty( $this->integration->getSentEvents() );
	}

	public function testSubmitLogsInvalidStreamConfig() {
		$streamName = 'foo';

		// The error message passed to StreamConfigException::__construct() below.
		$validationError = 'foo bar baz';

		$streamConfigFactory = $this->createMock( StreamConfigFactory::class );
		$streamConfigFactory->expects( $this->once() )
			->method( 'getStreamConfig' )
			->willThrowException( new StreamConfigException( $validationError ) );

		$logger = $this->createMock( LoggerInterface::class );
		$logger->expects( $this->once() )
			->method( 'warning' )
			->with(
				'The configuration for stream {streamName} is invalid: {validationError}',
				[
					'streamName' => $streamName,
					'validationError' => $validationError,
				]
			);

		// @phan-suppress-next-line PhanTypeMismatchArgument
		$client = new MetricsClient( $this->integration, $streamConfigFactory, $logger );

		$result = $client->submit( $streamName, [
			'$schema' => '/foo/1.0.0'
		] );

		$this->assertFalse( $result );
		$this->assertEmpty( $this->integration->getSentEvents() );
	}

	public function testSubmitDoesNotSendWhenEventIsNotCurated() {
		$stream = 'test.event';
		$event = [
			'$schema' => '/test/event/1.0.0',
		];

		// The event will have been decorated with additional metadata and contextual properties.
		// Do not make an assertion about its shape.
		$expectedEvent = $this->anything();
		$expectedStreamConfig = new StreamConfig( $this->streamConfigs[$stream] );

		$context = new ContextController( $this->integration );

		$curation = $this->getMockBuilder( CurationController::class )
			->onlyMethods( [ 'shouldProduceEvent' ] )
			->getMock();

		$curation->method( 'shouldProduceEvent' )
			->with( $expectedEvent, $expectedStreamConfig )
			->willReturn( false );

		// @phan-suppress-next-line PhanTypeMismatchArgument
		$client = new MetricsClient( $this->integration, $this->config, null, $context, $curation );

		$this->assertFalse( $client->submit( $stream, $event ) );
		$this->assertEmpty( $this->integration->getSentEvents() );
	}

	public function provideDispatch(): Generator {
		yield [
			'customData' => [],
			'expectedCustomData' => null,
		];
		yield [
			'customData' => [
				'foo' => 1,
				'bar' => 1.1,
				'baz' => 'qux',
				'quux' => true,
				'quuz' => null,
			],
			'expectedCustomData' => [
				'foo' => [
					'data_type' => 'number',
					'value' => '1',
				],
				'bar' => [
					'data_type' => 'number',
					'value' => '1.1',
				],
				'baz' => [
					'data_type' => 'string',
					'value' => 'qux',
				],
				'quux' => [
					'data_type' => 'boolean',
					'value' => 'true',
				],
				'quuz' => [
					'data_type' => 'null',
					'value' => 'null',
				],
			],
		];
	}

	/**
	 * @dataProvider provideDispatch
	 */
	public function testDispatch( $customData, $expectedCustomData ): void {
		$this->client->dispatch( 'foo', $customData );

		$event = $this->integration->getSentEvents()[0];

		$this->assertEquals( '/analytics/mediawiki/client/metrics_event/1.0.0', $event['$schema'] );
		$this->assertEquals( 'test.event.mpc1', $event['meta']['stream'] );

		$this->assertArrayHasKey(
			'domain',
			$event['meta'],
			'The event was prepared with MetricsClient::prepareEvent()'
		);
		$this->assertArrayHasKey( 'http', $event );

		if ( $expectedCustomData === null ) {
			$this->assertArrayNotHasKey( 'custom_data', $event );
		} else {
			$this->assertEquals( $expectedCustomData, $event['custom_data'] );
		}
	}

	public function testDispatchToMultipleStreams(): void {
		// It should call addRequestedValues for each event being submitted
		$context = $this->createMock( ContextController::class );
		$context->expects( $this->exactly( 2 ) )
			->method( 'addRequestedValues' )
			->willReturnArgument( 0 );

		// It should call shouldProduceEvent for each event being submitted
		$curation = $this->createMock( CurationController::class );
		$curation->expects( $this->exactly( 2 ) )
			->method( 'shouldProduceEvent' )
			->willReturn( true );

		// @phan-suppress-next-line PhanTypeMismatchArgument
		$client = new MetricsClient( $this->integration, $this->config, null, $context, $curation );

		$client->dispatch( 'bar' );

		$events = $this->integration->getSentEvents();

		$this->assertCount( 2, $events );

		$this->assertEquals( 'test.event.mpc1', $events[0]['meta']['stream'] );
		$this->assertEquals( 'test.event.mpc2', $events[1]['meta']['stream'] );

		$this->assertIsValidTimestamp( $events[0]['dt'] );
		$this->assertIsValidTimestamp( $events[1]['dt'] );
		$this->assertEquals(
			$events[0]['dt'],
			$events[1]['dt'],
			'All events are submitted with the same timestamp.'
		);
	}
}
