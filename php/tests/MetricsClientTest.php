<?php

namespace Wikimedia\MetricsPlatform\Tests;

require_once __DIR__ . '/TestIntegration.php';
require_once __DIR__ . '/TestEventSubmitter.php';

use Generator;
use PHPUnit\Framework\TestCase;
use Wikimedia\MetricsPlatform\ContextController;
use Wikimedia\MetricsPlatform\CurationController;
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
		// @phan-suppress-next-line PhanDeprecatedFunction
		$this->client->dispatch( 'foo', $customData );

		list( $streamName, $event ) = $this->eventSubmitter->getSubmissions()[0];

		$this->assertEquals( 'test.event.mpc1', $streamName );
		$this->assertEquals( '/analytics/mediawiki/client/metrics_event/2.0.0', $event['$schema'] );

		if ( $expectedCustomData === null ) {
			$this->assertArrayNotHasKey( 'custom_data', $event );
		} else {
			$this->assertEquals( $expectedCustomData, $event['custom_data'] );
		}
	}

	public function testDispatchToMultipleStreams(): void {
		ConvertibleTimestamp::setFakeTime( strtotime( '2012-12-12T12:12:12Z' ) );

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

		$client = new MetricsClient(
			$this->eventSubmitter,
			$this->integration,
			$this->config,
			null,

			// @phan-suppress-next-line PhanTypeMismatchArgument
			$context, $curation
		);

		// @phan-suppress-next-line PhanDeprecatedFunction
		$client->dispatch( 'bar' );

		$submissions = $this->eventSubmitter->getSubmissions();

		$this->assertCount( 2, $submissions );

		$this->assertEquals( 'test.event.mpc1', $submissions[0][0] );
		$this->assertEquals( 'test.event.mpc2', $submissions[1][0] );

		$this->assertIsValidTimestamp( $submissions[0][1]['dt'] );
		$this->assertIsValidTimestamp( $submissions[1][1]['dt'] );
		$this->assertEquals(
			$submissions[0][1]['dt'],
			$submissions[1][1]['dt'],
			'All events are submitted with the same timestamp.'
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
