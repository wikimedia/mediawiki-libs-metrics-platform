/* eslint-disable camelcase */
var sinon = require( 'sinon' );

var TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
var MetricsClient = require( './../../src/MetricsClient.js' );

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
	},
	'metrics.platform.test6': {
		schema_title: '/analytics/metrics_platform/interaction/common'
	}
};

/** @type EventData */
var modernEvent = {
	$schema: '/test/event/1.0.0',
	volcano: 'Nyiragongo',
	Explosivity: 1
};

/** @type EventData */
var legacyEvent = {
	$schema: '/test/event/1.0.0',
	volcano: 'Nyiragongo',
	Explosivity: 1,
	client_dt: '2021-05-12T00:00:00.000Z',
	dt: '2021-05-12T00:00:00.000Z'
};

var integration = new TestMetricsClientIntegration();
var metricsClient = new MetricsClient( integration, streamConfigs );

var sandbox = sinon.createSandbox();
var enqueueEventStub = sandbox.stub( integration, 'enqueueEvent' );
var logWarningStub = sandbox.stub( integration, 'logWarning' );
var onSubmitStub = sandbox.stub( integration, 'onSubmit' );

QUnit.module( 'MetricsClient', {
	beforeEach: function () {
		sandbox.reset();
	}
} );

QUnit.test( 'submit() - warn/do not produce for event without $schema', function ( assert ) {
	// @ts-ignore TS2345
	metricsClient.submit( 'metrics.platform.test', {} );

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( enqueueEventStub.callCount, 0, 'enqueueEvent() should not be called' );
	assert.strictEqual( onSubmitStub.callCount, 0, 'onSubmit() should not be called' );
} );

QUnit.test( 'submit() - produce an event correctly', function ( assert ) {
	metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( enqueueEventStub.callCount, 1, 'enqueueEvent() should be called' );
	assert.strictEqual( onSubmitStub.callCount, 1, 'onSubmit() should be called' );
} );

QUnit.test( 'streamConfig() - disallow modification', function ( assert ) {
	var streamConfig = metricsClient.getStreamConfig( 'metrics.platform.test' ) || {};
	streamConfig.schema_title = 'fake/title';

	streamConfig = metricsClient.getStreamConfig( 'metrics.platform.test' ) || {};

	assert.strictEqual( streamConfig.schema_title, 'metrics/platform/test' );
} );

QUnit.test( 'addRequiredMetadata() - modern event', function ( assert ) {
	var hasOwnProperty = Object.prototype.hasOwnProperty;

	modernEvent = metricsClient.addRequiredMetadata( modernEvent, 'metrics.platform.test' );
	assert.true( hasOwnProperty.call( modernEvent, 'dt' ), 'dt should be set' );
	assert.deepEqual( modernEvent.meta, {
		stream: 'metrics.platform.test',
		domain: 'test.example.com'
	} );
} );

QUnit.test( 'addRequiredMetadata() - legacy event', function ( assert ) {
	var hasOwnProperty = Object.prototype.hasOwnProperty;

	legacyEvent = metricsClient.addRequiredMetadata( legacyEvent, 'metrics.platform.test' );
	assert.true( hasOwnProperty.call( legacyEvent, 'client_dt' ), 'client_dt should be set' );
	assert.false( hasOwnProperty.call( legacyEvent, 'dt' ), 'dt should not be set' );
	assert.deepEqual( legacyEvent.meta, {
		stream: 'metrics.platform.test',
		domain: 'test.example.com'
	} );
} );

QUnit.test( 'getStreamNamesForEvent() ', function ( assert ) {
	/** @type {[string, string[], string][]} */
	var cases = [
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
	cases.forEach( function ( [ eventName, expected, message ] ) {
		assert.deepEqual(
			metricsClient.getStreamNamesForEvent( eventName ),
			expected,
			message
		);
	} );
} );

QUnit.test( 'getStreamNamesForEvent() - prefix matching', function ( assert ) {
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

QUnit.test( 'getStreamNamesForEvent() - streamConfigs is falsy', function ( assert ) {
	// eslint-disable-next-line no-shadow
	var metricsClient = new MetricsClient( integration, false );

	assert.deepEqual( metricsClient.getStreamNamesForEvent( 'foo' ), [] );
	assert.strictEqual(
		metricsClient.eventNameToStreamNamesMap,
		null,
		'No memoized map of event name to stream names is generated'
	);
} );

QUnit.test( 'dispatch() - produce events correctly', function ( assert ) {
	metricsClient.dispatch( 'widgetClick' );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( enqueueEventStub.callCount, 2, 'enqueueEvent() should be called' );

	var event1 = enqueueEventStub.args[ 0 ][ 0 ];
	var event2 = enqueueEventStub.args[ 1 ][ 0 ];

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

QUnit.test( 'dispatch() - constructs the bespoke_data property', function ( assert ) {
	metricsClient.dispatch( 'otherWidgetClick', {
		widget_id: 1234,
		widget_color: 'blue',
		widget_enabled: true,
		widget_extra_attribute: null
	} );

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( enqueueEventStub.callCount, 1, 'enqueueEvent() should be called' );

	var customData = enqueueEventStub.args[ 0 ][ 0 ].custom_data;

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

QUnit.test( 'dispatch() - warn/do not produce event when bespokeData properties are not snake_case', function ( assert ) {
	metricsClient.dispatch( 'otherWidgetClick', {
		widgetId: 1234
	} );

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( enqueueEventStub.callCount, 0, 'enqueueEvent() should not be called' );
} );

QUnit.test( 'dispatch() - warn/do not produce event when streamConfigs is false', function ( assert ) {
	// eslint-disable-next-line no-shadow
	var metricsClient = new MetricsClient( integration, false );

	metricsClient.dispatch( 'otherWidgetClick' );

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( enqueueEventStub.callCount, 0, 'enqueueEvent() should not be called' );
} );

QUnit.test( 'submitInteraction() - warn/do not produce for interactionData without action', function ( assert ) {
	// @ts-ignore TS2345
	metricsClient.submitInteraction(
		'metrics.platform.test6',
		'/fragment/analytics/metrics_platform/interaction/common/1.0.0'
	);

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( enqueueEventStub.callCount, 0, 'enqueueEvent() should not be called' );
	assert.strictEqual( onSubmitStub.callCount, 0, 'onSubmit() should not be called' );
} );

QUnit.test( 'submitInteraction() - produce event correctly', function ( assert ) {
	metricsClient.submitInteraction(
		'metrics.platform.test6',
		'/analytics/metrics_platform/web/base/1.0.0',
		'foo'
	);

	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( enqueueEventStub.callCount, 1, 'enqueueEvent() should be called' );
	assert.strictEqual( onSubmitStub.callCount, 1, 'onSubmit() should be called' );

	var event = enqueueEventStub.args[ 0 ][ 0 ];

	// @ts-ignore TS2345
	assert.strictEqual( event.meta.stream, 'metrics.platform.test6' );
	assert.strictEqual( event.$schema, '/analytics/metrics_platform/web/base/1.0.0' );
	assert.strictEqual( event.action, 'foo' );
} );

QUnit.test( 'submitInteraction() - disallow $schema overriding', function ( assert ) {
	metricsClient.submitInteraction(
		'metrics.platform.test6',
		'/analytics/metrics_platform/web/base/1.0.0',
		'foo',
		{
			// @ts-ignore TS2345
			$schema: '/bar/baz/1.0.0'
		}
	);

	var event = enqueueEventStub.args[ 0 ][ 0 ];

	assert.strictEqual( event.$schema, '/analytics/metrics_platform/web/base/1.0.0' );
} );
