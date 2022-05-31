<?php

namespace Wikimedia\MetricsPlatform\Tests;

use PHPUnit\Framework\TestCase;
use Wikimedia\MetricsPlatform\CurationController;
use Wikimedia\MetricsPlatform\StreamConfig\StreamConfig;
use Wikimedia\TestingAccessWrapper;

/** @covers \Wikimedia\MetricsPlatform\CurationController */
class CurationControllerTest extends TestCase {

	/** @var TestingAccessWrapper */
	private $curationController;

	/** @var StreamConfig */
	private $streamConfig;

	/** @var array */
	private $baseEvent;

	protected function setUp(): void {
		$curationController = new CurationController();
		$this->curationController = TestingAccessWrapper::newFromObject( $curationController );
		$this->streamConfig = new StreamConfig( [
			'producers' => [
				'metrics_platform_client' => [
					'curation' => [
						'page_id' => [
							'less_than' => 500,
							'not_equals' => 42,
						],
						'page_namespace_text' => [
							'equals' => 'Talk',
						],
						'user_is_logged_in' => [
							'equals' => true
						],
						'user_edit_count_bucket' => [
							'in' => [ '100-999 edits', '1000+ edits' ],
						],
						'user_groups' => [
							'contains_all' => [ 'user', 'autoconfirmed' ],
							'does_not_contain' => 'sysop',

							// Unknown context attribute with valid comparator.
							'unknown_rule' => false,
						],
					],
				],
			],
		] );
		$this->baseEvent = [
			'meta' => [ 'stream' => 'test.event' ],
			'$schema' => 'test/event',
			'page' => [
				'id' => 1,
				'namespace_text' => 'Talk',
			],
			'user' => [
				'groups' => [ 'user', 'autoconfirmed', 'steward' ],
				'is_logged_in' => true,
				'edit_count_bucket' => '1000+ edits',
			],
		];
	}

	public function testEventPassesCurationRules() {
		$this->assertTrue( $this->curationController->
			shouldProduceEvent( $this->baseEvent, $this->streamConfig ) );
	}

	public function testEventFailsWrongPageId() {
		$event = $this->baseEvent;
		$event['page']['id'] = 42;
		$this->assertFalse( $this->curationController->shouldProduceEvent( $event, $this->streamConfig ) );
	}

	public function testEventFailsWrongPageNamespaceText() {
		$event = $this->baseEvent;
		$event['page']['namespace_text'] = 'User';
		$this->assertFalse( $this->curationController->shouldProduceEvent( $event, $this->streamConfig ) );
	}

	public function testEventFailsWrongUserGroups() {
		$event = $this->baseEvent;
		$event['user']['groups'] = [ 'user', 'autoconfirmed', 'sysop' ];
		$this->assertFalse( $this->curationController->shouldProduceEvent( $event, $this->streamConfig ) );
	}

	public function testEventFailsNoUserGroups() {
		$event = $this->baseEvent;
		$event['user']['groups'] = [];
		$this->assertFalse( $this->curationController->shouldProduceEvent( $event, $this->streamConfig ) );
	}

	public function testEventFailsNotLoggedIn() {
		$event = $this->baseEvent;
		$event['user']['is_logged_in'] = false;
		$this->assertFalse( $this->curationController->shouldProduceEvent( $event, $this->streamConfig ) );
	}

	public function testEventFailsWrongUserEditCountBucket() {
		$event = $this->baseEvent;
		$event['user']['edit_count_bucket'] = '5-99 edits';
		$this->assertFalse( $this->curationController->shouldProduceEvent( $event, $this->streamConfig ) );
	}

	public function testIntegerEqualsRule() {
		$rules = [ 'equals' => 1 ];
		$this->assertTrue( $this->curationController->applyRules( 1, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( -1, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( null, $rules ) );
	}

	public function testFloatEqualsRule() {
		$rules = [ 'equals' => 1.0 ];
		$this->assertTrue( $this->curationController->applyRules( 1.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 0.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( -1.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( null, $rules ) );
	}

	public function testStringEqualsRule() {
		$rules = [ 'equals' => 'foo' ];
		$this->assertTrue( $this->curationController->applyRules( 'foo', $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 'bar', $rules ) );
		$this->assertFalse( $this->curationController->applyRules( '', $rules ) );
		$this->assertFalse( $this->curationController->applyRules( null, $rules ) );
	}

	public function testArrayEqualsRule() {
		$rules = [ 'equals' => [ 1, 2, 3 ] ];
		$this->assertTrue( $this->curationController->applyRules( [ 1, 2, 3 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 3, 2, 1 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 4, 5, 6 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( null, $rules ) );
	}

	public function testBooleanEqualsRule() {
		$rules = [ 'equals' => true ];
		$this->assertTrue( $this->curationController->applyRules( true, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( false, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 1, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( null, $rules ) );
	}

	public function testIntegerNotEqualsRule() {
		$rules = [ 'not_equals' => 1 ];
		$this->assertFalse( $this->curationController->applyRules( 1, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( -1, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( null, $rules ) );
	}

	public function testFloatNotEqualsRule() {
		$rules = [ 'not_equals' => 1.0 ];
		$this->assertFalse( $this->curationController->applyRules( 1.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 0.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( -1.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( null, $rules ) );
	}

	public function testStringNotEqualsRule() {
		$rules = [ 'not_equals' => 'foo' ];
		$this->assertFalse( $this->curationController->applyRules( 'foo', $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 'bar', $rules ) );
		$this->assertTrue( $this->curationController->applyRules( "", $rules ) );
		$this->assertTrue( $this->curationController->applyRules( null, $rules ) );
	}

	public function testArrayNotEqualsRule() {
		$rules = [ 'not_equals' => [ 1, 2, 3 ] ];
		$this->assertFalse( $this->curationController->applyRules( [ 1, 2, 3 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [ 3, 2, 1 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [ 4, 5, 6 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( null, $rules ) );
	}

	public function testBooleanNotEqualsRule() {
		$rules = [ 'not_equals' => true ];
		$this->assertFalse( $this->curationController->applyRules( true, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( false, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 1, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( null, $rules ) );
	}

	public function testIntegerGreaterThanRule() {
		$rules = [ 'greater_than' => 0 ];
		$this->assertTrue( $this->curationController->applyRules( 1, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( -1, $rules ) );
	}

	public function testFloatGreaterThanRule() {
		$rules = [ 'greater_than' => 0.0 ];
		$this->assertTrue( $this->curationController->applyRules( 1.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 0.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( -1.0, $rules ) );
	}

	public function testIntegerLessThanRule() {
		$rules = [ 'less_than' => 0 ];
		$this->assertFalse( $this->curationController->applyRules( 1, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( -1, $rules ) );
	}

	public function testFloatLessThanRule() {
		$rules = [ 'less_than' => 0.0 ];
		$this->assertFalse( $this->curationController->applyRules( 1.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 0.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( -1.0, $rules ) );
	}

	public function testIntegerGreaterThanOrEqualsRule() {
		$rules = [ 'greater_than_or_equals' => 0 ];
		$this->assertTrue( $this->curationController->applyRules( 1, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( -1, $rules ) );
	}

	public function testFloatGreaterThanOrEqualsRule() {
		$rules = [ 'greater_than_or_equals' => 0.0 ];
		$this->assertTrue( $this->curationController->applyRules( 1.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 0.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( -1.0, $rules ) );
	}

	public function testIntegerLessThanOrEqualsRule() {
		$rules = [ 'less_than_or_equals' => 0 ];
		$this->assertFalse( $this->curationController->applyRules( 1, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( -1, $rules ) );
	}

	public function testFloatLessThanOrEqualsRule() {
		$rules = [ 'less_than_or_equals' => 0.0 ];
		$this->assertFalse( $this->curationController->applyRules( 1.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 0.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( -1.0, $rules ) );
	}

	public function testIntegerInCollectionRule() {
		$rules = [ 'in' => [ 1, 2, 3 ] ];
		$this->assertTrue( $this->curationController->applyRules( 1, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 4, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( -1, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( null, $rules ) );
	}

	public function testFloatInCollectionRule() {
		$rules = [ 'in' => [ 1.0, 2.0, 3.0 ] ];
		$this->assertTrue( $this->curationController->applyRules( 1.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( 4.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( -1.0, $rules ) );
		$this->assertFalse( $this->curationController->applyRules( null, $rules ) );
	}

	public function testStringInCollectionRule() {
		$rules = [ 'in' => [ "a", "b", "c" ] ];
		$this->assertTrue( $this->curationController->applyRules( "a", $rules ) );
		$this->assertFalse( $this->curationController->applyRules( "A", $rules ) );
		$this->assertFalse( $this->curationController->applyRules( "", $rules ) );
		$this->assertFalse( $this->curationController->applyRules( null, $rules ) );
	}

	public function testIntegerNotInCollectionRule() {
		$rules = [ 'not_in' => [ 1, 2, 3 ] ];
		$this->assertFalse( $this->curationController->applyRules( 1, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 4, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( -1, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( null, $rules ) );
	}

	public function testFloatNotInCollectionRule() {
		$rules = [ 'not_in' => [ 1.0, 2.0, 3.0 ] ];
		$this->assertFalse( $this->curationController->applyRules( 1.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( 4.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( -1.0, $rules ) );
		$this->assertTrue( $this->curationController->applyRules( null, $rules ) );
	}

	public function testStringNotInCollectionRule() {
		$rules = [ 'not_in' => [ "a", "b", "c" ] ];
		$this->assertFalse( $this->curationController->applyRules( "a", $rules ) );
		$this->assertTrue( $this->curationController->applyRules( "A", $rules ) );
		$this->assertTrue( $this->curationController->applyRules( "", $rules ) );
		$this->assertTrue( $this->curationController->applyRules( null, $rules ) );
	}

	public function testIntegerCollectionContainsRule() {
		$rules = [ 'contains' => 1 ];
		$this->assertTrue( $this->curationController->applyRules( [ 1, 2, 3 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 3, 4, 5 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

	public function testFloatCollectionContainsRule() {
		$rules = [ 'contains' => 1.0 ];
		$this->assertTrue( $this->curationController->applyRules( [ 1.0, 2.0, 3.0 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 3.0, 4.0, 5.0 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

	public function testStringCollectionContainsRule() {
		$rules = [ 'contains' => "a" ];
		$this->assertTrue( $this->curationController->applyRules( [ "a", "b", "c" ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ "c", "d", "e" ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

	public function testIntegerCollectionDoesNotContainRule() {
		$rules = [ 'does_not_contain' => 1 ];
		$this->assertFalse( $this->curationController->applyRules( [ 1, 2, 3 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [ 3, 4, 5 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [], $rules ) );
	}

	public function testFloatCollectionDoesNotContainRule() {
		$rules = [ 'does_not_contain' => 1.0 ];
		$this->assertFalse( $this->curationController->applyRules( [ 1.0, 2.0, 3.0 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [ 3.0, 4.0, 5.0 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [], $rules ) );
	}

	public function testStringCollectionDoesNotContainRule() {
		$rules = [ 'does_not_contain' => "a" ];
		$this->assertFalse( $this->curationController->applyRules( [ "a", "b", "c" ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [ "c", "d", "e" ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [], $rules ) );
	}

	public function testIntegerCollectionContainsAllRule() {
		$rules = [ 'contains_all' => [ 1, 2, 3 ] ];
		$this->assertTrue( $this->curationController->applyRules( [ 1, 2, 3 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 3, 4, 5 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 4, 5, 6 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

	public function testFloatCollectionContainsAllRule() {
		$rules = [ 'contains_all' => [ 1.0, 2.0, 3.0 ] ];
		$this->assertTrue( $this->curationController->applyRules( [ 1.0, 2.0, 3.0 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 3.0, 4.0, 5.0 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 4.0, 5.0, 6.0 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

	public function testStringCollectionContainsAllRule() {
		$rules = [ 'contains_all' => [ "a", "b", "c" ] ];
		$this->assertTrue( $this->curationController->applyRules( [ "a", "b", "c" ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ "c", "d", "e" ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ "d", "e", "f" ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

	public function testIntegerCollectionContainsAnyRule() {
		$rules = [ 'contains_any' => [ 1, 2, 3 ] ];
		$this->assertTrue( $this->curationController->applyRules( [ 1, 2, 3 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [ 3, 4, 5 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 4, 5, 6 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

	public function testFloatCollectionContainsAnyRule() {
		$rules = [ 'contains_any' => [ 1.0, 2.0, 3.0 ] ];
		$this->assertTrue( $this->curationController->applyRules( [ 1.0, 2.0, 3.0 ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [ 3.0, 4.0, 5.0 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ 4.0, 5.0, 6.0 ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

	public function testStringCollectionContainsAnyRule() {
		$rules = [ 'contains_any' => [ "a", "b", "c" ] ];
		$this->assertTrue( $this->curationController->applyRules( [ "a", "b", "c" ], $rules ) );
		$this->assertTrue( $this->curationController->applyRules( [ "c", "d", "e" ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [ "d", "e", "f" ], $rules ) );
		$this->assertFalse( $this->curationController->applyRules( [], $rules ) );
	}

}
