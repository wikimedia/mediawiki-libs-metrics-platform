'use strict';

/* eslint-disable camelcase */

const TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
const ContextController = require( './../../src/ContextController.js' );

const integration = new TestMetricsClientIntegration();
const contextController = new ContextController( integration );

/** @type StreamConfig */
const streamConfig = {
	producers: {
		metrics_platform_client: {
			provide_values: [
				'agent_client_platform',
				'agent_client_platform_family',
				'page_id',
				'page_title',
				'page_namespace',
				'page_namespace_name',
				'page_revision_id',
				'page_wikidata_id',
				'page_wikidata_qid',
				'page_content_language',
				'page_is_redirect',
				'page_user_groups_allowed_to_move',
				'page_user_groups_allowed_to_edit',
				'mediawiki_skin',
				'mediawiki_version',
				'mediawiki_is_production',
				'mediawiki_is_debug_mode',
				'mediawiki_database',
				'mediawiki_site_content_language',
				'performer_is_logged_in',
				'performer_id',
				'performer_name',
				'performer_session_id',
				'performer_active_browsing_session_token',
				'performer_pageview_id',
				'performer_groups',
				'performer_is_bot',
				'performer_is_temp',
				'performer_language',
				'performer_language_variant',
				'performer_can_probably_edit_page',
				'performer_edit_count',
				'performer_edit_count_bucket',
				'performer_registration_dt'
			]
		}
	}
};

/** @type StreamConfig */
const streamConfig2 = {
	sample: {
		unit: 'pageview',
		rate: 0.5
	}
};

QUnit.module( 'ContextController' );

QUnit.test( 'addRequestedValues()', ( assert ) => {
	const clientDt = new Date().toISOString();
	const eventData = contextController.addRequestedValues(
		{
			$schema: '/analytics/mediawiki/metrics_platform/event/1.0.0',
			dt: clientDt,
			client_dt: clientDt
		},
		streamConfig
	);

	assert.deepEqual( eventData, {

		// $schema, dt, and client_dt are unchanged
		$schema: '/analytics/mediawiki/metrics_platform/event/1.0.0',
		dt: clientDt,
		client_dt: clientDt,

		agent: {
			client_platform: 'mediawiki_js',
			client_platform_family: 'desktop_browser'
		},
		page: {
			id: 1,
			title: 'Test',
			namespace: 0,
			namespace_name: '',
			revision_id: 1,
			wikidata_id: 1,
			wikidata_qid: 'Q1',
			content_language: 'zh',
			is_redirect: false,
			user_groups_allowed_to_move: [ '*' ],
			user_groups_allowed_to_edit: [ '*' ]
		},
		mediawiki: {
			skin: 'timeless',
			version: '1.37.0',
			is_production: true,
			is_debug_mode: false,
			database: 'zhwiki',
			site_content_language: 'zh'
		},
		performer: {
			is_logged_in: true,
			id: 1,
			name: 'TestUser',
			session_id: integration.getSessionId(),
			active_browsing_session_token: integration.getActiveBrowsingSessionToken(),
			pageview_id: integration.getPageviewId(),
			groups: [ '*' ],
			is_bot: false,
			is_temp: false,
			language: 'en',
			can_probably_edit_page: true,
			edit_count: 10,
			edit_count_bucket: '5-99 edits'

			// The language_variant and registration_dt property should not be set. undefined or
			// null values returned by Integration::getContextAttributes() should not be set by
			// ContextController::addRequestedValues().
		}
	} );
} );

QUnit.test( 'addRequestedValues() - mixes in sampling config', ( assert ) => {
	const clientDt = new Date().toISOString();
	const eventData = contextController.addRequestedValues(
		{
			$schema: '/analytics/mediawiki/metrics_platform/event/1.0.0',
			dt: clientDt,
			client_dt: clientDt
		},
		streamConfig2
	);

	assert.deepEqual( eventData, {

		// $schema, dt, and client_dt are unchanged
		$schema: '/analytics/mediawiki/metrics_platform/event/1.0.0',
		dt: clientDt,
		client_dt: clientDt,

		agent: {
			client_platform: 'mediawiki_js',
			client_platform_family: 'desktop_browser'
		},
		sample: streamConfig2.sample
	} );
} );
