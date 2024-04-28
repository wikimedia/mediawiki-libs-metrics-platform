<?php

namespace Wikimedia\MetricsPlatform\Tests\Consistency;

use PHPUnit\Framework\TestCase;
use Symfony\Component\Process\Process;

class ConsistencyTest extends TestCase {
	private const IMPLEMENTATION_TO_COMMAND_MAP = [
		'php' => [ 'php', 'main.php' ],
		'js' => [ 'node', 'main.js' ],
	];

	private function getImplementations(): array {
		return array_keys( self::IMPLEMENTATION_TO_COMMAND_MAP );
	}

	private function dispatch( $impl ): array {
		$command = self::IMPLEMENTATION_TO_COMMAND_MAP[ $impl ];
		$cwd = __DIR__ . "/{$impl}";

		$process = new Process( $command, $cwd );
		$process->run();

		$this->assertTrue( $process->isSuccessful(), "The {$impl} implementation should exit without error." );
		$this->assertEquals( '', $process->getErrorOutput() );

		$rawOutput = $process->getOutput();
		$output = json_decode( $rawOutput, true );

		$this->assertIsArray( $output, "The {$impl} should output valid JSON." );

		// Only the PHP Event Platform Client adds the http property of the event.
		unset( $output['http'] );

		// TODO: Test whether all Metrics Platform Clients are adding timestamps in the form
		// YY:MM:DDThh:mm:ss.vvvZ?
		unset( $output['dt'] );

		return $output;
	}

	public function testConsistency() {
		// Check the dispatched data against the expected data.
		$expectedEventRaw = file_get_contents( __DIR__ . '/expected_event.json' );
		$expectedEvent = json_decode( $expectedEventRaw, true  );

		foreach ( self::getImplementations() as $impl ) {
			$output = $this->dispatch( $impl );

			$this->assertEquals(
				$expectedEvent,
				$output,
				"The {$impl} implementation should match expected event data."
			);
		}
	}
}
