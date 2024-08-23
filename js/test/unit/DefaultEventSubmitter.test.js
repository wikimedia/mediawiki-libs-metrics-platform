'use strict';

const DefaultEventSubmitter = require( '../../src/NodeEventSubmitter.js' );

QUnit.module( 'DefaultEventSubmitter' );

QUnit.test( 'constructor() - sets eventGateUrl', ( assert ) => {
	let eventSubmitter = new DefaultEventSubmitter();

	assert.strictEqual(
		eventSubmitter.eventGateUrl,
		'https://intake-analytics.wikimedia.org/v1/events?hasty=true'
	);

	eventSubmitter = new DefaultEventSubmitter( 'http://default_integration.new/foo/bar' );

	assert.strictEqual(
		eventSubmitter.eventGateUrl,
		'http://default_integration.new/v1/events?hasty=true'
	);
} );
