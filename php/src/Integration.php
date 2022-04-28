<?php

namespace Wikimedia\Metrics;

interface Integration {

	/**
	 * Get the hostname associated with the current request.
	 *
	 * @return string
	 */
	public function getHostName(): string;

	/**
	 * Transmit an event to a destination intake service.
	 *
	 * @param array $event event data, represented as an associative array
	 */
	public function send( array $event ): void;

	// Context

	/**
	 * @return string|null
	 */
	public function getAgentAppInstallId(): ?string;

	/**
	 * @return string
	 */
	public function getAgentClientPlatform(): string;

	/**
	 * @return string|null
	 */
	public function getClientPlatformFamily(): ?string;

	/**
	 * @return int
	 */
	public function getPageId(): int;

	/**
	 * @return string
	 */
	public function getPageTitle(): string;

	/**
	 * @return int
	 */
	public function getPageNamespace(): int;

	/**
	 * @return string
	 */
	public function getPageNamespaceName(): string;

	/**
	 * @return int|null
	 */
	public function getPageRevisionId(): ?int;

	/**
	 * @return string|null
	 */
	public function getPageWikidataItemId(): ?string;

	/**
	 * @return string|null
	 */
	public function getPageContentLanguage(): ?string;

	/**
	 * @return bool
	 */
	public function getPageIsRedirect(): bool;

	/**
	 * @return array
	 */
	public function getPageGroupsAllowedToMove(): array;

	/**
	 * @return array
	 */
	public function getPageGroupsAllowedToEdit(): array;

	/**
	 * @return string
	 */
	public function getMediaWikiSkin(): string;

	/**
	 * @return string
	 */
	public function getMediaWikiVersion(): string;

	/**
	 * @return bool
	 */
	public function getMediaWikiIsProduction(): bool;

	/**
	 * @return bool
	 */
	public function getMediaWikiIsDebugMode(): bool;

	/**
	 * @return string
	 */
	public function getMediaWikiDBName(): string;

	/**
	 * @return string
	 */
	public function getMediaWikiSiteContentLanguage(): string;

	/**
	 * @return string|null
	 */
	public function getMediaWikiSiteContentLanguageVariant(): ?string;

	/**
	 * @return bool
	 */
	public function getUserIsLoggedIn(): bool;

	/**
	 * @return int
	 */
	public function getUserId(): int;

	/**
	 * @return string|null
	 */
	public function getUserName(): ?string;

	/**
	 * @return string|null
	 */
	public function getUserSessionId(): ?string;

	/**
	 * @return string|null
	 */
	public function getUserPageviewId(): ?string;

	/**
	 * @return array
	 */
	public function getUserGroups(): array;

	/**
	 * @return bool
	 */
	public function getUserIsBot(): bool;

	/**
	 * @return string
	 */
	public function getUserLanguage(): string;

	/**
	 * @return string|null
	 */
	public function getUserLanguageVariant(): ?string;

	/**
	 * @return bool
	 */
	public function getUserCanProbablyEditPage(): bool;

	/**
	 * @return int|null
	 */
	public function getUserEditCount(): ?int;

	/**
	 * @return string|null
	 */
	public function getUserEditCountBucket(): ?string;

	/**
	 * @return int|null
	 */
	public function getUserRegistrationTimestamp(): ?int;
}
