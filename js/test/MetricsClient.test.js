/* eslint-disable camelcase */
var sinon = require( 'sinon' ),

	TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' ),
	MetricsClient = require( '../src/MetricsClient.js' ),

	/** @type StreamConfigs */
	streamConfigs = {
		'metrics.platform.test': {
			schema_title: 'metrics/platform/test',
			producers: {
				metrics_platform_client: {
					provide_values: [
						'mediawiki_skin'
					]
				}
			}
		}
	},

	/** @type EventData */
	modernEvent = {
		$schema: '/test/event/1.0.0',
		volcano: 'Nyiragongo',
		Explosivity: 1
	},

	/** @type EventData */
	legacyEvent = {
		$schema: '/test/event/1.0.0',
		volcano: 'Nyiragongo',
		Explosivity: 1,
		client_dt: '2021-05-12T00:00:00.000Z',
		dt: '2021-05-12T00:00:00.000Z'
	},

	integration = new TestMetricsClientIntegration(),
	metricsClient = new MetricsClient( integration, streamConfigs ),

	enqueueEventStub = sinon.stub( integration, 'enqueueEvent' ),
	logWarningStub = sinon.stub( integration, 'logWarning' );

QUnit.module( 'MetricsClient', {
	beforeEach: function () {
		enqueueEventStub.reset();
		logWarningStub.reset();
	}
} );

QUnit.test( 'submit() - warn for event without schema', function ( assert ) {
	// @ts-ignore TS2345
	metricsClient.submit( 'metrics.platform.test', {} );

	assert.strictEqual( logWarningStub.callCount, 1, 'logWarning() should be called' );
	assert.strictEqual( enqueueEventStub.callCount, 0, 'enqueueEvent() should not be called' );
} );

QUnit.test( 'submit() - produce an event correctly', function ( assert ) {
	metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );
	assert.strictEqual( logWarningStub.callCount, 0, 'logWarning() should not be called' );
	assert.strictEqual( enqueueEventStub.callCount, 1, 'enqueueEvent() should be called' );
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
	assert.strictEqual( hasOwnProperty.call( modernEvent, 'dt' ), true, 'dt should be set' );
	assert.deepEqual( modernEvent.meta, {
		stream: 'metrics.platform.test',
		domain: 'test.example.com'
	} );
} );

QUnit.test( 'addRequiredMetadata() - legacy event', function ( assert ) {
	var hasOwnProperty = Object.prototype.hasOwnProperty;

	legacyEvent = metricsClient.addRequiredMetadata( legacyEvent, 'metrics.platform.test' );
	assert.strictEqual( hasOwnProperty.call( legacyEvent, 'client_dt' ), true, 'client_dt should be set' );
	assert.strictEqual( hasOwnProperty.call( legacyEvent, 'dt' ), false, 'dt should not be set' );
	assert.deepEqual( legacyEvent.meta, {
		stream: 'metrics.platform.test',
		domain: 'test.example.com'
	} );
} );
