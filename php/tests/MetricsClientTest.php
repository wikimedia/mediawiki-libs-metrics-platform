<?php

namespace Wikimedia\Metrics\Test;

require_once __DIR__ . '/TestIntegration.php';

use Generator;
use Wikimedia\Metrics\ContextController;
use Wikimedia\Metrics\CurationController;
use Wikimedia\Metrics\MetricsClient;
use Wikimedia\TestingAccessWrapper;
use Wikimedia\Timestamp\ConvertibleTimestamp;

/** @covers \Wikimedia\Metrics\MetricsClient */
class MetricsClientTest extends \PHPUnit\Framework\TestCase {

	/** @var TestIntegration */
	private $integration;

	/** @var MetricsClient */
	private $client;

	/** @var array */
	private $streamConfigs = [
		'test.event' => [],
		'test.event.legacy' => [],
	];

	/** @inheritDoc */
	protected function setUp(): void {
		parent::setUp();

		$this->integration = new TestIntegration();
		$this->client = new MetricsClient( $this->integration, $this->streamConfigs );
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
		$this->assertFalse( $this->client->submit( 'test.event', [] ) );
		$this->assertFalse( $this->client->submit( 'foo', [
			'$schema' => '/test/event/1.0.0',
		] ) );
		$this->assertEmpty( $this->integration->getSentEvents() );
	}

	public function testSubmitDoesNotSentWhenEventIsNotCurated() {
		$stream = 'test.event';
		$event = [
			'$schema' => '/test/event/1.0.0',
		];

		// The event will have been decorated with additional metadata and contextual properties.
		// Do not make an assertion about its shape.
		$expectedEvent = $this->anything();

		$curationController = $this->getMockBuilder( CurationController::class )
			->onlyMethods( [ 'shouldProduceEvent' ] )
			->getMock();

		$expectedEvent = $this->anything();
		$curationController->method( 'shouldProduceEvent' )
			->with( $expectedEvent, $this->streamConfigs[$stream] )
			->willReturn( false );

		$client = new MetricsClient(
			$this->integration,
			$this->streamConfigs,
			new ContextController( $this->integration ),
			$curationController
		);

		$this->assertFalse( $client->submit( $stream, $event ) );
		$this->assertEmpty( $this->integration->getSentEvents() );
	}
}
