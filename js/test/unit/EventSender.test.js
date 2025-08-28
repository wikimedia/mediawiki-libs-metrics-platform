'use strict';

/* eslint-disable camelcase */

const sinon = require( 'sinon' );

const StubEventTransport = require( './StubEventTransport.js' );
const { DefaultEventSender } = require( './../../src/EventSender.js' );

/** @type {EventData} */
const modernEvent = {
	$schema: '/test/event/1.0.0',
	volcano: 'Nyiragongo',
	Explosivity: 1
};

/** @type {EventData} */
const legacyEvent = {
	$schema: '/test/event/1.0.0',
	volcano: 'Nyiragongo',
	Explosivity: 1,
	client_dt: '2021-05-12T00:00:00.000Z',
	dt: '2021-05-12T00:00:00.000Z'
};

const eventTransport = new StubEventTransport();
const eventSender = new DefaultEventSender(
	'foo.domain',
	'foo.stream',
	{
		agent: {
			client_platform: 'mediawiki_js',
			client_platform_family: 'mobile_browser'
		}
	},
	eventTransport
);

const sandbox = sinon.createSandbox();
const transportEventStub = sandbox.stub( eventTransport, 'transportEvent' );

const hasOwnProperty = Object.prototype.hasOwnProperty;

QUnit.module( 'MetricsClient', {
	beforeEach: () => {
		sandbox.reset();
	}
} );

QUnit.test( 'sendEvent() - sets dt field if it is not set', ( assert ) => {
	eventSender.sendEvent( modernEvent );

	const sentEvent = transportEventStub.args[ 0 ][ 0 ];

	assert.true( hasOwnProperty.call( sentEvent, 'dt' ), 'dt should be set' );
} );

QUnit.test( 'addRequiredMetadata() - removes dt field if the client_dt field is set', ( assert ) => {
	eventSender.sendEvent( legacyEvent );

	const sentEvent = transportEventStub.args[ 0 ][ 0 ];

	assert.true( hasOwnProperty.call( sentEvent, 'client_dt' ), 'client_dt should be set' );
	assert.false( hasOwnProperty.call( sentEvent, 'dt' ), 'dt should not be set' );
} );
