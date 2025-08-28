'use strict';

const DefaultEventTransport = require( '../../src/NodeEventTransport.js' );

QUnit.module( 'DefaultEventTransport' );

QUnit.test( 'constructor() - sets default eventGateUrl', ( assert ) => {
	const eventTransport = new DefaultEventTransport();

	assert.strictEqual(
		eventTransport.eventIntakeUrl,
		'https://intake-analytics.wikimedia.org/v1/events?hasty=true'
	);
} );
