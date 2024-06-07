// eslint-disable-next-line no-unused-vars
const MetricsClient = require( './MetricsClient.js' );

/**
 * @param {MetricsClient} metricsClient
 * @param {string} streamName
 * @param {string} schemaID
 * @param {boolean} [init=false]
 * @constructor
 */
function Instrument( metricsClient, streamName, schemaID, init ) {
	this.metricsClient = metricsClient;
	this.streamName = streamName;
	this.schemaID = schemaID;
	this.eventSequencePosition = 0;

	if ( init ) {
		this.submitInteraction( 'init' );
	}
}

/**
 * @return {boolean}
 */
Instrument.prototype.isStreamInSample = function () {
	return this.metricsClient.isStreamInSample( this.streamName );
};

/**
 * @param {string} action
 * @param {InteractionContextData} [interactionData]
 */
Instrument.prototype.submitInteraction = function ( action, interactionData ) {
	interactionData = Object.assign(
		{},
		interactionData || {},
		{
			// eslint-disable-next-line camelcase
			funnel_event_sequence_position: this.eventSequencePosition++
		}
	);

	this.metricsClient.submitInteraction(
		this.streamName,
		this.schemaID,
		action,
		interactionData
	);
};

/**
 * @param {ElementInteractionData} interactionData
 */
Instrument.prototype.submitClick = function ( interactionData ) {

	// eslint-disable-next-line camelcase
	interactionData.funnel_event_sequence_position = this.eventSequencePosition++;

	this.metricsClient.submitClick( this.streamName, interactionData );
};

module.exports = Instrument;
