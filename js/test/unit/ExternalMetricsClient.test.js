// TypeScript doesn't support ES5-style inheritance
// (see https://github.com/microsoft/TypeScript/issues/18609).
// @ts-nocheck

/* eslint-disable camelcase */
var sinon = require( 'sinon' );

var TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
var MetricsClient = require( '../../src/ExternalMetricsClient.js' );

/** @type StreamConfigs */
var streamConfigs = {
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

var integration = new TestMetricsClientIntegration();

var sandbox = sinon.createSandbox();
var enqueueEventStub = sandbox.stub( integration, 'enqueueEvent' );
var logWarningStub = sandbox.stub( integration, 'logWarning' );

sandbox.stub( integration, 'onSubmit' );

QUnit.module( 'ExternalMetricsClient', {
	beforeEach: function () {
		sandbox.reset();
	}
} );

QUnit.test( 'constructor() - fetches stream configs when they are not given', function ( assert ) {
	var fetchStreamConfigsSpy = sandbox.spy( integration, 'fetchStreamConfigs' );

	var metricsClient = new MetricsClient( integration );

	assert.deepEqual( metricsClient.streamConfigs, {}, 'Initializes streamConfigs to an empty object' );
	assert.strictEqual( fetchStreamConfigsSpy.callCount, 1, 'fetchStreamConfigs() should not be called' );

	fetchStreamConfigsSpy.restore();
} );

QUnit.test( 'fetchStreamConfigs() - invalidates eventNameToStreamNames map', function ( assert ) {
	var fetchStreamConfigsStub = sandbox.stub( integration, 'fetchStreamConfigs' );

	// @ts-ignore TS2585
	fetchStreamConfigsStub.onFirstCall().returns( Promise.resolve( streamConfigs ) );

	var metricsClient = new MetricsClient( integration );
	metricsClient.getStreamNamesForEvent( 'widgetClick' );

	assert.deepEqual( metricsClient.eventNameToStreamNamesMap, {} );

	var done = assert.async();

	// Because Integration#fetchStreamConfigs() returns an instance of Promise, we have to wait for
	// the microtask queue to be drained before making an assertion.
	//
	// @ts-ignore TS2580
	process.nextTick( function () {
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

QUnit.test( 'submit()/dispatch() - does not produce an event until streamConfigs are fetched', function ( assert ) {
	var streamConfigsPromise = new Promise( function ( resolve ) {
		setTimeout(
			function () {
				resolve( streamConfigs );
			},
			250
		);
	} );

	var fetchStreamConfigsStub = sandbox.stub( integration, 'fetchStreamConfigs' );

	// @ts-ignore TS2585
	fetchStreamConfigsStub.onFirstCall().returns( streamConfigsPromise );

	var metricsClient = new MetricsClient( integration );

	metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );
	metricsClient.dispatch( 'click' );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( enqueueEventStub.callCount, 0, 'enqueueEvent() should not be called' );

	var done = assert.async();

	streamConfigsPromise.then( function () {
		assert.strictEqual( enqueueEventStub.callCount, 2, 'enqueueEvent() should be called' );

		fetchStreamConfigsStub.restore();

		done();
	} );
} );

QUnit.test( 'logs a warning if too many calls are enqueued before stream configs are fetched', function ( assert ) {
	var streamConfigsPromise = new Promise( function ( resolve ) {
		setTimeout(
			function () {
				resolve( streamConfigs );
			},
			250
		);
	} );

	var fetchStreamConfigsStub = sandbox.stub( integration, 'fetchStreamConfigs' );

	// @ts-ignore TS2585
	fetchStreamConfigsStub.onFirstCall().returns( streamConfigsPromise );

	var metricsClient = new MetricsClient( integration );

	for ( var i = 0; i < 132; i += 2 ) {
		metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );
		metricsClient.dispatch( 'click' );
	}

	assert.strictEqual( logWarningStub.callCount, 4, 'logWarning() should be called twice' );
	assert.strictEqual( logWarningStub.args[ 0 ][ 0 ], 'Call to submit( metrics.platform.test, eventData ) dropped because the queue is full.' );
	assert.strictEqual( logWarningStub.args[ 1 ][ 0 ], 'Call to dispatch( click, customData ) dropped because the queue is full.' );
	assert.strictEqual( logWarningStub.args[ 2 ][ 0 ], 'Call to submit( metrics.platform.test, eventData ) dropped because the queue is full.' );
	assert.strictEqual( logWarningStub.args[ 3 ][ 0 ], 'Call to dispatch( click, customData ) dropped because the queue is full.' );

	assert.strictEqual( enqueueEventStub.callCount, 0, 'enqueueEvent() should not be called' );

	var done = assert.async();

	streamConfigsPromise.then( function () {
		assert.strictEqual( enqueueEventStub.callCount, 128, 'enqueueEvent() should be called' );

		fetchStreamConfigsStub.restore();

		done();
	} );
} );
