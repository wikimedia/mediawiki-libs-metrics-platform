<?php

namespace Wikimedia\Metrics\Test;

use Wikimedia\Metrics\ContextController;
use Wikimedia\TestingAccessWrapper;

require_once __DIR__ . '/TestIntegration.php';

class ContextControllerTest extends \PHPUnit\Framework\TestCase {

	/** @var ContextController */
	private $contextController;

	/** @var array */
	private $streamConfig;

	protected function setUp() : void {
		$contextController = new ContextController( new TestIntegration() );
		$this->contextController = TestingAccessWrapper::newFromObject( $contextController );
		$this->streamConfig = [
			"schema_title" => "test/event",
			"producers" => [
				"metrics_platform_client" => [
					"provide_values" => [
						"page_id",
						"page_namespace_id",
						"page_namespace_text",
						"page_title",
						"page_is_redirect",
						"page_revision_id",
						"page_content_language",
						"page_wikidata_id",
						"page_groups_allowed_to_edit",
						"page_groups_allowed_to_move",
						"user_id",
						"user_is_logged_in",
						"user_name",
						"user_groups",
						"user_edit_count",
						"user_edit_count_bucket",
						"user_registration_timestamp",
						"user_language",
						"user_language_variant",
						"user_is_bot",
						"user_can_probably_edit_page",
						"mediawiki_skin",
						"mediawiki_version",
						"mediawiki_site_content_language",
						"access_method",
						"platform",
						"platform_family",
						"is_debug_mode",
						"is_production",
					],
				],
			],
		];
	}

	/** @covers \Wikimedia\Metrics\ContextController::addRequestedValues */
	public function testAddContextValues() : void {
		$baseEvent = [
			'$schema' => "test/event",
			"meta" => [ "stream" => "test.event" ],
		];

		$event = $this->contextController->addRequestedValues( $baseEvent, $this->streamConfig );
		$pageData = $event["page"];
		$userData = $event["user"];
		$siteData = $event["mediawiki"];

		$this->assertSame( $pageData["id"], 1 );
		$this->assertSame( $pageData["namespace_id"], 0 );
		$this->assertSame( $pageData["namespace_text"], "" );
		$this->assertSame( $pageData["title"], "Test" );
		$this->assertSame( $pageData["is_redirect"], false );
		$this->assertSame( $pageData["revision_id"], 1 );
		$this->assertSame( $pageData["content_language"], "zh" );
		$this->assertSame( $pageData["wikidata_id"], "Q1" );
		$this->assertSame( $pageData["groups_allowed_to_move"], [] );
		$this->assertSame( $pageData["groups_allowed_to_edit"], [] );

		$this->assertSame( $userData["id"], 1 );
		$this->assertSame( $userData["is_logged_in"], true );
		$this->assertSame( $userData["name"], "TestUser" );
		$this->assertSame( $userData["groups"], [ "*" ] );
		$this->assertSame( $userData["edit_count"], 10 );
		$this->assertSame( $userData["edit_count_bucket"], "5-99 edits" );
		$this->assertSame( $userData["registration_timestamp"], 1427224089000 );
		$this->assertSame( $userData["language"], "zh" );
		$this->assertSame( $userData["language_variant"], "zh-tw" );
		$this->assertSame( $userData["is_bot"], false );
		$this->assertSame( $userData["can_probably_edit_page"], true );

		$this->assertSame( $siteData["skin"], "timeless" );
		$this->assertSame( $siteData["version"], "1.37.0" );
		$this->assertSame( $siteData["site_content_language"], "zh" );

		$this->assertSame( $event["access_method"], "mobile web" );
		$this->assertSame( $event["platform"], "mediawiki_php" );
		$this->assertSame( $event["platform_family"], "web" );
		$this->assertSame( $event["is_production"], true );
	}

}
