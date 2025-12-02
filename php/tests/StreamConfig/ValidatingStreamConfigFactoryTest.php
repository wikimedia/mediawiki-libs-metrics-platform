<?php

namespace Wikimedia\MetricsPlatform\Tests\StreamConfig;

use Generator;
use JsonSchema\Validator;
use PHPUnit\Framework\TestCase;
use Wikimedia\MetricsPlatform\StreamConfig\StreamConfig;
use Wikimedia\MetricsPlatform\StreamConfig\StreamConfigException;
use Wikimedia\MetricsPlatform\StreamConfig\ValidatingStreamConfigFactory;

/**
 * @covers \Wikimedia\MetricsPlatform\StreamConfig\StreamConfig
 * @covers \Wikimedia\MetricsPlatform\StreamConfig\ValidatingStreamConfigFactory
 */
class ValidatingStreamConfigFactoryTest extends TestCase {
	private function getFactory( $rawStreamConfigs ): ValidatingStreamConfigFactory {
		$validator = new Validator();

		return new ValidatingStreamConfigFactory( $rawStreamConfigs, $validator );
	}

	public function testItShouldThrowSimple(): void {
		$this->expectException( StreamConfigException::class );

		$this->getFactory( [] )->getStreamConfig( 'test.stream' );
	}

	public static function provideShouldHandleValidStreamConfigs(): Generator {
		yield [
			'rawStreamConfig' => [],
			'expectedRequestValues' => [],
			'expectedCurationRules' => [],
		];
		yield [
			'rawStreamConfig' => [
				'producers' => []
			],
			'expectedRequestValues' => [],
			'expectedCurationRules' => [],
		];
		yield [
			'rawStreamConfig' => [
				'producers' => [
					'foo_producer' => [
						'bar' => 'baz',
					],
				],
			],
			'expectedRequestValues' => [],
			'expectedCurationRules' => [],
		];
		yield [
			'rawStreamConfig' => [
				'producers' => [
					'metrics_platform_client' => []
				]
			],
			'expectedRequestValues' => [],
			'expectedCurationRules' => [],
		];
		yield [
			'rawStreamConfig' => [
				'producers' => [
					'metrics_platform_client' => [
						'provide_values' => [
							'performer_name',
						],
						'curation' => [
							'performer_name' => [
								'equals' => 'Phuedx',
							],
						],
					],
				],
			],
			'expectedRequestValues' => [
				'performer_name',
			],
			'expectedCurationRules' => [
				'performer_name' => [
					'equals' => 'Phuedx',
				],
			],
		];
	}

	/**
	 * @dataProvider provideShouldHandleValidStreamConfigs
	 */
	public function testItShouldHandleValidStreamConfigs(
		$rawStreamConfig,
		$expectedRequestValues,
		$expectedCurationRules
	): void {
		$streamName = 'test.stream';
		$streamConfigs = [
			$streamName => $rawStreamConfig,
		];

		$factory = $this->getFactory( $streamConfigs );
		$streamConfig = $factory->getStreamConfig( $streamName );

		$this->assertNotNull( $streamConfig );
		$this->assertEquals( $expectedRequestValues, $streamConfig->getRequestedValues() );
		$this->assertEquals( $expectedCurationRules, $streamConfig->getCurationRules() );
	}

	public function testItShouldHandleDisabledStreamConfigs(): void {
		$streamName = 'test.stream';
		$streamConfigs = false;

		$factory = $this->getFactory( $streamConfigs );
		$streamConfig = $factory->getStreamConfig( $streamName );

		$this->assertEquals( new StreamConfig( [] ), $streamConfig );
	}

	public static function provideMetricsPlatformClientConfig(): Generator {
		yield [
			'metricsPlatformClientConfig' => [
				'foo' => [],
			],
		];

		// provide_values
		yield [
			'metricsPlatformClientConfig' => [
				'provide_values' => [
					'foo',
				],
			],
		];
		yield [
			'metricsPlatformClientConfig' => [
				'provide_values' => [
					'page_id',
					'page_id',
				],
			],
		];

		// curation
		yield [
			'metricsPlatformClientConfig' => [
				'curation' => [],
			],
		];
		yield [
			'metricsPlatformClientConfig' => [
				'curation' => [
					// Valid context attribute name with valid operator and invalid operand.
					'performer_is_logged_in' => [
						'equals' => [],
					],
				],
			],
		];
		yield [
			'metricsPlatformClientConfig' => [
				'curation' => [
					// Invalid context attribute name with valid operator/operand.
					'performer_is_not_logged_in' => [
						'equals' => true,
					],
				],
			],
		];

		// NOTE: We do not make assertions about the validation of the "sample" property because
		// it does not make sense in this context.
	}

	/**
	 * @dataProvider provideMetricsPlatformClientConfig
	 */
	public function testItShouldValidateMetricsPlatformClientConfig( $metricsPlatformClientConfig ): void {
		$this->expectException( StreamConfigException::class );

		$streamName = 'test.stream';
		$rawStreamConfigs = [
			$streamName => [
				'producers' => [
					'metrics_platform_client' => $metricsPlatformClientConfig,
				],
			],
		];

		$this->getFactory( $rawStreamConfigs )
			->getStreamConfig( 'test.stream' );
	}
}
