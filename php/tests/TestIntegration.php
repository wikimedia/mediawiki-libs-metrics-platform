<?php

namespace Wikimedia\Metrics\Test;

use Wikimedia\Metrics\Integration;

class TestIntegration implements Integration {

	/** @inheritDoc */
	public function getStreamConfigs(): array {
		return [];
	}

	/** @inheritDoc */
	public function getHostName(): string {
		return 'www.example.org';
	}

	/** @inheritDoc */
	public function getTimestamp(): string {
		return '1970-01-01T00:00:00.000Z';
	}

	/** @inheritDoc */
	public function send( array $event ): void {
	}

	// Context

	/** @inheritDoc */
	public function getPageId(): ?int {
		return 1;
	}

	/** @inheritDoc */
	public function getPageNamespaceId(): ?int {
		return 0;
	}

	/** @inheritDoc */
	public function getPageNamespaceText(): ?string {
		return "";
	}

	/** @inheritDoc */
	public function getPageTitle(): ?string {
		return "Test";
	}

	/** @inheritDoc */
	public function getPageIsRedirect(): ?bool {
		return false;
	}

	/** @inheritDoc */
	public function getPageRevisionId(): ?int {
		return 1;
	}

	/** @inheritDoc */
	public function getPageWikidataItemId(): ?string {
		return "Q1";
	}

	/** @inheritDoc */
	public function getPageContentLanguage(): ?string {
		return "zh";
	}

	/** @inheritDoc */
	public function getPageGroupsAllowedToEdit(): ?array {
		return [];
	}

	/** @inheritDoc */
	public function getPageGroupsAllowedToMove(): ?array {
		return [];
	}

	/** @inheritDoc */
	public function getUserId(): ?int {
		return 1;
	}

	/** @inheritDoc */
	public function getUserIsLoggedIn(): ?bool {
		return true;
	}

	/** @inheritDoc */
	public function getUserIsBot(): ?bool {
		return false;
	}

	/** @inheritDoc */
	public function getUserName(): ?string {
		return "TestUser";
	}

	/** @inheritDoc */
	public function getUserGroups(): ?array {
		return [ "*" ];
	}

	/** @inheritDoc */
	public function getUserCanProbablyEditPage(): ?bool {
		return true;
	}

	/** @inheritDoc */
	public function getUserEditCount(): ?int {
		return 10;
	}

	/** @inheritDoc */
	public function getUserEditCountBucket(): ?string {
		return "5-99 edits";
	}

	/** @inheritDoc */
	public function getUserRegistrationTimestamp(): ?int {
		return 1427224089000;
	}

	/** @inheritDoc */
	public function getUserLanguage(): ?string {
		return "zh";
	}

	/** @inheritDoc */
	public function getUserLanguageVariant(): ?string {
		return "zh-tw";
	}

	/** @inheritDoc */
	public function getMediaWikiSkin(): ?string {
		return "timeless";
	}

	/** @inheritDoc */
	public function getMediaWikiVersion(): ?string {
		return "1.37.0";
	}

	/** @inheritDoc */
	public function getMediaWikiSiteContentLanguage(): ?string {
		return "zh";
	}

	/** @inheritDoc */
	public function getAccessMethod(): ?string {
		return "mobile web";
	}

	/** @inheritDoc */
	public function isProduction(): ?bool {
		return true;
	}

}
