/* eslint-disable max-len */
( function () {

	var TestIntegration = require( './TestIntegration.js' ),
		AssociationController = require( '../src/AssociationController.js' ),
		SamplingController = require( '../src/SamplingController.js' ),

		associationController = new AssociationController( new TestIntegration() ),
		samplingController = new SamplingController( associationController );

	QUnit.module( 'SamplingController' );

	QUnit.test( 'streamInSample() - valid and invalid stream configs', function ( assert ) {
		var conf = {
			emptyConfig: {},
			nonemptyConfigNoSample: {
				some: 'value'
			},
			zeroRateValidUnit: {
				sample: {
					rate: 0.0,
					unit: 'session'
				}
			},
			validRateInvalidUnit: {
				sample: {
					rate: 0.5,
					unit: 'coelacanth'
				}
			},
			validRateMissingUnit: {
				sample: {
					rate: 0.5
				}
			},
			tooHighRateValidUnit: {
				sample: {
					rate: 5,
					unit: 'session'
				}
			},
			tooHighRateInvalidUnit: {
				sample: {
					rate: 5,
					unit: 'coelacanth'
				}
			},
			tooHighRateMissingUnit: {
				sample: {
					rate: 5
				}
			},
			tooLowRateValidUnit: {
				sample: {
					rate: -5,
					unit: 'session'
				}
			},
			tooLowRateInvalidUnit: {
				sample: {
					rate: -5,
					unit: 'coelacanth'
				}
			},
			tooLowRateMissingUnit: {
				sample: {
					rate: -5
				}
			},
			missingRateValidUnit: {
				sample: {
					unit: 'session'
				}
			},
			missingRateInvalidUnit: {
				sample: {
					unit: 'coelacanth'
				}
			},
			missingRateMissingUnit: {
				sample: {}
			}
		};

		assert.strictEqual( samplingController.streamInSample( conf.nonExistentStream ), false );
		assert.strictEqual( samplingController.streamInSample( conf.emptyConfig ), true );
		assert.strictEqual( samplingController.streamInSample( conf.nonemptyConfigNoSample ), true );
		assert.strictEqual( samplingController.streamInSample( conf.zeroRateValidUnit ), false );

		assert.strictEqual( samplingController.streamInSample( conf.validRateInvalidUnit ), false );
		assert.strictEqual( samplingController.streamInSample( conf.validRateMissingUnit ), false );

		assert.strictEqual( samplingController.streamInSample( conf.tooHighRateValidUnit ), false );
		assert.strictEqual( samplingController.streamInSample( conf.tooHighRateInvalidUnit ), false );
		assert.strictEqual( samplingController.streamInSample( conf.tooHighRateMissingUnit ), false );

		assert.strictEqual( samplingController.streamInSample( conf.tooLowRateValidUnit ), false );
		assert.strictEqual( samplingController.streamInSample( conf.tooLowRateInvalidUnit ), false );
		assert.strictEqual( samplingController.streamInSample( conf.tooLowRateMissingUnit ), false );

		assert.strictEqual( samplingController.streamInSample( conf.missingRateValidUnit ), false );
		assert.strictEqual( samplingController.streamInSample( conf.missingRateInvalidUnit ), false );
		assert.strictEqual( samplingController.streamInSample( conf.missingRateMissingUnit ), false );
	} );

	QUnit.test( 'streamInSample() - session sampling is deterministic', function ( assert ) {
		var conf, x0, i;

		conf = {
			sample: {
				rate: 0.5,
				unit: 'session'
			}
		};

		x0 = samplingController.streamInSample( conf );

		for ( i = 0; i < 5; i++ ) {
			assert.strictEqual( x0, samplingController.streamInSample( conf ) );
		}
	} );

	QUnit.test( 'streamInSample() - pageview sampling is deterministic', function ( assert ) {
		var conf, x0, i;

		conf = {
			sample: {
				rate: 0.5,
				unit: 'pageview'
			}
		};

		x0 = samplingController.streamInSample( conf );

		for ( i = 0; i < 5; i++ ) {
			assert.strictEqual( x0, samplingController.streamInSample( conf ) );
		}
	} );

	QUnit.test( 'probability in sample is a number in [0,1]', function ( assert ) {
		var UINT32_MAX = 4294967295, // (2^32) - 1
			id = associationController.generateId(),
			probabilityInSample = parseInt( id.slice( 0, 8 ), 16 ) / UINT32_MAX;
		assert.ok( probabilityInSample >= 0 && probabilityInSample <= 1 );
	} );

}() );
