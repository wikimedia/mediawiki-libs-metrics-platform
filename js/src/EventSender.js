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
 * @param {string} streamName
 * @param {MetricsPlatform.Context.ContextAttributes} contextAttributes
 * @param {MetricsPlatform.EventTransport} eventTransport
 * @constructor
 * @implements {MetricsPlatform.EventSender}
 * @memberof MetricsPlatform
 */
function DefaultEventSender( streamName, contextAttributes, eventTransport ) {
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
			stream: this.streamName
		}
	);

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
