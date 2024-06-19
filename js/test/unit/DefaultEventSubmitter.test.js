const DefaultEventSubmitter = require( '../../src/DefaultEventSubmitter' );

QUnit.module( 'DefaultEventSubmitter' );

QUnit.test( 'constructor() - sets eventGateUrl', function ( assert ) {
	var eventSubmitter = new DefaultEventSubmitter();

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
