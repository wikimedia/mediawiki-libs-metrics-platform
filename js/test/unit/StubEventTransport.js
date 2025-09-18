'use strict';

/**
 * @constructor
 * @implements {MetricsPlatform.EventTransport}
 */
function StubEventTransport() {
}

/**
 * @param {EventData} eventData
 */
StubEventTransport.prototype.transportEvent = function ( eventData ) {
	console.log( JSON.stringify( eventData ) );
};

module.exports = StubEventTransport;
