'use strict';

/* eslint-disable camelcase */

const sinon = require( 'sinon' );

const Instrument = require( '../../src/Instrument.js' );
const { DummyEventSender } = require( '../../src/EventSender.js' );

const eventSender = new DummyEventSender();
const instrument = new Instrument( eventSender, 'fooSchemaID' );

const sandbox = sinon.createSandbox();

const sendEventStub = sandbox.stub( eventSender, 'sendEvent' );

QUnit.module( 'Instrument', {
	afterEach: () => {
		sandbox.reset();
	}
} );

QUnit.test( 'isStreamInSample()', ( assert ) => {
	assert.strictEqual( instrument.isStreamInSample(), false );
} );

QUnit.test( 'submitInteraction()', ( assert ) => {
	instrument.submitInteraction( 'init' );
	instrument.submitInteraction( 'scroll', {
		action_subtype: 'up'
	} );

	assert.strictEqual( sendEventStub.callCount, 2 );
	assert.deepEqual(
		sendEventStub.args[ 0 ][ 0 ],
		{
			action: 'init',
			$schema: 'fooSchemaID',
			funnel_event_sequence_position: 1
		}
	);
	assert.deepEqual(
		sendEventStub.args[ 1 ][ 0 ],
		{
			action: 'scroll',
			$schema: 'fooSchemaID',
			action_subtype: 'up',
			funnel_event_sequence_position: 2
		}
	);
} );

QUnit.test( 'submitInteraction() - disallow FESP overriding', ( assert ) => {
	instrument.submitInteraction( 'scroll', {
		action_subtype: 'up',
		funnel_event_sequence_position: 42
	} );

	assert.deepEqual(
		sendEventStub.args[ 0 ][ 0 ],
		{
			action: 'scroll',
			$schema: 'fooSchemaID',
			action_subtype: 'up',
			funnel_event_sequence_position: 3
		}
	);
} );
