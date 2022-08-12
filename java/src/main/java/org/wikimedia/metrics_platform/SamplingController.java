package org.wikimedia.metrics_platform;

import javax.annotation.concurrent.ThreadSafe;

/**
 * SamplingController: computes various sampling functions on the client
 *
 * Sampling is based on associative identifiers, each of which have a
 * well-defined scope, and sampling config, which each stream provides as
 * part of its configuration.
 */
@ThreadSafe
class SamplingController {

    private final MetricsClientIntegration integration;
    private final SessionController sessionController;

    SamplingController(MetricsClientIntegration integration, SessionController sessionController) {
        this.integration = integration;
        this.sessionController = sessionController;
    }

    /**
     * @param streamConfig stream config
     * @return true if in sample or false otherwise
     */
    boolean isInSample(StreamConfig streamConfig) {
        if (!streamConfig.hasSamplingConfig()) {
            return true;
        }
        SamplingConfig samplingConfig = streamConfig.getProducerConfig().getMetricsPlatformClientConfig().getSamplingConfig();
        if (samplingConfig.getRate() == 1.0) {
            return true;
        }
        if (samplingConfig.getRate() == 0.0) {
            return false;
        }
        return getSamplingValue(samplingConfig.getIdentifier()) < samplingConfig.getRate();
    }

    /**
     * @param identifier identifier type from sampling config
     * @return a floating point value between 0.0 and 1.0 (inclusive)
     */
    double getSamplingValue(SamplingConfig.Identifier identifier) {
        String token = getSamplingId(identifier).substring(0, 8);
        return (double) Long.parseLong(token, 16) / (double) 0xFFFFFFFFL;
    }

    /**
     * Returns the ID string to be used when evaluating presence in sample.
     * The ID used is configured in stream config.
     *
     * @param identifier Identifier enum value
     * @return the requested ID string
     */
    String getSamplingId(SamplingConfig.Identifier identifier) {
        if (identifier == SamplingConfig.Identifier.SESSION) {
            return sessionController.getSessionId();
        }
        if (identifier == SamplingConfig.Identifier.DEVICE) {
            return integration.getAppInstallId();
        }
        throw new RuntimeException("Bad identifier type");
    }

}
