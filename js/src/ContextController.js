var IMetricsClientIntegration = require( './IMetricsClientIntegration.js' ); // eslint-disable-line no-unused-vars

/**
 * Add context requested in stream configuration.
 *
 * @param {IMetricsClientIntegration} integration
 * @constructor
 */
function ContextController( integration ) {
	this.integration = integration;
}

/**
 * Mix the contextual attributes requested in stream configuration into the given event data.
 *
 * @param {MetricsPlatformEventData} eventData
 * @param {StreamConfig} streamConfig
 * @return {MetricsPlatformEventData}
 */
ContextController.prototype.addRequestedValues = function ( eventData, streamConfig ) {
	var requestedValues = streamConfig &&
		streamConfig.producers &&
		streamConfig.producers.metrics_platform_client &&
		streamConfig.producers.metrics_platform_client.provide_values;

	eventData.agent = eventData.agent || {};

	/* eslint-disable camelcase */
	eventData.agent.client_platform = this.integration.getPlatform();
	eventData.agent.client_platform_family = this.integration.getPlatformFamily();
	/* eslint-enable camelcase */

	if ( !Array.isArray( requestedValues ) ) {
		return eventData;
	}
	for ( var i = 0; i < requestedValues.length; i++ ) {
		/** @type {StreamProducerContextAttribute} */
		var requestedValue = requestedValues[ i ];
		var t;
		/* eslint-disable max-len, camelcase */
		switch ( requestedValue ) {

			// Agent
			//
			// agent.client_platform and agent.client_platform_family are set regardless of the
			// stream configuration.
			case 'agent_client_platform':
				break;
			case 'agent_client_platform_family':
				break;

			// Page
			case 'page_id':
				eventData.page = eventData.page || {};
				eventData.page.id = this.integration.getPageId();
				break;
			case 'page_title':
				eventData.page = eventData.page || {};
				eventData.page.title = this.integration.getPageTitle();
				break;
			case 'page_namespace':
				eventData.page = eventData.page || {};
				eventData.page.namespace = this.integration.getPageNamespaceId();
				break;
			case 'page_namespace_name':
				eventData.page = eventData.page || {};
				eventData.page.namespace_name = this.integration.getPageNamespaceText();
				break;
			case 'page_revision_id':
				eventData.page = eventData.page || {};
				eventData.page.revision_id = this.integration.getPageRevisionId();
				break;
			case 'page_wikidata_id':
				t = this.integration.getPageWikidataId();
				if ( t ) {
					eventData.page = eventData.page || {};
					eventData.page.wikidata_id = t;
				}
				break;
			case 'page_content_language':
				eventData.page = eventData.page || {};
				eventData.page.content_language = this.integration.getPageContentLanguage();
				break;
			case 'page_is_redirect':
				eventData.page = eventData.page || {};
				eventData.page.is_redirect = this.integration.getPageIsRedirect();
				break;
			case 'page_user_groups_allowed_to_edit':
				eventData.page = eventData.page || {};
				eventData.page.user_groups_allowed_to_edit = this.integration.getPageRestrictionEdit();
				break;
			case 'page_user_groups_allowed_to_move':
				eventData.page = eventData.page || {};
				eventData.page.user_groups_allowed_to_move = this.integration.getPageRestrictionMove();
				break;

			// MediaWiki/Site
			case 'mediawiki_skin':
				eventData.mediawiki = eventData.mediawiki || {};
				eventData.mediawiki.skin = this.integration.getMediaWikiSkin();
				break;
			case 'mediawiki_version':
				eventData.mediawiki = eventData.mediawiki || {};
				eventData.mediawiki.version = this.integration.getMediaWikiVersion();
				break;
			case 'mediawiki_is_production':
				eventData.mediawiki = eventData.mediawiki || {};
				eventData.mediawiki.is_production = this.integration.isProduction();
				break;
			case 'mediawiki_is_debug_mode':
				eventData.mediawiki = eventData.mediawiki || {};
				eventData.mediawiki.is_debug_mode = this.integration.isDebugMode();
				break;
			case 'mediawiki_site_content_language':
				eventData.mediawiki = eventData.mediawiki || {};
				eventData.mediawiki.site_content_language = this.integration.getMediaWikiSiteContentLanguage();
				break;

			// Performer
			case 'performer_is_logged_in':
				eventData.performer = eventData.performer || {};
				eventData.performer.is_logged_in = this.integration.getUserIsLoggedIn();
				break;
			case 'performer_id':
				t = this.integration.getUserId();
				if ( t ) {
					eventData.performer = eventData.performer || {};
					eventData.performer.id = t;
				}
				break;
			case 'performer_name':
				t = this.integration.getUserName();
				if ( t ) {
					eventData.performer = eventData.performer || {};
					eventData.performer.name = t;
				}
				break;
			case 'performer_session_id':
				eventData.performer = eventData.performer || {};
				eventData.performer.session_id = this.integration.getSessionId();
				break;
			case 'performer_pageview_id':
				eventData.performer = eventData.performer || {};
				eventData.performer.pageview_id = this.integration.getPageviewId();
				break;
			case 'performer_groups':
				eventData.performer = eventData.performer || {};
				eventData.performer.groups = this.integration.getUserGroups();
				break;
			case 'performer_is_bot':
				eventData.performer = eventData.performer || {};
				eventData.performer.is_bot = this.integration.getUserIsBot();
				break;
			case 'performer_language':
				eventData.performer = eventData.performer || {};
				eventData.performer.language = this.integration.getUserLanguage();
				break;
			case 'performer_language_variant':
				eventData.performer = eventData.performer || {};
				eventData.performer.language_variant = this.integration.getUserLanguageVariant();
				break;
			case 'performer_can_probably_edit_page':
				eventData.performer = eventData.performer || {};
				eventData.performer.can_probably_edit_page = this.integration.getUserCanProbablyEditPage();
				break;
			case 'performer_edit_count':
				t = this.integration.getUserEditCount();
				if ( t ) {
					eventData.performer = eventData.performer || {};
					eventData.performer.edit_count = t;
				}
				break;
			case 'performer_edit_count_bucket':
				t = this.integration.getUserEditCountBucket();
				if ( t ) {
					eventData.performer = eventData.performer || {};
					eventData.performer.edit_count_bucket = t;
				}
				break;
			case 'performer_registration_dt':
				t = this.integration.getUserRegistrationTimestamp();
				if ( t ) {
					eventData.performer = eventData.performer || {};
					eventData.performer.registration_dt = t;
				}
				break;

			default:
				this.integration.logWarning( 'EventLogging: Ignoring unknown requested value' +
					' "' + requestedValue + '"' );
				break;
		}
		/* eslint-enable max-len, camelcase */
	}
	return eventData;
};

module.exports = ContextController;
