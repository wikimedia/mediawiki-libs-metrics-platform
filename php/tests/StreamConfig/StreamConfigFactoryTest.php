<?php

namespace Wikimedia\Metrics\Test\StreamConfig;

use Generator;
use PHPUnit\Framework\TestCase;
use Wikimedia\Metrics\StreamConfig\StreamConfig;
use Wikimedia\Metrics\StreamConfig\StreamConfigException;
use Wikimedia\Metrics\StreamConfig\StreamConfigFactory;

/**
 * @coversDefaultClass \Wikimedia\Metrics\StreamConfig\StreamConfigFactory
 */
class StreamConfigFactoryTest extends TestCase {
	private function getFactory( $rawStreamConfigs ): StreamConfigFactory {
		return new StreamConfigFactory( $rawStreamConfigs );
	}

	/**
	 * @covers ::getStreamConfig
	 */
	public function testGetStreamConfig(): void {
		$factory = $this->getFactory( false );

		$this->assertNull( $factory->getStreamConfig( 'test.stream' ) );
		$this->assertNull( $factory->getStreamConfig( 'foo' ) );

		// ---

		$factory = $this->getFactory( [
			'test.stream' => [],
		] );

		$this->assertEquals( new StreamConfig( [] ), $factory->getStreamConfig( 'test.stream' ) );
	}

	public function provideStreamConfigThrows(): Generator {
		yield [ false ];
		yield [ true ];
		yield [ 1 ];
		yield [ 'foo' ];
	}

	/**
	 * @covers ::getStreamConfig
	 * @dataProvider provideStreamConfigThrows
	 */
	public function testGetStreamConfigThrows( $rawStreamConfig ): void {
		$this->expectException( StreamConfigException::class );

		$factory = $this->getFactory( [
			'test.stream' => $rawStreamConfig,
		] );
		$factory->getStreamConfig( 'foo' );
	}
}
