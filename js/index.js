const DefaultIntegration = require( './src/DefaultIntegration.js' );
const DefaultEventSubmitter = require( './src/DefaultEventSubmitter.js' ).DefaultEventSubmitter;
const MetricsClient = require( './src/ExternalMetricsClient.js' );

/**
 * @param {string} [streamConfigsOrigin]
 * @param {string} [eventGateOrigin]
 * @return {MetricsClient}
 */
function createMetricsClient( streamConfigsOrigin, eventGateOrigin ) {
	return new MetricsClient(
		new DefaultIntegration( streamConfigsOrigin ),
		new DefaultEventSubmitter( eventGateOrigin )
	);
}

module.exports = {
	createMetricsClient: createMetricsClient,
	MetricsClient: MetricsClient,
	Integration: DefaultIntegration,
	EventSubmitter: DefaultEventSubmitter
};
