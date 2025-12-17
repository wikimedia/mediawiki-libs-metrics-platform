const DefaultIntegration = require( './src/DefaultIntegration.js' );
const DefaultLogger = require( './src/DefaultLogger.js' );
const DefaultEventSubmitter = require( './src/DefaultEventSubmitter.js' );
const MetricsClient = require( './src/ExternalMetricsClient.js' );

/**
 * @param {string} [streamConfigsOrigin]
 * @param {string} [eventIntakeUrl]
 * @return {MetricsClient}
 */
function createMetricsClient( streamConfigsOrigin, eventIntakeUrl ) {
	return new MetricsClient(
		new DefaultIntegration( streamConfigsOrigin ),
		new DefaultLogger(),
		new DefaultEventSubmitter( eventIntakeUrl )
	);
}

module.exports = {
	createMetricsClient: createMetricsClient,
	MetricsClient: MetricsClient,
	Integration: DefaultIntegration,
	Logger: DefaultLogger,
	EventSubmitter: DefaultEventSubmitter
};
