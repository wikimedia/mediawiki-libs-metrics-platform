'use strict';

/**
 * @constructor
 */
function StubEventSubmitter() {
}

/**
 * @param {EventData} eventData
 */
StubEventSubmitter.prototype.submitEvent = function ( eventData ) {
	console.log( JSON.stringify( eventData ) );

	this.onSubmitEvent( eventData );
};

/**
 * @param {EventData} eventData
 */
StubEventSubmitter.prototype.onSubmitEvent = function ( eventData ) {
	console.log( JSON.stringify( eventData ) );
};

module.exports = StubEventSubmitter;
