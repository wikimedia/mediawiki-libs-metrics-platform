<?php

namespace Wikimedia\MetricsPlatform\Tests\Consistency;

use Wikimedia\MetricsPlatform\Integration as IIntegration;

class Integration implements IIntegration {

	/** @var array */
	private $data;

	public function __construct( $filename ) {
		$rawContents = file_get_contents( $filename );

		$this->data = json_decode( $rawContents, true );
	}

	public function getHostName(): string {
		return $this->data['hostname'];
	}

	public function send( array $event ): void {
		echo json_encode( $event );
	}

	public function getAgentAppInstallId(): ?string {
		return $this->data['agent']['app_install_id'] ?? null;
	}

	public function getAgentClientPlatform(): string {
		return $this->data['agent']['client_platform'];
	}

	public function getAgentClientPlatformFamily(): ?string {
		return $this->data['agent']['client_platform_family'] ?? null;
	}

	public function getPageId(): int {
		return $this->data['page']['id'];
	}

	public function getPageTitle(): string {
		return $this->data['page']['title'];
	}

	public function getPageNamespace(): int {
		return $this->data['page']['namespace'];
	}

	public function getPageNamespaceName(): string {
		return $this->data['page']['namespace_name'];
	}

	public function getPageRevisionId(): ?int {
		return $this->data['page']['revision_id'] ?? null;
	}

	public function getPageWikidataId(): ?string {
		return $this->data['page']['wikidata_id'] ?? null;
	}

	public function getPageContentLanguage(): ?string {
		return $this->data['page']['content_language'] ?? null;
	}

	public function getPageIsRedirect(): bool {
		return $this->data['page']['is_redirect'];
	}

	public function getPageGroupsAllowedToMove(): array {
		return $this->data['page']['user_groups_allowed_to_move'];
	}

	public function getPageGroupsAllowedToEdit(): array {
		return $this->data['page']['user_groups_allowed_to_edit'];
	}

	public function getMediaWikiSkin(): string {
		return $this->data['mediawiki']['skin'];
	}

	public function getMediaWikiVersion(): string {
		return $this->data['mediawiki']['version'];
	}

	public function getMediaWikiIsProduction(): bool {
		return $this->data['mediawiki']['is_production'];
	}

	public function getMediaWikiIsDebugMode(): bool {
		return $this->data['mediawiki']['is_debug_mode'];
	}

	public function getMediaWikiDBName(): string {
		return $this->data['mediawiki']['db_name'];
	}

	public function getMediaWikiSiteContentLanguage(): string {
		return $this->data['mediawiki']['site_content_language'];
	}

	public function getMediaWikiSiteContentLanguageVariant(): ?string {
		return $this->data['mediawiki']['site_content_language_variant'] ?? null;
	}

	public function getUserIsLoggedIn(): bool {
		return $this->data['performer']['is_logged_in'];
	}

	public function getUserId(): int {
		return $this->data['performer']['id'];
	}

	public function getUserName(): ?string {
		return $this->data['performer']['name'] ?? null;
	}

	public function getUserSessionId(): ?string {
		return $this->data['performer']['session_id'] ?? null;
	}

	public function getUserPageviewId(): ?string {
		return $this->data['performer']['pageview_id'];
	}

	public function getUserGroups(): array {
		return $this->data['performer']['groups'];
	}

	public function getUserIsBot(): bool {
		return $this->data['performer']['is_bot'];
	}

	public function getUserLanguage(): string {
		return $this->data['performer']['language'];
	}

	public function getUserLanguageVariant(): ?string {
		return $this->data['performer']['language_variant'] ?? null;
	}

	public function getUserCanProbablyEditPage(): bool {
		return $this->data['performer']['can_probably_edit_page'];
	}

	public function getUserEditCount(): ?int {
		return $this->data['performer']['edit_count'] ?? null;
	}

	public function getUserEditCountBucket(): ?string {
		return $this->data['performer']['edit_count_bucket'] ?? null;
	}

	public function getUserRegistrationTimestamp(): ?int {
		return $this->data['performer']['registration_dt'] ?? null;
	}
}
