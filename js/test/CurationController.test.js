/* eslint-disable camelcase */
( function () {

	var curationController = new ( require( '../src/CurationController.js' ) )();

	QUnit.module( 'curationController' );

	QUnit.test( 'equals', function ( assert ) {
		assert.ok( curationController.applyRules( 1, { equals: 1 } ) );
		assert.ok( curationController.applyRules( 'a', { equals: 'a' } ) );
		assert.notOk( curationController.applyRules( 1, { equals: 0 } ) );
		assert.notOk( curationController.applyRules( '1', { equals: 1 } ) );
		assert.notOk( curationController.applyRules( '0', { equals: 0 } ) );
		assert.notOk( curationController.applyRules( 0, { equals: undefined } ) );
	} );

	QUnit.test( 'greaterThan', function ( assert ) {
		assert.ok( curationController.applyRules( 1, { greater_than: 0 } ) );
		assert.ok( curationController.applyRules( 1.2, { greater_than: 1.1 } ) );
		assert.notOk( curationController.applyRules( 1.1, { greater_than: 1.1 } ) );
		assert.notOk( curationController.applyRules( 0, { greater_than: 1 } ) );
		assert.notOk( curationController.applyRules( 0, { greater_than: 0 } ) );
	} );

	QUnit.test( 'lessThan', function ( assert ) {
		assert.ok( curationController.applyRules( 0, { less_than: 1 } ) );
		assert.ok( curationController.applyRules( 1.1, { less_than: 1.2 } ) );
		assert.notOk( curationController.applyRules( 1.1, { less_than: 1.1 } ) );
		assert.notOk( curationController.applyRules( 1, { less_than: 0 } ) );
		assert.notOk( curationController.applyRules( 0, { less_than: 0 } ) );
	} );

	QUnit.test( 'in', function ( assert ) {
		assert.ok( curationController.applyRules( 1, { in: [ 1 ] } ) );
		assert.notOk( curationController.applyRules( 1, { in: [ 0 ] } ) );
	} );

	QUnit.test( 'contains', function ( assert ) {
		assert.ok( curationController.applyRules( [ 1 ], { contains: 1 } ) );
		assert.notOk( curationController.applyRules( [ 1 ], { contains: 0 } ) );
	} );

	QUnit.test( 'containsAll', function ( assert ) {
		assert.ok( curationController.applyRules( [ 0, 1 ], { contains_all: [ 0, 1 ] } ) );
		assert.ok( curationController.applyRules( [ 0, 1 ], { contains_all: [ 1, 0 ] } ) );
		assert.notOk( curationController.applyRules( [], { contains_all: [ 1 ] } ) );
	} );

	QUnit.test( 'containsAny', function ( assert ) {
		assert.ok( curationController.applyRules( [ 0, 1 ], { contains_any: [ 0, 1 ] } ) );
		assert.ok( curationController.applyRules( [ 0, 1 ], { contains_any: [ 1, 2 ] } ) );
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

		assert.ok( curationController.shouldProduceEvent( baseEvent(), streamConfig ) );

		event = baseEvent();
		event.page.id = 42;
		assert.notOk( curationController.shouldProduceEvent( event, streamConfig ), 'wrong page id' );

		event = baseEvent();
		event.page.namespace_text = 'User';
		assert.notOk( curationController.shouldProduceEvent( event, streamConfig ), 'wrong page namespace text' );

		event = baseEvent();
		event.user.groups = [ 'user', 'autoconfirmed', 'sysop' ];
		assert.notOk( curationController.shouldProduceEvent( event, streamConfig ), 'wrong user groups' );

		event = baseEvent();
		event.user.groups = [];
		assert.notOk( curationController.shouldProduceEvent( event, streamConfig ), 'no user groups' );

		event = baseEvent();
		event.user.is_logged_in = false;
		assert.notOk( curationController.shouldProduceEvent( event, streamConfig ), 'user not logged in' );

		event = baseEvent();
		event.user.edit_count_bucket = '5-99 edits';
		assert.notOk( curationController.shouldProduceEvent( event, streamConfig ), 'wrong user edit count bucket' );

		event = baseEvent();
		event.device.pixel_ratio = 1.0;
		assert.notOk( curationController.shouldProduceEvent( event, streamConfig ), 'device pixel ratio too low' );

		event = baseEvent();
		event.device.pixel_ratio = 3.0;
		assert.notOk( curationController.shouldProduceEvent( event, streamConfig ), 'device pixel ratio too high' );
	} );

}() );
