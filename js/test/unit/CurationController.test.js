'use strict';

/* eslint-disable camelcase */

const curationController = new ( require( './../../src/CurationController.js' ) )();

QUnit.module( 'CurationController', () => {

	QUnit.module( 'applyRules()', () => {
		QUnit.test( 'equals', ( assert ) => {
			assert.true( curationController.applyRules( 1, { equals: 1 } ) );
			assert.true( curationController.applyRules( 'a', { equals: 'a' } ) );
			assert.false( curationController.applyRules( 1, { equals: 0 } ) );
			assert.false( curationController.applyRules( '1', { equals: 1 } ) );
			assert.false( curationController.applyRules( '0', { equals: 0 } ) );
			assert.false( curationController.applyRules( 0, { equals: undefined } ) );
		} );
		QUnit.test( 'greaterThan', ( assert ) => {
			assert.true( curationController.applyRules( 1, { greater_than: 0 } ) );
			assert.true( curationController.applyRules( 1.2, { greater_than: 1.1 } ) );
			assert.false( curationController.applyRules( 1.1, { greater_than: 1.1 } ) );
			assert.false( curationController.applyRules( 0, { greater_than: 1 } ) );
			assert.false( curationController.applyRules( 0, { greater_than: 0 } ) );
		} );
		QUnit.test( 'lessThan', ( assert ) => {
			assert.true( curationController.applyRules( 0, { less_than: 1 } ) );
			assert.true( curationController.applyRules( 1.1, { less_than: 1.2 } ) );
			assert.false( curationController.applyRules( 1.1, { less_than: 1.1 } ) );
			assert.false( curationController.applyRules( 1, { less_than: 0 } ) );
			assert.false( curationController.applyRules( 0, { less_than: 0 } ) );
		} );
		QUnit.test( 'in', ( assert ) => {
			assert.true( curationController.applyRules( 1, { in: [ 1 ] } ) );
			assert.false( curationController.applyRules( 1, { in: [ 0 ] } ) );
		} );
		QUnit.test( 'contains', ( assert ) => {
			assert.true( curationController.applyRules( [ 1 ], { contains: 1 } ) );
			assert.false( curationController.applyRules( [ 1 ], { contains: 0 } ) );
		} );
		QUnit.test( 'containsAll', ( assert ) => {
			assert.true(
				curationController.applyRules( [ 0, 1 ], { contains_all: [ 0, 1 ] } )
			);
			assert.true(
				curationController.applyRules( [ 0, 1 ], { contains_all: [ 1, 0 ] } )
			);
			assert.false( curationController.applyRules( [], { contains_all: [ 1 ] } ) );
		} );
		QUnit.test( 'containsAny', ( assert ) => {
			assert.true(
				curationController.applyRules(
					[ 0, 1 ],
					{ contains_any: [ 0, 1 ] }
				)
			);
			assert.true(
				curationController.applyRules(
					[ 0, 1 ],
					{ contains_any: [ 1, 2 ] }
				)
			);
		} );
	} );

	QUnit.test( 'shouldProduceEvent()', ( assert ) => {
		let event;

		/** @type StreamConfig */
		const streamConfig = {
			producers: {
				metrics_platform_client: {
					curation: {
						page_id: {
							less_than: 500,
							not_equals: 42
						},
						page_namespace_name: {
							equals: 'Talk'
						},
						performer_is_logged_in: {
							equals: true
						},
						performer_edit_count_bucket: {
							in: [ '100-999 edits', '1000+ edits' ]
						},
						performer_groups: {
							contains_all: [ 'user', 'autoconfirmed' ],
							does_not_contain: 'sysop'
						}
					}
				}
			}
		};

		/**
		 * @return {EventData}
		 */
		function baseEvent() {
			return {
				$schema: 'test/event',
				meta: {
					stream: 'test.event'
				},
				page: {
					id: 1,
					namespace_name: 'Talk'
				},
				performer: {
					groups: [ 'user', 'autoconfirmed', 'steward' ],
					is_logged_in: true,
					edit_count_bucket: '1000+ edits'
				},
				device: {
					pixel_ratio: 2.0
				}
			};
		}

		assert.true(
			curationController.shouldProduceEvent( baseEvent(), streamConfig )
		);

		event = baseEvent();
		event.page = event.page || {};
		event.page.id = 42;
		assert.false(
			curationController.shouldProduceEvent( event, streamConfig ),
			'wrong page id'
		);

		event = baseEvent();
		event.page = event.page || {};
		event.page.namespace_name = 'User';
		assert.false(
			curationController.shouldProduceEvent( event, streamConfig ),
			'wrong page namespace text'
		);

		event = baseEvent();
		event.performer = event.performer || {};
		event.performer.groups = [ 'user', 'autoconfirmed', 'sysop' ];
		assert.false(
			curationController.shouldProduceEvent( event, streamConfig ),
			'wrong user groups'
		);

		event = baseEvent();
		event.performer = event.performer || {};
		event.performer.groups = [];
		assert.false(
			curationController.shouldProduceEvent( event, streamConfig ),
			'no user groups'
		);

		event = baseEvent();
		event.performer = event.performer || {};
		event.performer.is_logged_in = false;
		assert.false(
			curationController.shouldProduceEvent( event, streamConfig ),
			'user not logged in'
		);

		event = baseEvent();
		event.performer = event.performer || {};
		event.performer.edit_count_bucket = '5-99 edits';
		assert.false(
			curationController.shouldProduceEvent( event, streamConfig ),
			'wrong user edit count bucket'
		);
	} );
} );
