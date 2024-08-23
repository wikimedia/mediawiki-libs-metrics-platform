const getEventGateUrl = require( './DefaultEventSubmitter.js' ).getEventGateUrl;

/**
 * A simple event submitter for use in server-side environments or environments requiring
 * simplicity.
 *
 * This event submitter submits the event to the event intake service immediately. The request is
 * made using the [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API).
 *
 * @param {string} [eventGateOrigin] The origin of the EventGate event intake service to send
 *  events to. `https://intake-analytics.wikimedia.org` by default
 * @constructor
 */
function NodeEventSubmitter( eventGateOrigin ) {
	this.eventGateUrl = getEventGateUrl( eventGateOrigin );
}

/**
 * Submits to the event intake service or enqueues the event for submission to the event
 * intake service.
 *
 * @param {EventData} eventData
 */
NodeEventSubmitter.prototype.submitEvent = function ( eventData ) {
	fetch( this.eventGateUrl, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify( eventData )
	} );

	this.onSubmitEvent( eventData );
};

/**
 * Called when an event is enqueued for submission to the event intake service.
 *
 * @param {EventData} eventData
 */
NodeEventSubmitter.prototype.onSubmitEvent = function ( eventData ) {
	// eslint-disable-next-line no-console
	console.info( 'Submitted the following event:', eventData );
};

module.exports = NodeEventSubmitter;
