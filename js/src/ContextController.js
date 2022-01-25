/**
 * Adds context requested in stream configuration.
 *
 * @param {!IMetricsClientIntegration} integration client integration implementation
 * @constructor
 */
function ContextController( integration ) {
	this.integration = integration;
}

/**
 * Add values requested in stream configuration to the event data.
 *
 * @param {!Object} eventData
 * @param {!Object} streamConfig
 * @return {!Object}
 */
ContextController.prototype.addRequestedValues = function ( eventData, streamConfig ) {
	var i, requestedValue, requestedValues = streamConfig &&
		streamConfig.producers &&
		streamConfig.producers.metrics_platform_client &&
		streamConfig.producers.metrics_platform_client.provide_values;

	if ( !Array.isArray( requestedValues ) ) {
		return eventData;
	}
	for ( i = 0; i < requestedValues.length; i++ ) {
		requestedValue = requestedValues[ i ];
		/* eslint-disable max-len, camelcase */
		switch ( requestedValue ) {
			// Page
			case 'page_id':
				eventData.page = eventData.page || {};
				eventData.page.id = this.integration.getPageId();
				break;
			case 'page_namespace_id':
				eventData.page = eventData.page || {};
				eventData.page.namespace_id = this.integration.getPageNamespaceId();
				break;
			case 'page_namespace_text':
				eventData.page = eventData.page || {};
				eventData.page.namespace_text = this.integration.getPageNamespaceText();
				break;
			case 'page_title':
				eventData.page = eventData.page || {};
				eventData.page.title = this.integration.getPageTitle();
				break;
			case 'page_is_redirect':
				eventData.page = eventData.page || {};
				eventData.page.is_redirect = this.integration.getPageIsRedirect();
				break;
			case 'page_revision_id':
				eventData.page = eventData.page || {};
				eventData.page.revision_id = this.integration.getPageRevisionId();
				break;
			case 'page_wikidata_id':
				eventData.page = eventData.page || {};
				eventData.page.wikidata_id = this.integration.getPageWikidataId();
				break;
			case 'page_content_language':
				eventData.page = eventData.page || {};
				eventData.page.content_language = this.integration.getPageContentLanguage();
				break;
			case 'page_user_groups_allowed_to_edit':
				eventData.page = eventData.page || {};
				eventData.page.user_groups_allowed_to_edit = this.integration.getPageRestrictionEdit();
				break;
			case 'page_user_groups_allowed_to_move':
				eventData.page = eventData.page || {};
				eventData.page.user_groups_allowed_to_move = this.integration.getPageRestrictionMove();
				break;

			// User
			case 'user_id':
				eventData.user = eventData.user || {};
				eventData.user.id = this.integration.getUserId();
				break;
			case 'user_is_logged_in':
				eventData.user = eventData.user || {};
				eventData.user.is_logged_in = this.integration.getUserIsLoggedIn();
				break;
			case 'user_is_bot':
				eventData.user = eventData.user || {};
				eventData.user.is_bot = this.integration.getUserIsBot();
				break;
			case 'user_name':
				eventData.user = eventData.user || {};
				eventData.user.name = this.integration.getUserName();
				break;
			case 'user_groups':
				eventData.user = eventData.user || {};
				eventData.user.groups = this.integration.getUserGroups();
				break;
			case 'user_can_probably_edit_page':
				eventData.user = eventData.user || {};
				eventData.user.can_probably_edit_page = this.integration.getUserCanProbablyEditPage();
				break;
			case 'user_edit_count':
				eventData.user = eventData.user || {};
				eventData.user.edit_count = this.integration.getUserEditCount();
				break;
			case 'user_edit_count_bucket':
				eventData.user = eventData.user || {};
				eventData.user.edit_count_bucket = this.integration.getUserEditCountBucket();
				break;
			case 'user_registration_timestamp':
				eventData.user = eventData.user || {};
				eventData.user.registration_timestamp = this.integration.getUserRegistrationTimestamp();
				break;
			case 'user_language':
				eventData.user = eventData.user || {};
				eventData.user.language = this.integration.getUserLanguage();
				break;
			case 'user_language_variant':
				eventData.user = eventData.user || {};
				eventData.user.language_variant = this.integration.getUserLanguageVariant();
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
			case 'mediawiki_site_content_language':
				eventData.mediawiki = eventData.mediawiki || {};
				eventData.mediawiki.site_content_language = this.integration.getMediaWikiSiteContentLanguage();
				break;

			// Device
			case 'device_pixel_ratio':
				eventData.device = eventData.device || {};
				eventData.device.pixel_ratio = this.integration.getDevicePixelRatio();
				break;
			case 'device_hardware_concurrency':
				eventData.device = eventData.device || {};
				eventData.device.hardware_concurrency = this.integration.getDeviceHardwareConcurrency();
				break;
			case 'device_max_touch_points':
				eventData.device = eventData.device || {};
				eventData.device.max_touch_points = this.integration.getDeviceMaxTouchPoints();
				break;

			// Other
			case 'access_method':
				eventData.access_method = this.integration.getAccessMethod();
				break;
			case 'platform':
				eventData.platform = this.integration.getPlatform();
				break;
			case 'platform_family':
				eventData.platform_family = this.integration.getPlatformFamily();
				break;
			case 'is_debug_mode':
				eventData.is_debug_mode = this.integration.isDebugMode();
				break;
			case 'is_production':
				eventData.is_production = this.integration.isProduction();
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
