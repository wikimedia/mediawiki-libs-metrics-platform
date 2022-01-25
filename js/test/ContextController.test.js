/* eslint-disable camelcase */
var TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
var ContextController = require( '../src/ContextController.js' );

var integration = new TestMetricsClientIntegration();
var contextController = new ContextController( integration );

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
	var event = contextController.addRequestedValues( {}, streamConfig );
	assert.strictEqual( event.page.id, 1 );
	assert.strictEqual( event.page.namespace_id, 0 );
	assert.strictEqual( event.page.namespace_text, '' );
	assert.strictEqual( event.page.title, 'Test' );
	assert.strictEqual( event.page.is_redirect, false );
	assert.strictEqual( event.page.revision_id, 1 );
	assert.strictEqual( event.page.wikidata_id, 'Q1' );
	assert.strictEqual( event.page.content_language, 'zh' );
	assert.strictEqual( event.page.user_groups_allowed_to_edit.length, 0 );
	assert.strictEqual( event.page.user_groups_allowed_to_move.length, 0 );

	assert.strictEqual( event.user.id, 1 );
	assert.strictEqual( event.user.is_logged_in, true );
	assert.strictEqual( event.user.is_bot, false );
	assert.strictEqual( event.user.name, 'TestUser' );
	assert.strictEqual( event.user.groups.length, 1 );
	assert.strictEqual( event.user.groups[ 0 ], '*' );
	assert.strictEqual( event.user.can_probably_edit_page, true );
	assert.strictEqual( event.user.edit_count, 10 );
	assert.strictEqual( event.user.edit_count_bucket, '5-99 edits' );
	assert.strictEqual( event.user.registration_timestamp, 1427224089000 );
	assert.strictEqual( event.user.language, 'zh' );
	assert.strictEqual( event.user.language_variant, 'zh-tw' );

	assert.strictEqual( event.mediawiki.skin, 'timeless' );
	assert.strictEqual( event.mediawiki.version, '1.37.0' );
	assert.strictEqual( event.mediawiki.site_content_language, 'zh' );

	assert.strictEqual( event.access_method, 'mobile web' );
	assert.strictEqual( event.platform, 'web' );
	assert.strictEqual( event.platform_family, 'web' );
	assert.strictEqual( event.is_production, true );
} );
