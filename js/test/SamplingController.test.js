var TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' ),
	SamplingController = require( '../src/SamplingController.js' ),

	integration = new TestMetricsClientIntegration(),
	samplingController = new SamplingController( integration );

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

	[
		// @ts-ignore TS2339
		[ conf.nonExistentStream, false ],
		[ conf.emptyConfig, true ],
		[ conf.nonemptyConfigNoSample, true ],
		[ conf.zeroRateValidUnit, false ],
		[ conf.validRateInvalidUnit, false ],
		[ conf.validRateMissingUnit, false ],
		[ conf.tooHighRateValidUnit, false ],
		[ conf.tooHighRateInvalidUnit, false ],
		[ conf.tooHighRateMissingUnit, false ],
		[ conf.tooLowRateValidUnit, false ],
		[ conf.tooLowRateInvalidUnit, false ],
		[ conf.tooLowRateMissingUnit, false ],
		[ conf.missingRateValidUnit, false ],
		[ conf.missingRateInvalidUnit, false ],
		[ conf.missingRateMissingUnit, false ]
	].forEach( function ( value ) {
		assert.strictEqual( samplingController.streamInSample( value[ 0 ] ), value[ 1 ] );
	} );
} );

QUnit.test( 'streamInSample() - session sampling is deterministic', function ( assert ) {
	/** @type StreamConfig */
	var conf = {
		sample: {
			rate: 0.5,
			unit: 'session'
		}
	};

	var x0 = samplingController.streamInSample( conf );

	for ( var i = 0; i < 5; i++ ) {
		assert.strictEqual( x0, samplingController.streamInSample( conf ) );
	}
} );

QUnit.test( 'streamInSample() - pageview sampling is deterministic', function ( assert ) {
	/** @type StreamConfig */
	var conf = {
		sample: {
			rate: 0.5,
			unit: 'pageview'
		}
	};

	var x0 = samplingController.streamInSample( conf );

	for ( var i = 0; i < 5; i++ ) {
		assert.strictEqual( x0, samplingController.streamInSample( conf ) );
	}
} );

QUnit.test( 'probability in sample is a number in [0,1]', function ( assert ) {
	var UINT32_MAX = 4294967295, // (2^32) - 1
		id = integration.generateRandomId(),
		probabilityInSample = parseInt( id.slice( 0, 8 ), 16 ) / UINT32_MAX;

	assert.strictEqual( probabilityInSample >= 0 && probabilityInSample <= 1, true );
} );
