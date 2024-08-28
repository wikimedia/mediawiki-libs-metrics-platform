'use strict';

/* eslint-disable camelcase */

const sinon = require( 'sinon' );

const TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
const StubEventSubmitter = require( './StubEventSubmitter.js' );
const MetricsClient = require( '../../src/MetricsClient.js' );
const Instrument = require( '../../src/Instrument.js' );

const integration = new TestMetricsClientIntegration();
const stubEventSubmitter = new StubEventSubmitter();
const metricsClient = new MetricsClient( integration, false, stubEventSubmitter );
const instrument = new Instrument( metricsClient, 'fooStreamName', 'fooSchemaID' );

const sandbox = sinon.createSandbox();

const submitInteractionStub = sandbox.stub( metricsClient, 'submitInteraction' );

QUnit.module( 'Instrument', {
	afterEach: () => {
		sandbox.reset();
	}
} );

QUnit.test( 'isStreamInSample()', ( assert ) => {
	const streamInSampleStub = sandbox.stub( metricsClient, 'isStreamInSample' );
	streamInSampleStub.returns( true );

	assert.strictEqual( instrument.isStreamInSample(), true );

	assert.strictEqual( streamInSampleStub.callCount, 1 );
	assert.strictEqual( streamInSampleStub.args[ 0 ][ 0 ], 'fooStreamName' );

	streamInSampleStub.restore();
} );

QUnit.test( 'submitInteraction()', ( assert ) => {
	instrument.submitInteraction( 'init' );
	instrument.submitInteraction( 'scroll', {
		action_subtype: 'up'
	} );

	assert.strictEqual( submitInteractionStub.callCount, 2 );
	assert.deepEqual(
		submitInteractionStub.args[ 0 ],
		[
			'fooStreamName',
			'fooSchemaID',
			'init',
			{
				funnel_event_sequence_position: 1
			}
		]
	);
	assert.deepEqual(
		submitInteractionStub.args[ 1 ],
		[
			'fooStreamName',
			'fooSchemaID',
			'scroll',
			{
				action_subtype: 'up',
				funnel_event_sequence_position: 2
			}
		]
	);
} );

QUnit.test( 'submitInteraction() - disallow FESP overriding', ( assert ) => {
	instrument.submitInteraction( 'scroll', {
		action_subtype: 'up',
		funnel_event_sequence_position: 42
	} );

	assert.deepEqual( submitInteractionStub.args[ 0 ][ 3 ], {
		action_subtype: 'up',
		funnel_event_sequence_position: 3
	} );
} );
