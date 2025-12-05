'use strict';

/* eslint-disable camelcase */

const sinon = require( 'sinon' );

const TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
const TestMetricsClientLogger = require( './TestMetricsClientLogger.js' );
const StubEventSubmitter = require( './StubEventSubmitter.js' );
const MetricsClient = require( './../../src/MetricsClient.js' );

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
	},
	'metrics.platform.test6': {
		schema_title: '/analytics/product_metrics/interaction/common'
	},
	'metrics.platform.test7': {
		schema_title: '/analytics/product_metrics/web/base'
	},
	'metrics.platform.test8': {
		schema_title: '/analytics/product_metrics/web/base',
		producers: {
			metrics_platform_client: {
				stream_name: 'metrics.platform.test7'
			}
		}
	}
};

/** @type EventData */
let modernEvent = {
	$schema: '/test/event/1.0.0',
	volcano: 'Nyiragongo',
	Explosivity: 1
};

/** @type EventData */
let legacyEvent = {
	$schema: '/test/event/1.0.0',
	volcano: 'Nyiragongo',
	Explosivity: 1,
	client_dt: '2021-05-12T00:00:00.000Z',
	dt: '2021-05-12T00:00:00.000Z'
};

const integration = new TestMetricsClientIntegration();
const logger = new TestMetricsClientLogger();
const eventSubmitter = new StubEventSubmitter();
const metricsClient = new MetricsClient( integration, logger, streamConfigs, eventSubmitter );

const sandbox = sinon.createSandbox();
const submitEventStub = sandbox.stub( eventSubmitter, 'submitEvent' );
const logWarningStub = sandbox.stub( logger, 'logWarning' );

QUnit.module( 'MetricsClient', {
	beforeEach: () => {
		sandbox.reset();
	}
} );

QUnit.test( 'submit() - warn/do not produce for event without $schema', ( assert ) => {
	metricsClient.submit( 'metrics.platform.test', {} );

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( submitEventStub.callCount, 0, 'submitEvent() should not be called' );
} );

QUnit.test( 'submit() - produce an event correctly', ( assert ) => {
	metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( submitEventStub.callCount, 1, 'submitEvent() should be called' );
} );

QUnit.test( 'submit() - produce an event to a redirected stream', ( assert ) => {
	metricsClient.submit( 'metrics.platform.test8', { $schema: '/analytics/product_metrics/web/base/1.4.3' } );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( submitEventStub.callCount, 1, 'submitEvent() should be called' );

	const event = submitEventStub.args[ 0 ][ 0 ];

	assert.strictEqual( event.meta.stream, 'metrics.platform.test7', 'The event was redirect' );
} );

QUnit.test( 'submit() - check if the stream is in-sample', ( assert ) => {
	const isStreamInSampleSpy = sandbox.spy( metricsClient.samplingController, 'isStreamInSample' );

	metricsClient.submit( 'metrics.platform.test8', { $schema: '/analytics/product_metrics/web/base/1.4.3' } );

	const expectedStreamConfig = streamConfigs[ 'metrics.platform.test8' ];

	assert.strictEqual( isStreamInSampleSpy.callCount, 1, 'isStreamInSample() should be called' );
	assert.deepEqual(
		isStreamInSampleSpy.args[ 0 ][ 0 ],
		expectedStreamConfig,
		'isStreamInSample() should be called with the config for the original stream'
	);

	isStreamInSampleSpy.restore();
} );

QUnit.test( 'streamConfig() - disallow modification', ( assert ) => {
	let streamConfig = metricsClient.getStreamConfig( 'metrics.platform.test' ) || {};
	streamConfig.schema_title = 'fake/title';

	streamConfig = metricsClient.getStreamConfig( 'metrics.platform.test' ) || {};

	assert.strictEqual( streamConfig.schema_title, 'metrics/platform/test' );
} );

QUnit.test( 'addRequiredMetadata() - modern event', ( assert ) => {
	const hasOwnProperty = Object.prototype.hasOwnProperty;

	modernEvent = metricsClient.addRequiredMetadata( modernEvent, 'metrics.platform.test' );
	assert.true( hasOwnProperty.call( modernEvent, 'dt' ), 'dt should be set' );
	assert.deepEqual( modernEvent.meta, {
		stream: 'metrics.platform.test',
		domain: 'test.example.com'
	} );
} );

QUnit.test( 'addRequiredMetadata() - legacy event', ( assert ) => {
	const hasOwnProperty = Object.prototype.hasOwnProperty;

	legacyEvent = metricsClient.addRequiredMetadata( legacyEvent, 'metrics.platform.test' );
	assert.true( hasOwnProperty.call( legacyEvent, 'client_dt' ), 'client_dt should be set' );
	assert.false( hasOwnProperty.call( legacyEvent, 'dt' ), 'dt should not be set' );
	assert.deepEqual( legacyEvent.meta, {
		stream: 'metrics.platform.test',
		domain: 'test.example.com'
	} );
} );

QUnit.test( 'getStreamNamesForEvent() ', ( assert ) => {
	/** @type {[string, string[], string][]} */
	const cases = [
		[
			'widgetClick', // eventName
			[ 'metrics.platform.test2', 'metrics.platform.test3' ], // expected
			'Many streams can be interested in an event' // message
		],

		// Exercise the memoization logic.
		[
			'widgetClick',
			[ 'metrics.platform.test2', 'metrics.platform.test3' ],
			'Memoization should not affect the result'
		],
		[
			'otherWidgetClick',
			[ 'metrics.platform.test2' ],
			'One stream can be interested in multiple events'
		],
		[
			'unknownEventName',
			[],
			'Zero streams can be interested in an event'
		]
	];
	cases.forEach( ( [ eventName, expected, message ] ) => {
		assert.deepEqual(
			metricsClient.getStreamNamesForEvent( eventName ),
			expected,
			message
		);
	} );
} );

QUnit.test( 'getStreamNamesForEvent() - prefix matching', ( assert ) => {
	assert.deepEqual(
		metricsClient.getStreamNamesForEvent( 'widgetClickFoo' ),
		[
			'metrics.platform.test2',
			'metrics.platform.test3'
		]
	);
	assert.deepEqual(
		metricsClient.getStreamNamesForEvent( 'click' ),
		[ 'metrics.platform.test5' ]
	);
} );

QUnit.test( 'getStreamNamesForEvent() - streamConfigs is falsy', ( assert ) => {
	// eslint-disable-next-line no-shadow
	const metricsClient = new MetricsClient( integration, logger, false, eventSubmitter );

	assert.deepEqual( metricsClient.getStreamNamesForEvent( 'foo' ), [] );
	assert.strictEqual(
		metricsClient.eventNameToStreamNamesMap,
		null,
		'No memoized map of event name to stream names is generated'
	);
} );

QUnit.test( 'submitInteraction() - warn/do not produce for interactionData without action', ( assert ) => {
	metricsClient.submitInteraction(
		'metrics.platform.test6',
		'/fragment/analytics/product_metrics/interaction/common/1.0.0'
	);

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( submitEventStub.callCount, 0, 'submitEvent() should not be called' );
} );

QUnit.test( 'submitInteraction() - produce event correctly', ( assert ) => {
	metricsClient.submitInteraction(
		'metrics.platform.test6',
		'/analytics/product_metrics/web/base/1.0.0',
		'foo'
	);

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( submitEventStub.callCount, 1, 'submitEvent() should be called' );

	const event = submitEventStub.args[ 0 ][ 0 ];

	assert.strictEqual( event.meta.stream, 'metrics.platform.test6' );
	assert.strictEqual( event.$schema, '/analytics/product_metrics/web/base/1.0.0' );
	assert.strictEqual( event.action, 'foo' );
} );

QUnit.test( 'submitInteraction() - disallow $schema overriding', ( assert ) => {
	metricsClient.submitInteraction(
		'metrics.platform.test6',
		'/analytics/product_metrics/web/base/1.0.0',
		'foo',
		{
			$schema: '/bar/baz/1.0.0'
		}
	);

	const event = submitEventStub.args[ 0 ][ 0 ];

	assert.strictEqual( event.$schema, '/analytics/product_metrics/web/base/1.0.0' );
} );

// T381849: Testing temporarily for growthExperiments to be able to add experiment details
// as interaction data via the `experiment` fragment
QUnit.test( 'submitInteraction() - experiments details are added when included as interaction data using the `experiment` fragment', ( assert ) => {
	const experimentsAsInteractionData = {
		action_subtype: 'foo',
		action_source: 'bar',
		experiment: {
			assigned: 'community-updates-module',
			enrolled: 'growth-experiments',
			coordinator: 'custom'
		}
	};

	metricsClient.submitInteraction(
		'metrics.platform.test7',
		'/analytics/product_metrics/web/base/1.4.1',
		'someAction',
		experimentsAsInteractionData
	);

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( submitEventStub.callCount, 1, 'submitEvent() should be called' );

	const event = submitEventStub.args[ 0 ][ 0 ];

	assert.deepEqual( event.experiment.assigned, 'community-updates-module' );
	assert.deepEqual( event.experiment.enrolled, 'growth-experiments' );
	assert.deepEqual( event.experiment.coordinator, 'custom' );
} );

// T381849, T377098: Testing temporarily for growthExperiments to be able to add experiment details
// as interaction data via the `experiments` fragment
QUnit.test( 'submitInteraction() - experiments details are added when included as interaction data using the `experiments` fragment', ( assert ) => {
	const experimentsAsInteractionData = {
		action_subtype: 'foo',
		action_source: 'bar',
		experiments: {
			assigned: { 'growth-experiments': 'community-updates-module' },
			enrolled: [ 'growth-experiments' ]
		}
	};

	metricsClient.submitInteraction(
		'metrics.platform.test7',
		'/analytics/product_metrics/web/base/1.4.1',
		'someAction',
		experimentsAsInteractionData
	);

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( submitEventStub.callCount, 1, 'submitEvent() should be called' );

	const event = submitEventStub.args[ 0 ][ 0 ];

	assert.deepEqual( event.experiments.assigned, {
		'growth-experiments': 'community-updates-module'
	} );
	assert.deepEqual( event.experiments.enrolled, [ 'growth-experiments' ] );
} );
