'use strict';

/* eslint-disable camelcase */

const sinon = require( 'sinon' );

const TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
const StubEventTransport = require( './StubEventTransport.js' );
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
const eventTransport = new StubEventTransport();
const metricsClient = new MetricsClient( integration, streamConfigs, eventTransport );

const sandbox = sinon.createSandbox();
const transportEventStub = sandbox.stub( eventTransport, 'transportEvent' );
const logWarningStub = sandbox.stub( integration, 'logWarning' );

QUnit.module( 'MetricsClient', {
	beforeEach: () => {
		sandbox.reset();
	}
} );

QUnit.test( 'submit() - warn/do not produce for event without $schema', ( assert ) => {
	metricsClient.submit( 'metrics.platform.test', {} );

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( transportEventStub.callCount, 0, 'transportEvent() should not be called' );
} );

QUnit.test( 'submit() - produce an event correctly', ( assert ) => {
	metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( transportEventStub.callCount, 1, 'transportEvent() should be called' );
} );

QUnit.test( 'streamConfig() - disallow modification', ( assert ) => {
	let streamConfig = metricsClient.getStreamConfig( 'metrics.platform.test' ) || {};
	streamConfig.schema_title = 'fake/title';

	streamConfig = metricsClient.getStreamConfig( 'metrics.platform.test' ) || {};

	assert.strictEqual( streamConfig.schema_title, 'metrics/platform/test' );
} );

QUnit.test( 'addRequiredMetadata() - modern event', ( assert ) => {
	const hasOwnProperty = Object.prototype.hasOwnProperty;

	modernEvent = metricsClient.addRequiredMetadata( modernEvent );
	assert.true( hasOwnProperty.call( modernEvent, 'dt' ), 'dt should be set' );
} );

QUnit.test( 'addRequiredMetadata() - legacy event', ( assert ) => {
	const hasOwnProperty = Object.prototype.hasOwnProperty;

	legacyEvent = metricsClient.addRequiredMetadata( legacyEvent );
	assert.true( hasOwnProperty.call( legacyEvent, 'client_dt' ), 'client_dt should be set' );
	assert.false( hasOwnProperty.call( legacyEvent, 'dt' ), 'dt should not be set' );
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
	const metricsClient = new MetricsClient( integration, false, eventTransport );

	assert.deepEqual( metricsClient.getStreamNamesForEvent( 'foo' ), [] );
	assert.strictEqual(
		metricsClient.eventNameToStreamNamesMap,
		null,
		'No memoized map of event name to stream names is generated'
	);
} );

QUnit.test( 'dispatch() - produce events correctly', ( assert ) => {
	metricsClient.dispatch( 'widgetClick' );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( transportEventStub.callCount, 2, 'transportEvent() should be called' );

	const event1 = transportEventStub.args[ 0 ][ 0 ];
	const event2 = transportEventStub.args[ 1 ][ 0 ];

	// Test that the first event was constructed and produced correctly
	assert.strictEqual( event1.$schema, MetricsClient.SCHEMA );
	assert.deepEqual( event1.meta, {
		stream: 'metrics.platform.test2',
		domain: integration.getHostname()
	} );

	assert.strictEqual( event1.name, 'widgetClick' );

	// Test that the second event was constructed and produced correctly
	assert.strictEqual( event2.$schema, MetricsClient.SCHEMA );
	assert.deepEqual( event2.meta, {
		stream: 'metrics.platform.test3',
		domain: integration.getHostname()
	} );
	assert.strictEqual( event2.name, 'widgetClick' );

	assert.strictEqual(
		event1.dt,
		event2.dt,
		'All events should appear to be produced at the same time'
	);
} );

QUnit.test( 'dispatch() - constructs the bespoke_data property', ( assert ) => {
	metricsClient.dispatch( 'otherWidgetClick', {
		widget_id: 1234,
		widget_color: 'blue',
		widget_enabled: true,
		widget_extra_attribute: null
	} );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( transportEventStub.callCount, 1, 'transportEvent() should be called' );

	const customData = transportEventStub.args[ 0 ][ 0 ].custom_data;

	assert.deepEqual(
		customData,
		{
			widget_id: {
				data_type: 'number',
				value: '1234'
			},
			widget_color: {
				data_type: 'string',
				value: 'blue'
			},
			widget_enabled: {
				data_type: 'boolean',
				value: 'true'
			},
			widget_extra_attribute: {
				data_type: 'null',
				value: 'null'
			}
		}
	);
} );

QUnit.test( 'dispatch() - warn/do not produce event when bespokeData properties are not snake_case', ( assert ) => {
	metricsClient.dispatch( 'otherWidgetClick', {
		widgetId: 1234
	} );

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( transportEventStub.callCount, 0, 'transportEvent() should not be called' );
} );

QUnit.test( 'dispatch() - warn/do not produce event when streamConfigs is false', ( assert ) => {
	// eslint-disable-next-line no-shadow
	const metricsClient = new MetricsClient( integration, false, eventTransport );

	metricsClient.dispatch( 'otherWidgetClick' );

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( transportEventStub.callCount, 0, 'transportEvent() should not be called' );
} );

QUnit.test( 'submitInteraction() - warn/do not produce for interactionData without action', ( assert ) => {
	metricsClient.submitInteraction(
		'metrics.platform.test6',
		'/fragment/analytics/product_metrics/interaction/common/1.0.0'
	);

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( transportEventStub.callCount, 0, 'transportEvent() should not be called' );
} );

QUnit.test( 'submitInteraction() - produce event correctly', ( assert ) => {
	metricsClient.submitInteraction(
		'metrics.platform.test6',
		'/analytics/product_metrics/web/base/1.0.0',
		'foo'
	);

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( transportEventStub.callCount, 1, 'transportEvent() should be called' );

	const event = transportEventStub.args[ 0 ][ 0 ];

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

	const event = transportEventStub.args[ 0 ][ 0 ];

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
	assert.strictEqual( transportEventStub.callCount, 1, 'transportEvent() should be called' );

	const event = transportEventStub.args[ 0 ][ 0 ];

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
	assert.strictEqual( transportEventStub.callCount, 1, 'transportEvent() should be called' );

	const event = transportEventStub.args[ 0 ][ 0 ];

	assert.deepEqual( event.experiments.assigned, {
		'growth-experiments': 'community-updates-module'
	} );
	assert.deepEqual( event.experiments.enrolled, [ 'growth-experiments' ] );
} );
