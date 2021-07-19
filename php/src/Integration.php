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

	// Context

	/**
	 * @return int|null
	 */
	public function getPageId() : ?int;

	/**
	 * @return int|null
	 */
	public function getPageNamespaceId() : ?int;

	/**
	 * @return string|null
	 */
	public function getPageNamespaceText() : ?string;

	/**
	 * @return string|null
	 */
	public function getPageTitle() : ?string;

	/**
	 * @return bool|null
	 */
	public function getPageIsRedirect() : ?bool;

	/**
	 * @return int|null
	 */
	public function getPageRevisionId() : ?int;

	/**
	 * @return string|null
	 */
	public function getPageWikidataItemId() : ?string;

	/**
	 * @return string|null
	 */
	public function getPageContentLanguage() : ?string;

	/**
	 * @return array|null
	 */
	public function getPageGroupsAllowedToEdit() : ?array;

	/**
	 * @return array|null
	 */
	public function getPageGroupsAllowedToMove() : ?array;

	/**
	 * @return int|null
	 */
	public function getUserId() : ?int;

	/**
	 * @return bool|null
	 */
	public function getUserIsLoggedIn() : ?bool;

	/**
	 * @return bool|null
	 */
	public function getUserIsBot() : ?bool;

	/**
	 * @return string|null
	 */
	public function getUserName() : ?string;

	/**
	 * @return array|null
	 */
	public function getUserGroups() : ?array;

	/**
	 * @return bool|null
	 */
	public function getUserCanProbablyEditPage() : ?bool;

	/**
	 * @return int|null
	 */
	public function getUserEditCount() : ?int;

	/**
	 * @return string|null
	 */
	public function getUserEditCountBucket() : ?string;

	/**
	 * @return int|null
	 */
	public function getUserRegistrationTimestamp() : ?int;

	/**
	 * @return string|null
	 */
	public function getUserLanguage() : ?string;

	/**
	 * @return string|null
	 */
	public function getUserLanguageVariant() : ?string;

	/**
	 * @return string|null
	 */
	public function getMediaWikiSkin() : ?string;

	/**
	 * @return string|null
	 */
	public function getMediaWikiVersion() : ?string;

	/**
	 * @return string|null
	 */
	public function getMediaWikiSiteContentLanguage() : ?string;

	/**
	 * @return string|null
	 */
	public function getAccessMethod() : ?string;

	/**
	 * @return bool|null
	 */
	public function isProduction() : ?bool;

}
