// TypeScript doesn't support ES5-style inheritance
// (see https://github.com/microsoft/TypeScript/issues/18609).
// @ts-nocheck

'use strict';

/* eslint-disable camelcase */

const sinon = require( 'sinon' );

const TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
const MetricsClient = require( '../../src/ExternalMetricsClient.js' );
const StubEventSubmitter = require( './StubEventSubmitter.js' );

/** @type StreamConfigs */
const streamConfigs = {
	'metrics.platform.test': {
		schema_title: 'metrics/platform/test',
		producers: {
			metrics_platform_client: {
				provide_values: [
					'mediawiki_skin'
				]
			}
		}
	},
	'metrics.platform.test2': {
		schema_title: 'metrics/platform/test2',
		producers: {
			metrics_platform_client: {
				events: [
					'widgetClick',
					'otherWidgetClick'
				]
			}
		}
	},
	'metrics.platform.test3': {
		schema_title: 'metrics/platform/test3',
		producers: {
			metrics_platform_client: {
				events: [ 'widgetClick' ]
			}
		}
	},
	'metrics.platform.test4': {
		schema_title: 'metrics/platform/test4'
	},
	'metrics.platform.test5': {
		schema_title: 'metrics/platform/test5',
		producers: {
			metrics_platform_client: {
				events: [
					'click'
				]
			}
		}
	}
};

const eventSubmitter = new StubEventSubmitter();
const integration = new TestMetricsClientIntegration();

const sandbox = sinon.createSandbox();
const submitEventStub = sandbox.stub( eventSubmitter, 'submitEvent' );
const logWarningStub = sandbox.stub( integration, 'logWarning' );

sandbox.stub( eventSubmitter, 'onSubmitEvent' );

QUnit.module( 'ExternalMetricsClient', {
	beforeEach: () => {
		sandbox.reset();
	}
} );

QUnit.test( 'constructor() - fetches stream configs when they are not given', ( assert ) => {
	const fetchStreamConfigsSpy = sandbox.spy( integration, 'fetchStreamConfigs' );

	const metricsClient = new MetricsClient( integration, eventSubmitter );

	assert.deepEqual( metricsClient.streamConfigs, {}, 'Initializes streamConfigs to an empty object' );
	assert.strictEqual( fetchStreamConfigsSpy.callCount, 1, 'fetchStreamConfigs() should not be called' );

	fetchStreamConfigsSpy.restore();
} );

QUnit.test( 'fetchStreamConfigs() - invalidates eventNameToStreamNames map', ( assert ) => {
	const fetchStreamConfigsStub = sandbox.stub( integration, 'fetchStreamConfigs' );

	// @ts-ignore TS2585
	fetchStreamConfigsStub.onFirstCall().returns( Promise.resolve( streamConfigs ) );

	const metricsClient = new MetricsClient( integration, eventSubmitter );
	metricsClient.getStreamNamesForEvent( 'widgetClick' );

	assert.deepEqual( metricsClient.eventNameToStreamNamesMap, {} );

	const done = assert.async();

	// Because Integration#fetchStreamConfigs() returns an instance of Promise, we have to wait for
	// the microtask queue to be drained before making an assertion.
	//
	// @ts-ignore TS2580
	process.nextTick( () => {
		assert.strictEqual( metricsClient.streamConfigs, streamConfigs );
		assert.strictEqual(
			metricsClient.eventNameToStreamNamesMap,
			null,
			'eventNameToStreamNames map is invalidated when stream configs are fetched and updated'
		);

		fetchStreamConfigsStub.restore();

		done();
	} );
} );

QUnit.test( 'submit()/dispatch() - does not produce an event until streamConfigs are fetched', ( assert ) => {
	const streamConfigsPromise = new Promise( ( resolve ) => {
		setTimeout(
			() => {
				resolve( streamConfigs );
			},
			250
		);
	} );

	const fetchStreamConfigsStub = sandbox.stub( integration, 'fetchStreamConfigs' );

	// @ts-ignore TS2585
	fetchStreamConfigsStub.onFirstCall().returns( streamConfigsPromise );

	const metricsClient = new MetricsClient( integration, eventSubmitter );

	metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );
	metricsClient.dispatch( 'click' );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( submitEventStub.callCount, 0, 'submitEvent() should not be called' );

	const done = assert.async();

	streamConfigsPromise.then( () => {
		assert.strictEqual( submitEventStub.callCount, 2, 'submitEvent() should be called' );

		fetchStreamConfigsStub.restore();

		done();
	} );
} );

QUnit.test( 'logs a warning if too many calls are enqueued before stream configs are fetched', ( assert ) => {
	const streamConfigsPromise = new Promise( ( resolve ) => {
		setTimeout(
			() => {
				resolve( streamConfigs );
			},
			250
		);
	} );

	const fetchStreamConfigsStub = sandbox.stub( integration, 'fetchStreamConfigs' );

	// @ts-ignore TS2585
	fetchStreamConfigsStub.onFirstCall().returns( streamConfigsPromise );

	const metricsClient = new MetricsClient( integration, eventSubmitter );

	for ( let i = 0; i < 132; i += 2 ) {
		metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );
		metricsClient.dispatch( 'click' );
	}

	assert.strictEqual( logWarningStub.callCount, 4, 'logWarning() should be called twice' );
	assert.strictEqual( logWarningStub.args[ 0 ][ 0 ], 'Call to submit( metrics.platform.test, eventData ) dropped because the queue is full.' );
	assert.strictEqual( logWarningStub.args[ 1 ][ 0 ], 'Call to dispatch( click, customData ) dropped because the queue is full.' );
	assert.strictEqual( logWarningStub.args[ 2 ][ 0 ], 'Call to submit( metrics.platform.test, eventData ) dropped because the queue is full.' );
	assert.strictEqual( logWarningStub.args[ 3 ][ 0 ], 'Call to dispatch( click, customData ) dropped because the queue is full.' );

	assert.strictEqual( submitEventStub.callCount, 0, 'submitEvent() should not be called' );

	const done = assert.async();

	streamConfigsPromise.then( () => {
		assert.strictEqual( submitEventStub.callCount, 128, 'submitEvent() should be called' );

		fetchStreamConfigsStub.restore();

		done();
	} );
} );
