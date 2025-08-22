/**
 * TODO: Convert CallQueueEntry to an object so that we can use stricter typing.
 *
 * Previously, CallQueueEntry was defined as:
 *
 * ```ts
 * type CallQueueEntry =
 *     ["submit", string, string, BaseEventData]
 *     | ["dispatch", string, string, FormattedCustomData]
 *     ;
 * ```
 *
 * @typedef {Array} MetricsPlatform.ExternalMetricsClient.CallQueueEntry
 * @typedef {MetricsPlatform.ExternalMetricsClient.CallQueueEntry[]} MetricsPlatform.ExternalMetricsClient.CallQueue
 */

//

const MetricsClient = require( './MetricsClient.js' );

const STATE_FETCHING_STREAM_CONFIGS = 1;
const STATE_FETCHED_STREAM_CONFIGS = 2;

const CALL_QUEUE_MAX_LENGTH = 128;

/**
 * @param {MetricsPlatform.Integration} integration
 * @param {MetricsPlatform.EventTransport} [eventTransport]
 * @constructor
 * @extends {MetricsPlatform.MetricsClient}
 * @memberof MetricsPlatform
 */
function ExternalMetricsClient( integration, eventTransport ) {
	MetricsClient.call( this, integration, {}, eventTransport );
	this.super = MetricsClient;

	this.state = STATE_FETCHING_STREAM_CONFIGS;
	this.fetchStreamConfigs();

	/** @type {MetricsPlatform.ExternalMetricsClient.CallQueue} */
	this.callQueue = [];
}

ExternalMetricsClient.prototype = Object.create( MetricsClient.prototype );
ExternalMetricsClient.prototype.constructor = ExternalMetricsClient;

/**
 * Fetch and update the stream configs, and then process the call queue, submitting and dispatching
 * events as necessary.
 *
 * @private
 */
ExternalMetricsClient.prototype.fetchStreamConfigs = function () {
	const that = this;

	that.integration.fetchStreamConfigs()
		.then( ( /** @type {StreamConfigs} */ streamConfigs ) => {
			that.streamConfigs = streamConfigs;
			that.eventNameToStreamNamesMap = null;
			that.state = STATE_FETCHED_STREAM_CONFIGS;

			that.processCallQueue();
		} );
};

/**
 * Enqueues a call to be processed.
 *
 * The call queue has a maximum capacity of 128 calls. If it is full, then the earliest call is
 * dropped and a warning is logged.
 *
 * @param {MetricsPlatform.ExternalMetricsClient.CallQueueEntry} call
 * @private
 */
ExternalMetricsClient.prototype.queueCall = function ( call ) {
	this.callQueue.push( call );

	if ( this.callQueue.length > CALL_QUEUE_MAX_LENGTH ) {
		const droppedCall = this.callQueue.shift();

		if ( droppedCall ) {
			const callString = [
				droppedCall[ 0 ],
				'( ',
				droppedCall[ 2 ],
				', ',
				droppedCall[ 0 ] === 'submit' ? 'eventData' : 'customData',
				' )'
			].join( '' );

			this.integration.logWarning( 'Call to ' + callString + ' dropped because the queue is full.' );
		}
	}

	this.processCallQueue();
};

/**
 * Processes calls to {@link MetricsClient.prototype.submit} and
 * {@link MetricsClient.prototype.dispatch} if the stream configs have been fetched and updated.
 *
 * @private
 */
ExternalMetricsClient.prototype.processCallQueue = function () {
	if ( this.state === STATE_FETCHING_STREAM_CONFIGS ) {
		return;
	}

	while ( this.callQueue.length ) {
		const call = this.callQueue.shift();

		if ( !call ) {
			// NOTE: This should never happen.
			continue;
		}

		if ( call[ 0 ] === 'submit' ) {
			this.processSubmitCall( call[ 1 ], call[ 2 ], call[ 3 ] );
		} else if ( call[ 0 ] === 'dispatch' ) {
			this.processDispatchCall( call[ 1 ], call[ 2 ], call[ 3 ] );
		}
	}
};

/**
 * NOTE: Calls to `submit()` are processed after the stream configs are fetched and updated. If
 * the Metrics Platform Client was initialized with stream configs, then calls to `submit()` are
 * processed immediately.
 *
 * @override
 */
ExternalMetricsClient.prototype.submit = function ( streamName, eventData ) {
	const result = this.validateSubmitCall( streamName, eventData );

	if ( result ) {
		this.queueCall( [
			'submit',
			new Date().toISOString(),
			streamName,
			eventData
		] );
	}
};

/**
 * NOTE: Calls to `dispatch()` are processed after the stream configs are fetched and updated. If
 * the Metrics Platform Client was initialized with stream configs, then calls to `dispatch()` are
 * processed immediately.
 *
 * @override
 */
ExternalMetricsClient.prototype.dispatch = function ( eventName, customData ) {
	const result = this.validateDispatchCall( eventName, customData );

	if ( result ) {
		this.queueCall( [
			'dispatch',
			new Date().toISOString(),
			eventName,
			result
		] );
	}
};

module.exports = ExternalMetricsClient;
