/**
 * @namespace MetricsPlatform
 */

/**
 * A specific adaptor for a non-wiki environment that the Metrics Platform Client is executing in.
 *
 * @interface ExternalIntegration
 * @memberof MetricsPlatform
 */

/**
 * Get the Stream configs URL
 *
 * @name MetricsPlatform.Integration#getStreamConfigs
 * @param {string} origin
 * @return {string}
 * @method
 */

/**
 * Fetches stream configs from some source, remote or local.
 *
 * @name MetricsPlatform.Integration#fetchStreamConfigs
 * @return {Promise<EventPlatform.StreamConfigs>}
 * @method
 */

/**
 * Gets the hostname of the current document.
 *
 * @method
 * @name MetricsPlatform.Integration#getHostname
 * @return {string}
 */

/**
 * Gets a deep clone of the object.
 *
 * @method
 * @name MetricsPlatform.Integration#clone
 * @param {Object} obj
 * @return {Object}
 */

/**
 * Gets the values for those context attributes that are available in the execution
 * environment.
 *
 * @method
 * @name MetricsPlatform.Integration#getContextAttributes
 * @return {MetricsPlatform.Context.ContextAttributes}
 */
