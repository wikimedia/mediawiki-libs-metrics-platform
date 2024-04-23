const DefaultEventSubmitter = require( './DefaultEventSubmitter.js' );

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
	}

	// @ts-ignore TS2551
	this.onSubmitEvent( eventData );
};

/**
 * Submits all queued events to the event ingestion service.
 */
BrowserEventSubmitter.prototype.doSubmitEvents = function () {
	if ( this.events ) {
		return;
	}

	navigator.sendBeacon(
		// @ts-ignore TS2339
		this.eventGateUrl,
		JSON.stringify( this.events )
	);

	this.events = [];
};

module.exports = BrowserEventSubmitter;
