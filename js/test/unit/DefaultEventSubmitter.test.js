'use strict';

const DefaultEventSubmitter = require( '../../src/NodeEventSubmitter.js' );

QUnit.module( 'DefaultEventSubmitter' );

QUnit.test( 'constructor() - sets default eventGateUrl', ( assert ) => {
	const eventSubmitter = new DefaultEventSubmitter();

	assert.strictEqual(
		eventSubmitter.eventIntakeUrl,
		'https://intake-analytics.wikimedia.org/v1/events?hasty=true'
	);
} );
