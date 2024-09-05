/**
 * @constructor
 */
function NullInstrument() {}

/**
 * See {@link MetricsClient#isStreamInSample}.
 *
 * @return {boolean} Always `false`
 */
NullInstrument.prototype.isStreamInSample = function () {
	return false;
};

/**
 * @return {boolean} Always `false`
 */
NullInstrument.prototype.isEnabled = function () {
	return false;
};

/**
 * See {@link MetricsClient#submitInteraction}.
 */
NullInstrument.prototype.submitInteraction = function () {};

/**
 * See {@link MetricsClient#submitClick}.
 */
NullInstrument.prototype.submitClick = function () {};

module.exports = NullInstrument;
