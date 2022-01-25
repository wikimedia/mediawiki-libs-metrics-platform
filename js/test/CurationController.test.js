/* eslint-disable camelcase */
( function () {

	var curationController = new ( require( '../src/CurationController.js' ) )();

	QUnit.module( 'curationController' );

	QUnit.test( 'equals', function ( assert ) {
		assert.strictEqual( curationController.applyRules( 1, { equals: 1 } ), true );
		assert.strictEqual( curationController.applyRules( 'a', { equals: 'a' } ), true );
		assert.strictEqual( curationController.applyRules( 1, { equals: 0 } ), false );
		assert.strictEqual( curationController.applyRules( '1', { equals: 1 } ), false );
		assert.strictEqual( curationController.applyRules( '0', { equals: 0 } ), false );
		assert.strictEqual( curationController.applyRules( 0, { equals: undefined } ), false );
	} );

	QUnit.test( 'greaterThan', function ( assert ) {
		assert.strictEqual( curationController.applyRules( 1, { greater_than: 0 } ), true );
		assert.strictEqual( curationController.applyRules( 1.2, { greater_than: 1.1 } ), true );
		assert.strictEqual( curationController.applyRules( 1.1, { greater_than: 1.1 } ), false );
		assert.strictEqual( curationController.applyRules( 0, { greater_than: 1 } ), false );
		assert.strictEqual( curationController.applyRules( 0, { greater_than: 0 } ), false );
	} );

	QUnit.test( 'lessThan', function ( assert ) {
		assert.strictEqual( curationController.applyRules( 0, { less_than: 1 } ), true );
		assert.strictEqual( curationController.applyRules( 1.1, { less_than: 1.2 } ), true );
		assert.strictEqual( curationController.applyRules( 1.1, { less_than: 1.1 } ), false );
		assert.strictEqual( curationController.applyRules( 1, { less_than: 0 } ), false );
		assert.strictEqual( curationController.applyRules( 0, { less_than: 0 } ), false );
	} );

	QUnit.test( 'in', function ( assert ) {
		assert.strictEqual( curationController.applyRules( 1, { in: [ 1 ] } ), true );
		assert.strictEqual( curationController.applyRules( 1, { in: [ 0 ] } ), false );
	} );

	QUnit.test( 'contains', function ( assert ) {
		assert.strictEqual( curationController.applyRules( [ 1 ], { contains: 1 } ), true );
		assert.strictEqual( curationController.applyRules( [ 1 ], { contains: 0 } ), false );
	} );

	QUnit.test( 'containsAll', function ( assert ) {
		assert.strictEqual(
			curationController.applyRules( [ 0, 1 ], { contains_all: [ 0, 1 ] } ),
			true
		);
		assert.strictEqual(
			curationController.applyRules( [ 0, 1 ], { contains_all: [ 1, 0 ] } ),
			true
		);
		assert.strictEqual( curationController.applyRules( [], { contains_all: [ 1 ] } ), false );
	} );

	QUnit.test( 'containsAny', function ( assert ) {
		assert.strictEqual(
			curationController.applyRules(
				[ 0, 1 ],
				{ contains_any: [ 0, 1 ] }
			),
			true
		);
		assert.strictEqual(
			curationController.applyRules(
				[ 0, 1 ],
				{ contains_any: [ 1, 2 ] }
			),
			true
		);
	} );

	QUnit.test( 'shouldProduceEvent()', function ( assert ) {
		var event, streamConfig = {
			producers: {
				metrics_platform_client: {
					curation: {
						page_id: {
							less_than: 500,
							not_equals: 42
						},
						page_namespace_text: {
							equals: 'Talk'
						},
						user_is_logged_in: {
							equals: true
						},
						user_edit_count_bucket: {
							in: [ '100-999 edits', '1000+ edits' ]
						},
						user_groups: {
							contains_all: [ 'user', 'autoconfirmed' ],
							does_not_contain: 'sysop'
						},
						device_pixel_ratio: {
							greater_than_or_equals: 1.5,
							less_than_or_equals: 2.5
						}
					}
				}
			}
		};

		function baseEvent() {
			return {
				$schema: 'test/event',
				meta: {
					stream: 'test.event'
				},
				page: {
					id: 1,
					namespace_text: 'Talk'
				},
				user: {
					groups: [ 'user', 'autoconfirmed', 'steward' ],
					is_logged_in: true,
					edit_count_bucket: '1000+ edits'
				},
				device: {
					pixel_ratio: 2.0
				}
			};
		}

		assert.strictEqual(
			curationController.shouldProduceEvent( baseEvent(), streamConfig ),
			true
		);

		event = baseEvent();
		event.page.id = 42;
		assert.strictEqual(
			curationController.shouldProduceEvent( event, streamConfig ),
			false,
			'wrong page id'
		);

		event = baseEvent();
		event.page.namespace_text = 'User';
		assert.strictEqual(
			curationController.shouldProduceEvent( event, streamConfig ),
			false,
			'wrong page namespace text'
		);

		event = baseEvent();
		event.user.groups = [ 'user', 'autoconfirmed', 'sysop' ];
		assert.strictEqual(
			curationController.shouldProduceEvent( event, streamConfig ),
			false,
			'wrong user groups'
		);

		event = baseEvent();
		event.user.groups = [];
		assert.strictEqual(
			curationController.shouldProduceEvent( event, streamConfig ),
			false,
			'no user groups'
		);

		event = baseEvent();
		event.user.is_logged_in = false;
		assert.strictEqual(
			curationController.shouldProduceEvent( event, streamConfig ),
			false,
			'user not logged in'
		);

		event = baseEvent();
		event.user.edit_count_bucket = '5-99 edits';
		assert.strictEqual(
			curationController.shouldProduceEvent( event, streamConfig ),
			false,
			'wrong user edit count bucket'
		);

		event = baseEvent();
		event.device.pixel_ratio = 1.0;
		assert.strictEqual(
			curationController.shouldProduceEvent( event, streamConfig ),
			false,
			'device pixel ratio too low'
		);

		event = baseEvent();
		event.device.pixel_ratio = 3.0;
		assert.strictEqual(
			curationController.shouldProduceEvent( event, streamConfig ),
			false,
			'device pixel ratio too high'
		);
	} );

}() );
