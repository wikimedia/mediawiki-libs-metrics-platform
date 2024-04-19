const DEFAULT_EVENTGATE_ORIGIN = 'https://intake-analytics.wikimedia.org';

/**
 * @param {string} origin
 * @return {string}
 */
function getEventGateUrl( origin ) {
	const result = new URL( origin );

	result.pathname = '/v1/events';
	result.searchParams.set( 'hasty', 'true' );

	return result.toString();
}

/**
 * @param {string} [eventGateOrigin] The origin of the EventGate event intake service to send
 *  events to. `https://intake-analytics.wikimedia.org` by default
 * @constructor
 */
function DefaultEventSubmitter( eventGateOrigin ) {
	this.eventGateUrl = getEventGateUrl( eventGateOrigin || DEFAULT_EVENTGATE_ORIGIN );
}

/**
 * Submits to the event ingestion service or enqueues the event for submission to the event
 * ingestion service.
 *
 * @param {EventData} eventData
 */
DefaultEventSubmitter.prototype.submitEvent = function ( eventData ) {
	navigator.sendBeacon(
		this.eventGateUrl,
		JSON.stringify( eventData )
	);

	this.onSubmitEvent( eventData );
};

/**
 * Called when an event is enqueued for submission to the event ingestion service.
 *
 * @param {EventData} eventData
 */
DefaultEventSubmitter.prototype.onSubmitEvent = function ( eventData ) {
	// eslint-disable-next-line no-console
	console.info( 'Submitted the following event:', eventData );
};

module.exports = DefaultEventSubmitter;
