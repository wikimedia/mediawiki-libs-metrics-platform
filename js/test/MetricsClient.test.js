/* eslint-disable camelcase */
( function () {

	var sinon = require( 'sinon' ),

		TestIntgration = require( './TestIntegration.js' ),
		MetricsClient = require( '../src/MetricsClient.js' ),

		streamConfigs = {
			'metrics.platform.test': {
				schema_title: 'metrics/platform/test',
				provide_values: [
					'skin'
				]
			}
		},

		modernEvent = {
			volcano: 'Nyiragongo',
			Explosivity: 1
		},

		legacyEvent = {
			volcano: 'Nyiragongo',
			Explosivity: 1,
			client_dt: '2021-05-12T00:00:00.000Z',
			dt: '2021-05-12T00:00:00.000Z'
		},

		integration = new TestIntgration(),
		metricsClient = new MetricsClient( integration, streamConfigs ),

		stubs = [
			sinon.stub( integration, 'enqueueEvent' ),
			sinon.stub( integration, 'logWarning' )
		];

	QUnit.module( 'MetricsClient', {
		beforeEach: function () {
			stubs.forEach( function ( stub ) {
				stub.reset();
			} );
		}
	} );

	QUnit.test( 'submit() - warn for event without schema', function ( assert ) {
		metricsClient.submit( 'metrics.platform.test', {} );
		assert.strictEqual( integration.logWarning.callCount, 1, 'logWarning() should be called' );
		assert.strictEqual( integration.enqueueEvent.callCount, 0, 'enqueueEvent() should not be called' );
	} );

	QUnit.test( 'submit() - produce an event correctly', function ( assert ) {
		metricsClient.submit( 'metrics.platform.test', { $schema: 'metrics/platform/test' } );
		assert.strictEqual( integration.logWarning.callCount, 0, 'logWarning() should not be called' );
		assert.strictEqual( integration.enqueueEvent.callCount, 1, 'enqueueEvent() should be called' );
	} );

	QUnit.test( 'streamConfig() - disallow modification', function ( assert ) {
		metricsClient.getStreamConfig( 'metrics.platform.test' ).schema_title = 'fake/title';
		assert.strictEqual( metricsClient.getStreamConfig( 'metrics.platform.test' ).schema_title, 'metrics/platform/test' );
	} );

	QUnit.test( 'addRequiredMetadata() - modern event', function ( assert ) {
		modernEvent = metricsClient.addRequiredMetadata( modernEvent, 'metrics.platform.test' );
		assert.ok( modernEvent.dt, 'dt should be set' );
		assert.strictEqual( modernEvent.meta.stream, 'metrics.platform.test', 'meta.stream should match provided stream name' );
		assert.strictEqual( modernEvent.meta.domain, 'test.example.com', 'meta.domain should match webHost field' );
	} );

	QUnit.test( 'addRequiredMetadata() - legacy event', function ( assert ) {
		legacyEvent = metricsClient.addRequiredMetadata( legacyEvent, 'metrics.platform.test' );
		assert.ok( legacyEvent.client_dt, 'client_dt should be set' );
		assert.notOk( legacyEvent.dt, 'dt should not be set' );
		assert.strictEqual( legacyEvent.meta.stream, 'metrics.platform.test', 'meta.stream should match provided stream name' );
		assert.strictEqual( legacyEvent.meta.domain, 'test.example.com', 'meta.domain should match webHost field' );
	} );

}() );
