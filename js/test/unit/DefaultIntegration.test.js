'use strict';

const DefaultIntegration = require( './../../src/DefaultIntegration.js' );

QUnit.module( 'DefaultIntegration' );

QUnit.test( 'constructor() - sets streamConfigsUrl', ( assert ) => {
	let integration = new DefaultIntegration();

	assert.strictEqual(
		integration.streamConfigsUrl,
		'https://meta.wikimedia.org/w/api.php?action=streamconfigs&format=json&formatversion=2&constraints=destination_event_service%3Deventgate-analytics-external'
	);

	integration = new DefaultIntegration( 'http://default_integration.new/foo/bar.php' );

	assert.strictEqual(
		integration.streamConfigsUrl,
		'http://default_integration.new/w/api.php?action=streamconfigs&format=json&formatversion=2&constraints=destination_event_service%3Deventgate-analytics-external',
		'The path is set to /w/api.php'
	);
} );

QUnit.test( 'getContextAttributes()/setContextAttributes()', ( assert ) => {
	/* eslint-disable camelcase */
	const integration = new DefaultIntegration();

	assert.deepEqual(
		integration.getContextAttributes(),
		{}
	);

	integration.setContextAttributes( {
		page: {
			id: 63367904,
			title: 'Sleep_Token'
		},
		performer: {
			is_logged_in: false
		}
	} );

	assert.deepEqual(
		integration.getContextAttributes(),
		{
			page: {
				id: 63367904,
				title: 'Sleep_Token'
			},
			performer: {
				is_logged_in: false
			}
		}
	);

	integration.setContextAttributes( {
		page: {
			id: 15580374
		}
	} );

	assert.deepEqual(
		integration.getContextAttributes(),
		{
			page: {
				id: 15580374,
				title: 'Sleep_Token'
			},
			performer: {
				is_logged_in: false
			}
		}
	);
	/* eslint-enable camelcase */
} );
