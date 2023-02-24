<?php

namespace Wikimedia\MetricsPlatform\Tests;

use Wikimedia\MetricsPlatform\Integration;

class TestIntegration implements Integration {

	/** @var array */
	private $contextAttributes = [
		'agent_app_install_id' => null,
		'agent_client_platform' => 'mediawiki_php',
		'agent_client_platform_family' => null,

		'performer_is_logged_in' => true,
		'performer_id' => 1,
		'performer_is_bot' => false,
		'performer_name' => 'TestUser',
		'performer_groups' => [ '*' ],
		'performer_can_probably_edit_page' => true,
		'performer_edit_count' => 10,
		'performer_edit_count_bucket' => '5-99 edits',
		'performer_registration_dt' => 1427224089000,
		'performer_language' => 'zh',
		'performer_language_variant' => 'zh-tw',

		'mediawiki_skin' => 'timeless',
		'mediawiki_version' => '1.39.0',
		'mediawiki_is_production' => false,
		'mediawiki_is_debug_mode' => true,
		'mediawiki_database' => 'zhwiki',
		'mediawiki_site_content_language' => 'zh',
		'mediawiki_site_content_language_variant' => 'zh-tw',

		'page_id' => 1,
		'page_namespace' => 0,
		'page_namespace_name' => '',
		'page_title' => 'Test',
		'page_is_redirect' => false,
		'page_revision_id' => 1,
		'page_wikidata_id' => 1,
		'page_wikidata_qid' => 'Q1',
		'page_content_language' => 'zh',
		'page_user_groups_allowed_to_edit' => [],
		'page_user_groups_allowed_to_move' => [],
	];

	/**
	 * @param string $name
	 * @return mixed|null
	 */
	public function getContextAttribute( string $name ) {
		return $this->contextAttributes[ $name ] ?? null;
	}
}
