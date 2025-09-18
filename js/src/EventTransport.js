// Types
// =====

/**
 * @interface EventTransport
 * @memberof MetricsPlatform
 */

/**
 * Transports the event to the event intake service or enqueues the event for transport to the
 * event intake service.
 *
 * @method
 * @name MetricsPlatform.EventTransport#transportEvent
 * @param {EventPlatform.EventData} event
 */

// Constants
// =========

const DEFAULT_EVENT_INTAKE_URL = 'https://intake-analytics.wikimedia.org/v1/events?hasty=true';
const DELAYED_SUBMIT_TIMEOUT = 5; // (s)

// API
// ===

/**
 * The default event transport used by {@link MetricsClient}.
 *
 * This event transport maintains an unbounded internal queue of events, which is drained every
 * 5 seconds or when the page is hidden. When the queue is drained, all events in the queue are
 * transported to the event intake service in one request. The request is made using the
 * [Navigator: sendBeacon() method][0]. That is, the request is made asynchronously in the
 * background by the browser with no indication whether it succeeded.
 *
 * This event transport is expected to be used in a browser. As well as the
 * [Navigator: sendBeacon() method][0], the event transport requires the browser to support for the
 * [Page Visbility API][1].
 *
 * [0]: https://developer.mozilla.org/en-US/docs/Web/API/Navigator/sendBeacon
 * [1]: https://developer.mozilla.org/en-US/docs/Web/API/Page_Visibility_API
 *
 * @param {string} [eventIntakeUrl] The URL of the EventGate event intake service to send events
 *  to. `https://intake-analytics.wikimedia.org/v1/events?hasty=true` by default
 * @constructor
 * @implements {MetricsPlatform.EventTransport}
 * @memberof MetricsPlatform
 */
function DefaultEventTransport( eventIntakeUrl ) {
	this.eventIntakeUrl = eventIntakeUrl || DEFAULT_EVENT_INTAKE_URL;

	/** @type {EventPlatform.EventData[]} */
	this.events = [];

	const eventTransport = this;

	this.isDocumentUnloading = false;

	window.addEventListener( 'pagehide', () => {
		eventTransport.isDocumentUnloading = true;
	} );

	window.addEventListener( 'pageshow', () => {
		eventTransport.isDocumentUnloading = false;
	} );

	document.addEventListener( 'visibilitychange', () => {
		if ( document.hidden ) {
			eventTransport.doTransportEvents();
		}
	} );

	this.delayedSubmitTimeoutID = null;
}

/**
 * Submits to the event intake service or enqueues the event for submission to the event
 * intake service.
 *
 * @param {EventPlatform.EventData} eventData
 */
DefaultEventTransport.prototype.transportEvent = function ( eventData ) {
	this.events.push( eventData );

	if ( this.isDocumentUnloading ) {
		this.doTransportEvents();
	} else {
		this.doDelayedTransportEvents();
	}

	this.onTransportEvent( eventData );
};

/**
 * Submits all queued events to the event intake service immediately and clears the queue.
 *
 * @ignore
 */
DefaultEventTransport.prototype.doTransportEvents = function () {
	if ( this.events.length ) {
		try {
			navigator.sendBeacon(
				this.eventIntakeUrl,
				JSON.stringify( this.events )
			);
		} catch ( e ) {
			// Some browsers throw when sending a beacon to a blocked URL (by an adblocker, for
			// example). Some browser extensions remove Navigator#sendBeacon() altogether. See also:
			//
			// 1. https://phabricator.wikimedia.org/T86680
			// 2. https://phabricator.wikimedia.org/T273374
			// 3. https://phabricator.wikimedia.org/T308311
			//
			// Regardless, ignore all errors for now.
			//
			// TODO (phuedx, 2024/09/09): Instrument this!
		}
	}

	this.events = [];
	this.delayedSubmitTimeoutID = null;
};

/**
 * Schedules a call to {@link DefaultEventTransport#doTransportEvents} in 5 seconds, if a call is
 * not already scheduled.
 *
 * @ignore
 */
DefaultEventTransport.prototype.doDelayedTransportEvents = function () {
	if ( this.delayedSubmitTimeoutID ) {
		return;
	}

	const eventTranpsport = this;

	this.delayedSubmitTimeoutID = setTimeout(
		() => {
			eventTranpsport.doTransportEvents();
		},
		DELAYED_SUBMIT_TIMEOUT * 1000
	);
};

/**
 * Called when an event is enqueued for submission to the event intake service.
 *
 * @param {EventPlatform.EventData} eventData
 */
DefaultEventTransport.prototype.onTransportEvent = function ( eventData ) {
	// eslint-disable-next-line no-console
	console.info( 'Submitted the following event:', eventData );
};

module.exports = DefaultEventTransport;
