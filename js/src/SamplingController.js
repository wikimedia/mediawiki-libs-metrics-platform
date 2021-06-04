/**
 * Evaluates events for presence in sample based on the stream configuration.
 */
( function () {

	var UINT32_MAX = 4294967295; // (2^32) - 1

	/**
	 * @param {!Object} associationController association controller
	 * @constructor
	 */
	function SamplingController( associationController ) {
		this.associationController = associationController;
	}

	/**
	 * Determine whether a stream is in or out of sample.
	 *
	 * @param {?Object} streamConfig stream configuration
	 * @return {!boolean} true if in-sample, false if out-sample.
	 */
	SamplingController.prototype.streamInSample = function ( streamConfig ) {
		var id;

		if ( !streamConfig ) {
			// If a stream is not defined, it is not in sample.
			return false;
		}

		if ( !streamConfig.sample ) {
			// If the stream does not specify sampling, it is in-sample.
			return true;
		}

		if (
			( !streamConfig.sample.rate || !streamConfig.sample.unit ) ||
			( streamConfig.sample.rate < 0 || streamConfig.sample.rate > 1 )
		) {
			// If the stream does specify sampling, but it is malformed,
			// it is not in-sample.
			return false;
		}

		switch ( streamConfig.sample.unit ) {
			case 'pageview':
				id = this.associationController.getPageviewId();
				break;
			case 'session':
				id = this.associationController.getSessionId();
				break;
			default:
				return false;
		}

		return parseInt( id.slice( 0, 8 ), 16 ) / UINT32_MAX < streamConfig.sample.rate;
	};

	module.exports = SamplingController;

}() );
