import Foundation

class SamplingController {

    private let integration: MetricsClientIntegration
    private let sessionController: SessionController

    init(integration: MetricsClientIntegration, sessionController: SessionController) {
        self.integration = integration
        self.sessionController = sessionController
    }

    /**
     * Gets whether the given stream is in-sample.
     *
     * If the stream is not configured for sampling, then it is always in-sample.
     *
     * If the stream is incorrectly configured for sampling – the sampling unit is not either "session" or "device", or
     * the sampling rate is not between 0 and 1 – then the stream is always out-sample.
     *
     * Otherwise, perform a deterministic (not stochastic) determination of whether the configured unit is in-sample
     * according to the configured rate.
     *
     * The algorithm works in a "widen the net on frozen fish" fashion – a stream determined to be in-sample for a
     * lower rate will be determined to be in-sample for a higher rate. For example: two streams, A and B, are both
     * configured to sample based on the device identifier at a rate of 0.1 and 0.2, respectively; events logged to
     * stream A could also be logged to stream B.
     *
     * Refer to sampling settings section in [mw:Wikimedia Product/Analytics Infrastructure/Stream configuration][0]
     * for more information.
     *
     * [0]:  https://www.mediawiki.org/wiki/Wikimedia_Product/Analytics_Infrastructure/Stream_configuration
     */
    func inSample(stream: String, config: StreamConfig) -> Bool {
        guard let samplingConfig = config.getSamplingConfig() else {
            /// If the stream config does not have a sampling config, then the stream is always in-sample.
            return true
        }

        guard
            let unit = samplingConfig.unit,
            unit == "session" || unit == "device",
            let rate = samplingConfig.rate
        else {
            NSLog(
                "MetricsClient: Stream \"\(stream)\" cannot be determined to be in-sample. The sampling unit must be either \"session\" or \"device\" and the rate must be a number between 0 and 1."
            )

            return false
        }
        
        let id = unit == "session" ? sessionController.sessionId() : integration.appInstallId();
 
        guard let token = UInt32(id.prefix(8), radix: 16) else {
            /// NOTE: This should never happen.
            return false
        }

        return (Double(token) / Double(UInt32.max)) < rate
    }
}
