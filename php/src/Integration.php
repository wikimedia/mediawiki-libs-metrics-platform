<?php

namespace Wikimedia\Metrics;

interface Integration {

	/**
	 * Get the stream configs to be used by the metrics client.
	 *
	 * @return array
	 */
	public function getStreamConfigs() : ?array;

	/**
	 * Get the hostname associated with the current request.
	 *
	 * @return string
	 */
	public function getHostName() : string;

	/**
	 * Get an ISO 8601 timestamp string for the current time.
	 * The timestamp *should* include milliseconds represented as
	 * fractional seconds to three decimal places:
	 *
	 * YYYY-MM-DDTHH:mm:ss.sssZ
	 *
	 * @return string
	 */
	public function getTimestamp() : string;

	/**
	 * Transmit an event to a destination intake service.
	 *
	 * @param array $event event data, represented as an associative array
	 */
	public function send( array $event ) : void;

}
