'use strict';

const TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' ),
	SamplingController = require( './../../src/SamplingController.js' ),

	integration = new TestMetricsClientIntegration(),
	samplingController = new SamplingController( integration );

QUnit.module( 'SamplingController' );

QUnit.test( 'isStreamInSample() - valid and invalid stream configs', ( assert ) => {
	const conf = {
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
	].forEach( ( value ) => {
		assert.strictEqual( samplingController.isStreamInSample( value[ 0 ] ), value[ 1 ] );
	} );
} );

QUnit.test( 'isStreamInSample() - session sampling is deterministic', ( assert ) => {
	/** @type StreamConfig */
	const conf = {
		sample: {
			rate: 0.5,
			unit: 'session'
		}
	};

	const x0 = samplingController.isStreamInSample( conf );

	for ( let i = 0; i < 5; i++ ) {
		assert.strictEqual( x0, samplingController.isStreamInSample( conf ) );
	}
} );

QUnit.test( 'isStreamInSample() - pageview sampling is deterministic', ( assert ) => {
	/** @type StreamConfig */
	const conf = {
		sample: {
			rate: 0.5,
			unit: 'pageview'
		}
	};

	const x0 = samplingController.isStreamInSample( conf );

	for ( let i = 0; i < 5; i++ ) {
		assert.strictEqual( x0, samplingController.isStreamInSample( conf ) );
	}
} );

QUnit.test( 'probability in sample is a number in [0,1]', ( assert ) => {
	const UINT32_MAX = 4294967295, // (2^32) - 1
		id = integration.generateRandomId(),
		probabilityInSample = parseInt( id.slice( 0, 8 ), 16 ) / UINT32_MAX;

	assert.strictEqual( probabilityInSample >= 0 && probabilityInSample <= 1, true );
} );

QUnit.test( 'a pageview/session/device that is in-sample at 1% is also in-sample at any greater rate', ( assert ) => {
	// A pageviewId value that is in-sample at 1% as a starting point
	integration.pageviewId = '00000000000000000ddd';

	const pageviewConf = {
		empty: {},
		sample1: {
			sample: {
				rate: 0.01,
				unit: 'pageview'
			}
		},
		sample5: {
			sample: {
				rate: 0.05,
				unit: 'pageview'
			}
		},
		sample10: {
			sample: {
				rate: 0.1,
				unit: 'pageview'
			}
		},
		sample25: {
			sample: {
				rate: 0.25,
				unit: 'pageview'
			}
		},
		sample50: {
			sample: {
				rate: 0.5,
				unit: 'pageview'
			}
		},
		sample75: {
			sample: {
				rate: 0.75,
				unit: 'pageview'
			}
		},
		sample100: {
			sample: {
				rate: 1,
				unit: 'pageview'
			}
		}
	};

	[
		// @ts-ignore TS2339
		[ pageviewConf.empty, true ],
		[ pageviewConf.sample1, true ],
		[ pageviewConf.sample5, true ],
		[ pageviewConf.sample10, true ],
		[ pageviewConf.sample25, true ],
		[ pageviewConf.sample50, true ],
		[ pageviewConf.sample75, true ],
		[ pageviewConf.sample100, true ]
	].forEach( ( value ) => {
		assert.strictEqual( samplingController.isStreamInSample( value[ 0 ] ), value[ 1 ] );
	} );

	// A sessionId value that is in-sample at 1% as a starting point
	integration.sessionId = '00000000000000000ddd';

	const sessionConf = {
		empty: {},
		sample1: {
			sample: {
				rate: 0.01,
				unit: 'session'
			}
		},
		sample5: {
			sample: {
				rate: 0.05,
				unit: 'session'
			}
		},
		sample10: {
			sample: {
				rate: 0.1,
				unit: 'session'
			}
		},
		sample25: {
			sample: {
				rate: 0.25,
				unit: 'session'
			}
		},
		sample50: {
			sample: {
				rate: 0.5,
				unit: 'session'
			}
		},
		sample75: {
			sample: {
				rate: 0.75,
				unit: 'session'
			}
		},
		sample100: {
			sample: {
				rate: 1,
				unit: 'session'
			}
		}
	};

	[
		// @ts-ignore TS2339
		[ sessionConf.empty, true ],
		[ sessionConf.sample1, true ],
		[ sessionConf.sample5, true ],
		[ sessionConf.sample10, true ],
		[ sessionConf.sample25, true ],
		[ sessionConf.sample50, true ],
		[ sessionConf.sample75, true ],
		[ sessionConf.sample100, true ]
	].forEach( ( value ) => {
		assert.strictEqual( samplingController.isStreamInSample( value[ 0 ] ), value[ 1 ] );
	} );
} );
