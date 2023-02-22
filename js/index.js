var DefaultIntegration = require( './src/DefaultIntegration.js' );
var MetricsClient = require( './src/MetricsClient.js' );

/**
 * @param {string} [streamConfigsOrigin]
 * @param {string} [eventGateOrigin]
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
