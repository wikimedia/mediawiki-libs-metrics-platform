const DefaultEventSubmitter = require( './DefaultEventSubmitter.js' );

const DELAYED_SUBMIT_TIMEOUT = 5; // (s)

/**
 * @param {string} [eventGateOrigin] The origin of the EventGate event intake service to send
 *  events to. `https://intake-analytics.wikimedia.org` by default
 * @constructor
 */
function BrowserEventSubmitter( eventGateOrigin ) {
	DefaultEventSubmitter.call( this, eventGateOrigin );

	/** @type {EventData[]} */
	this.events = [];

	const eventSubmitter = this;

	this.isDocumentUnloading = false;

	window.addEventListener( 'pagehide', function () {
		eventSubmitter.isDocumentUnloading = true;
	} );

	window.addEventListener( 'pageshow', function () {
		eventSubmitter.isDocumentUnloading = false;
	} );

	document.addEventListener( 'visibilitychange', function () {
		if ( document.hidden ) {
			eventSubmitter.doSubmitEvents();
		}
	} );

	/** @type {?ReturnType<typeof setTimeout>} */
	this.delayedSubmitTimeoutID = null;
}

/**
 * Submits to the event ingestion service or enqueues the event for submission to the event
 * ingestion service.
 *
 * @param {EventData} eventData
 */
BrowserEventSubmitter.prototype.submitEvent = function ( eventData ) {
	this.events.push( eventData );

	if ( this.isDocumentUnloading ) {
		this.doSubmitEvents();
	} else {
		this.doDelayedSubmit();
	}

	// @ts-ignore TS2551
	this.onSubmitEvent( eventData );
};

/**
 * Submits all queued events to the event ingestion service immediately and clears the queue.
 *
 * @ignore
 */
BrowserEventSubmitter.prototype.doSubmitEvents = function () {
	if ( this.events ) {
		navigator.sendBeacon(
			// @ts-ignore TS2339
			this.eventGateUrl,
			JSON.stringify( this.events )
		);
	}

	this.events = [];
	this.delayedSubmitTimeoutID = null;
};

/**
 * Schedules a call to {@link BrowserEventSubmitter#doSubmitEvents} in 5 seconds, if a call is not
 * already scheduled.
 *
 * @ignore
 */
BrowserEventSubmitter.prototype.doDelayedSubmit = function () {
	if ( this.delayedSubmitTimeoutID ) {
		return;
	}

	const eventSubmitter = this;

	this.delayedSubmitTimeoutID = setTimeout(
		function () {
			eventSubmitter.doSubmitEvents();
		},
		DELAYED_SUBMIT_TIMEOUT * 1000
	);
};

module.exports = BrowserEventSubmitter;
