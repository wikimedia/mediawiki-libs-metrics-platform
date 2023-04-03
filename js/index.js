var DefaultIntegration = require( './src/DefaultIntegration.js' );
var MetricsClient = require( './src/ExternalMetricsClient.js' );

/**
 * @param {string} [streamConfigsOrigin]
 * @param {string} [eventGateOrigin]
 * @return {MetricsClient}
 */
function createMetricsClient( streamConfigsOrigin, eventGateOrigin ) {
	return new MetricsClient(
		new DefaultIntegration( streamConfigsOrigin, eventGateOrigin )
	);
}

module.exports = {
	createMetricsClient: createMetricsClient,
	MetricsClient: MetricsClient,
	Integration: DefaultIntegration
};
