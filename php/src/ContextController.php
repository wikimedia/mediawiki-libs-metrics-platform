<?php

namespace Wikimedia\Metrics;

use Wikimedia\Metrics\StreamConfig\StreamConfig;

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
	 * Add contextual values configured in the stream configuration.
	 *
	 * @param array $event
	 * @param StreamConfig $streamConfig
	 * @return array
	 */
	public function addRequestedValues( array $event, StreamConfig $streamConfig ): array {
		$requestedValues = $streamConfig->getRequestedValues();

		foreach ( $requestedValues as $requestedValue ) {
			list( $primaryKey, $secondaryKey ) = explode( '_', $requestedValue, 2 );

			if ( !isset( $event[$primaryKey] ) ) {
				$event[$primaryKey] = [];
			}

			$value = $this->getContextualAttributeByName( $requestedValue );

			// Contextual attributes are null by default. Only add the requested contextual
			// attribute - incurring the cost of transporting it - if it is not null.
			if ( $value !== null ) {
				$event[$primaryKey][$secondaryKey] = $value;
			}
		}

		return $event;
	}

	/**
	 * @param string $name
	 * @return mixed
	 */
	private function getContextualAttributeByName( string $name ) {
		$int = $this->integration;

		switch ( $name ) {
			case 'agent_app_install_id':
				return $int->getAgentAppInstallId();
			case 'agent_client_platform':
				return $int->getAgentClientPlatform();
			case 'agent_client_platform_family':
				return $int->getClientPlatformFamily();

			case 'page_id':
				return $int->getPageId();
			case 'page_title':
				return $int->getPageTitle();
			case 'page_namespace':
				return $int->getPageNamespace();
			case 'page_namespace_name':
				return $int->getPageNamespaceName();
			case 'page_revision_id':
				return $int->getPageRevisionId();
			case 'page_wikidata_id':
				return $int->getPageWikidataItemId();
			case 'page_content_language':
				return $int->getPageContentLanguage();
			case 'page_is_redirect':
				return $int->getPageIsRedirect();
			case 'page_user_groups_allowed_to_move':
				return $int->getPageGroupsAllowedToMove();
			case 'page_user_groups_allowed_to_edit':
				return $int->getPageGroupsAllowedToEdit();

			case 'mediawiki_skin':
				return $int->getMediaWikiSkin();
			case 'mediawiki_version':
				return $int->getMediaWikiVersion();
			case 'mediawiki_is_production':
				return $int->getMediaWikiIsProduction();
			case 'mediawiki_is_debug_mode':
				return $int->getMediaWikiIsDebugMode();
			case 'mediawiki_site_content_language':
				return $int->getMediaWikiSiteContentLanguage();
			case 'mediawiki_site_content_language_variant':
				return $int->getMediaWikiSiteContentLanguageVariant();

			case 'performer_is_logged_in':
				return $int->getUserIsLoggedIn();
			case 'performer_id':
				return $int->getUserId();
			case 'performer_name':
				return $int->getUserName();
			case 'performer_session_id':
				return $int->getUserSessionId();
			case 'performer_pageview_id':
				return $int->getUserPageviewId();
			case 'performer_groups':
				return $int->getUserGroups();
			case 'performer_is_bot':
				return $int->getUserIsBot();
			case 'performer_language':
				return $int->getUserLanguage();
			case 'performer_language_variant':
				return $int->getUserLanguageVariant();
			case 'performer_can_probably_edit_page':
				return $int->getUserCanProbablyEditPage();
			case 'performer_edit_count':
				return $int->getUserEditCount();
			case 'performer_edit_count_bucket':
				return $int->getUserEditCountBucket();
			case 'performer_registration_dt':
				return $int->getUserRegistrationTimestamp();
		}
	}
}
