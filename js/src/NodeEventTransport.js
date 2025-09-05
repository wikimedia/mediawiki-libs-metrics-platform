const DEFAULT_EVENT_INTAKE_URL = 'https://intake-analytics.wikimedia.org/v1/events?hasty=true';

/**
 * A simple event transport for use in server-side environments or environments requiring
 * simplicity.
 *
 * This event transport submits the event to the event intake service immediately. The request is
 * made using the [Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API).
 *
 * @param {string} [eventIntakeUrl] The URL of the EventGate event intake service to send events
 *  to. `https://intake-analytics.wikimedia.org/v1/events?hasty=true` by default
 *
 * @constructor
 * @implements {MetricsPlatform.EventTransport}
 * @memberof MetricsPlatform
 */
function NodeEventTransport( eventIntakeUrl ) {
	this.eventIntakeUrl = eventIntakeUrl || DEFAULT_EVENT_INTAKE_URL;
}

/**
 * Submits to the event intake service or enqueues the event for submission to the event
 * intake service.
 *
 * @param {EventPlatform.EventData} eventData
 */
NodeEventTransport.prototype.transportEvent = function ( eventData ) {
	fetch( this.eventIntakeUrl, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify( eventData )
	} );

	// eslint-disable-next-line no-console
	console.info( 'Submitted the following event:', eventData );
};

module.exports = NodeEventTransport;
