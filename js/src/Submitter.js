// eslint-disable-next-line no-unused-vars
const MetricsClient = require( './MetricsClient.js' );

/**
 * @param {MetricsClient} metricsClient
 * @param {string} streamName
 * @param {string} streamID
 * @param {boolean} [init=false]
 * @constructor
 */
function Submitter( metricsClient, streamName, streamID, init ) {
	this.metricsClient = metricsClient;
	this.streamName = streamName;
	this.streamID = streamID;
	this.i = 0;

	if ( init ) {
		this.submitInteraction( 'init' );
	}
}

/**
 * @return {boolean}
 */
Submitter.prototype.isStreamInSample = function () {
	return this.metricsClient.isStreamInSample( this.streamName );
};

/**
 * @param {string} action
 * @param {InteractionContextData} [interactionData]
 */
Submitter.prototype.submitInteraction = function ( action, interactionData ) {
	interactionData = Object.assign(
		{},
		interactionData || {},
		{
			// eslint-disable-next-line camelcase
			funnel_event_sequence_position: this.i++
		}
	);

	this.metricsClient.submitInteraction(
		this.streamName,
		this.streamID,
		action,
		interactionData
	);
};

/**
 * @param {ElementInteractionData} interactionData
 */
Submitter.prototype.submitClick = function ( interactionData ) {

	// eslint-disable-next-line camelcase
	interactionData.funnel_event_sequence_position = this.i++;

	this.metricsClient.submitClick( this.streamName, interactionData );
};

module.exports = Submitter;
