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
				'page_id',
				'page_namespace_id',
				'page_namespace_text',
				'page_title',
				'page_is_redirect',
				'page_revision_id',
				'page_wikidata_id',
				'page_content_language',
				'page_user_groups_allowed_to_edit',
				'page_user_groups_allowed_to_move',
				'user_id',
				'user_is_logged_in',
				'user_is_bot',
				'user_name',
				'user_groups',
				'user_can_probably_edit_page',
				'user_edit_count',
				'user_edit_count_bucket',
				'user_registration_timestamp',
				'user_language',
				'user_language_variant',
				'mediawiki_skin',
				'mediawiki_version',
				'mediawiki_site_content_language',
				'access_method',
				'platform',
				'platform_family',
				'is_production'
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

		page: {
			id: 1,
			namespace_id: 0,
			namespace_text: '',
			title: 'Test',
			is_redirect: false,
			revision_id: 1,
			wikidata_id: 'Q1',
			content_language: 'zh',
			user_groups_allowed_to_edit: [],
			user_groups_allowed_to_move: []
		},
		user: {
			id: 1,
			is_logged_in: true,
			is_bot: false,
			name: 'TestUser',
			groups: [ '*' ],
			can_probably_edit_page: true,
			edit_count: 10,
			edit_count_bucket: '5-99 edits',
			registration_timestamp: 1427224089000,
			language: 'zh',
			language_variant: 'zh-tw'
		},
		mediawiki: {
			skin: 'timeless',
			version: '1.37.0',
			site_content_language: 'zh'
		},
		access_method: 'mobile web',
		platform: 'web',
		platform_family: 'web',
		is_production: true
	} );
} );
