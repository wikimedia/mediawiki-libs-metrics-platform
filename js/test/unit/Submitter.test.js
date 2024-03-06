/* eslint-disable camelcase */

const sinon = require( 'sinon' );

const TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
const MetricsClient = require( './../../src/MetricsClient.js' );
const Submitter = require( './../../src/Submitter.js' );

const integration = new TestMetricsClientIntegration();
const metricsClient = new MetricsClient( integration, false );
const submitter = new Submitter( metricsClient, 'fooStreamName', 'fooSchemaID' );

const sandbox = sinon.createSandbox();

QUnit.module( 'Submitter', {
	afterEach: () => {
		sandbox.reset();
	}
} );

QUnit.test( 'isStreamInSample()', ( assert ) => {
	const streamInSampleStub = sandbox.stub( metricsClient, 'isStreamInSample' );
	streamInSampleStub.returns( true );

	assert.strictEqual( submitter.isStreamInSample(), true );

	assert.strictEqual( streamInSampleStub.callCount, 1 );
	assert.strictEqual( streamInSampleStub.args[ 0 ][ 0 ], 'fooStreamName' );

	streamInSampleStub.restore();
} );

QUnit.test( 'submitInteraction()', ( assert ) => {
	const submitInteractionStub = sandbox.stub( metricsClient, 'submitInteraction' );

	submitter.submitInteraction( 'init' );
	submitter.submitInteraction( 'scroll', {
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
				funnel_event_sequence_position: 0
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
				funnel_event_sequence_position: 1
			}
		]
	);

	submitInteractionStub.restore();
} );

QUnit.test( 'submitInteraction() - disallow FESP overriding', ( assert ) => {
	const submitInteractionStub = sandbox.stub( metricsClient, 'submitInteraction' );

	submitter.submitInteraction( 'scroll', {
		action_subtype: 'up',
		funnel_event_sequence_position: 42
	} );

	assert.deepEqual( submitInteractionStub.args[ 0 ][ 3 ], {
		action_subtype: 'up',
		funnel_event_sequence_position: 2
	} );

	submitInteractionStub.restore();
} );
