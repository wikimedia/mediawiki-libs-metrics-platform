<?php

namespace Wikimedia\Metrics;

class ContextController {

	/** @var Integration */
	private $integration;

	/**
	 * ContextController constructor.
	 * @param Integration $integration
	 */
	public function __construct( Integration $integration ) {
		$this->integration = $integration;
	}

	/**
	 * Add context values configured in the stream configuration.
	 *
	 * @param array $event
	 * @param array $streamConfig
	 * @return array
	 */
	public function addRequestedValues( array $event, array $streamConfig ): array {
		if ( !$streamConfig ) {
			return $event;
		}
		$requestedValues = $this->getRequestedValues( $streamConfig );
		if ( !$requestedValues ) {
			return $event;
		}
		foreach ( $requestedValues as $value ) {
			switch ( $value ) {
				// Page
				case "page_id":
					$event["page"]["id"] = $this->integration->getPageId();
					break;
				case "page_namespace_id":
					$event["page"]["namespace_id"] = $this->integration->getPageNamespaceId();
					break;
				case "page_namespace_text":
					$event["page"]["namespace_text"] = $this->integration->getPageNamespaceText();
					break;
				case "page_title":
					$event["page"]["title"] = $this->integration->getPageTitle();
					break;
				case "page_is_redirect":
					$event["page"]["is_redirect"] = $this->integration->getPageIsRedirect();
					break;
				case "page_revision_id":
					$event["page"]["revision_id"] = $this->integration->getPageRevisionId();
					break;
				case "page_content_language":
					$event["page"]["content_language"] = $this->integration->getPageContentLanguage();
					break;
				case "page_wikidata_id":
					$event["page"]["wikidata_id"] = $this->integration->getPageWikidataItemId();
					break;
				case "page_groups_allowed_to_edit":
					$event["page"]["groups_allowed_to_edit"] = $this->integration->getPageGroupsAllowedToEdit();
					break;
				case "page_groups_allowed_to_move":
					$event["page"]["groups_allowed_to_move"] = $this->integration->getPageGroupsAllowedToMove();
					break;

				// User
				case "user_id":
					$event["user"]["id"] = $this->integration->getUserId();
					break;
				case "user_is_logged_in":
					$event["user"]["is_logged_in"] = $this->integration->getUserIsLoggedIn();
					break;
				case "user_name":
					$event["user"]["name"] = $this->integration->getUserName();
					break;
				case "user_groups":
					$event["user"]["groups"] = $this->integration->getUserGroups();
					break;
				case "user_edit_count":
					$event["user"]["edit_count"] = $this->integration->getUserEditCount();
					break;
				case "user_edit_count_bucket":
					$event["user"]["edit_count_bucket"] = $this->integration->getUserEditCountBucket();
					break;
				case "user_registration_timestamp":
					$event["user"]["registration_timestamp"] = $this->integration->getUserRegistrationTimestamp();
					break;
				case "user_language":
					$event["user"]["language"] = $this->integration->getUserLanguage();
					break;
				case "user_language_variant":
					$event["user"]["language_variant"] = $this->integration->getUserLanguageVariant();
					break;
				case "user_is_bot":
					$event["user"]["is_bot"] = $this->integration->getUserIsBot();
					break;
				case "user_can_probably_edit_page":
					$event["user"]["can_probably_edit_page"] = $this->integration->getUserCanProbablyEditPage();
					break;

				// MediaWiki/Site
				case "mediawiki_skin":
					$event["mediawiki"]["skin"] = $this->integration->getMediaWikiSkin();
					break;
				case "mediawiki_version":
					$event["mediawiki"]["version"] = $this->integration->getMediaWikiVersion();
					break;
				case "mediawiki_site_content_language":
					$event["mediawiki"]["site_content_language"] =
						$this->integration->getMediaWikiSiteContentLanguage();
					break;

				// Misc
				case "access_method":
					$event["access_method"] = $this->integration->getAccessMethod();
					break;
				case "platform":
					$event["platform"] = "mediawiki_php";
					break;
				case "platform_family":
					$event["platform_family"] = "web";
					break;
				case "is_production":
					$event["is_production"] = $this->integration->isProduction();
					break;
				default:
					break;
			}
		}
		return $event;
	}

	/**
	 * Extract the list of requested values from a stream configuration, if present.
	 *
	 * @param array $streamConfig
	 * @return array|null
	 */
	private function getRequestedValues( array $streamConfig ): ?array {
		if ( !$streamConfig ) {
			return null;
		}
		if ( !$streamConfig["producers"] ) {
			return null;
		}
		if ( !$streamConfig["producers"]["metrics_platform_client"] ) {
			return null;
		}
		if ( !$streamConfig["producers"]["metrics_platform_client"]["provide_values"] ) {
			return null;
		}
		if ( !is_array( $streamConfig["producers"]["metrics_platform_client"]["provide_values"] ) ) {
			return null;
		}
		return $streamConfig["producers"]["metrics_platform_client"]["provide_values"];
	}

}
