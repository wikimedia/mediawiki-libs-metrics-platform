const DefaultIntegration = require( './src/DefaultIntegration.js' );
const DefaultEventTransport = require( './src/EventTransport.js' );
const MetricsClient = require( './src/ExternalMetricsClient.js' );

/**
 * @param {string} [streamConfigsOrigin]
 * @param {string} [eventIntakeUrl]
 * @return {MetricsClient}
 */
function createMetricsClient( streamConfigsOrigin, eventIntakeUrl ) {
	return new MetricsClient(
		new DefaultIntegration( streamConfigsOrigin ),
		new DefaultEventTransport( eventIntakeUrl )
	);
}

module.exports = {
	createMetricsClient: createMetricsClient,
	MetricsClient: MetricsClient,
	Integration: DefaultIntegration,
	EventTransport: DefaultEventTransport
};
