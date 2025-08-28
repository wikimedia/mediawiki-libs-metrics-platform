/**
 * Represents the process by which an event is sent to a stream after the stream is determined to
 * be in-sample.
 *
 * @interface EventSender
 * @memberof MetricsPlatform
 */

/**
 * @method
 * @name MetricsPlatform.EventSender#sendEvent
 * @param {EventPlatform.EventData} event
 */

/**
 * @param {string} domain The value to use for `meta.domain`
 * @param {string} streamName The value to use for `meta.stream`
 * @param {MetricsPlatform.Context.ContextAttributes} contextAttributes
 * @param {MetricsPlatform.EventTransport} eventTransport
 * @constructor
 * @implements {MetricsPlatform.EventSender}
 * @memberof MetricsPlatform
 */
function DefaultEventSender(
	domain,
	streamName,
	contextAttributes,
	eventTransport
) {
	this.domain = domain;
	this.streamName = streamName;
	this.contextAttributes = contextAttributes;
	this.eventTransport = eventTransport;
}

/**
 * @param {EventPlatform.EventData} eventData
 */
DefaultEventSender.prototype.sendEvent = function ( eventData ) {
	eventData = Object.assign( eventData, this.contextAttributes );
	eventData.meta = Object.assign(
		eventData.meta || {},
		{
			domain: this.domain,
			stream: this.streamName
		}
	);

	// If the 'dt' field is not set, then the event intake service, EventGate, will set it. The
	// 'dt' field is a client-side timestamp for modern events and a server-side timestamp for
	// legacy events. Thus, if we detect a legacy event, i.e. an event with the 'client_dt' field
	// set, then we delete the 'dt' field.
	if ( eventData.client_dt ) {
		delete eventData.dt;
	} else {
		eventData.dt = eventData.dt || new Date().toISOString();
	}

	this.eventTransport.transportEvent( eventData );
};

/**
 * @constructor
 * @implements {MetricsPlatform.EventSender}
 * @memberof MetricsPlatform
 */
function DummyEventSender() {
}

DummyEventSender.prototype.sendEvent = function () {
};

module.exports = {
	DefaultEventSender,
	DummyEventSender
};
