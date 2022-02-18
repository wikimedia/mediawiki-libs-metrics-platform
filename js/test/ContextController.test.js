/* eslint-disable camelcase */
var TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
var ContextController = require( '../src/ContextController.js' );

var integration = new TestMetricsClientIntegration();
var contextController = new ContextController( integration );

/** @type StreamConfig */
var streamConfig = {
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
				'page_content_language',
				'page_is_redirect',
				'page_user_groups_allowed_to_move',
				'page_user_groups_allowed_to_edit',
				'mediawiki_skin',
				'mediawiki_version',
				'mediawiki_is_production',
				'mediawiki_is_debug_mode',
				'mediawiki_site_content_language',
				'performer_is_logged_in',
				'performer_id',
				'performer_name',
				'performer_session_id',
				'performer_pageview_id',
				'performer_groups',
				'performer_is_bot',
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

QUnit.module( 'ContextController' );

QUnit.test( 'addRequestedValues()', function ( assert ) {
	var clientDt = new Date().toISOString();
	var event = contextController.addRequestedValues(
		{
			$schema: '/analytics/mediawiki/metrics_platform/event/1.0.0',
			dt: clientDt,
			client_dt: clientDt
		},
		streamConfig
	);

	assert.deepEqual( event, {

		// $schema, dt, and client_dt are unchanged
		$schema: '/analytics/mediawiki/metrics_platform/event/1.0.0',
		dt: clientDt,
		client_dt: clientDt,

		agent: {
			client_platform: 'web',
			client_platform_family: 'web'
		},
		page: {
			id: 1,
			title: 'Test',
			namespace: 0,
			namespace_name: '',
			revision_id: 1,
			wikidata_id: 'Q1',
			content_language: 'zh',
			is_redirect: false,
			user_groups_allowed_to_move: [],
			user_groups_allowed_to_edit: []
		},
		mediawiki: {
			skin: 'timeless',
			version: '1.37.0',
			is_production: true,
			is_debug_mode: false,
			site_content_language: 'zh'
		},
		performer: {
			is_logged_in: true,
			id: 1,
			name: 'TestUser',
			session_id: integration.getSessionId(),
			pageview_id: integration.getPageviewId(),
			groups: [ '*' ],
			is_bot: false,
			language: 'zh',
			language_variant: 'zh-tw',
			can_probably_edit_page: true,
			edit_count: 10,
			edit_count_bucket: '5-99 edits',
			registration_dt: 1427224089000
		}
	} );
} );
