<?php

namespace Wikimedia\Metrics\Test;

use Wikimedia\Metrics\ContextController;
use Wikimedia\Metrics\StreamConfig\StreamConfig;
use Wikimedia\TestingAccessWrapper;

require_once __DIR__ . '/TestIntegration.php';

/** @covers \Wikimedia\Metrics\ContextController */
class ContextControllerTest extends \PHPUnit\Framework\TestCase {

	/** @var ContextController */
	private $contextController;

	/** @var array */
	private $streamConfig;

	protected function setUp(): void {
		$contextController = new ContextController( new TestIntegration() );
		$this->contextController = TestingAccessWrapper::newFromObject( $contextController );
		$this->streamConfig = new StreamConfig( [
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
		] );
	}

	public function testAddContextValues(): void {
		$baseEvent = [
			'$schema' => "test/event",
			"meta" => [ "stream" => "test.event" ],
		];

		$event = $this->contextController->addRequestedValues( $baseEvent, $this->streamConfig );
		$pageData = $event["page"];
		$userData = $event["user"];
		$siteData = $event["mediawiki"];

		$this->assertSame( 1, $pageData["id"] );
		$this->assertSame( 0, $pageData["namespace_id"] );
		$this->assertSame( "", $pageData["namespace_text"] );
		$this->assertSame( "Test", $pageData["title"] );
		$this->assertSame( false, $pageData["is_redirect"] );
		$this->assertSame( 1, $pageData["revision_id"] );
		$this->assertSame( "zh", $pageData["content_language"] );
		$this->assertSame( "Q1", $pageData["wikidata_id"] );
		$this->assertSame( $pageData["groups_allowed_to_move"], [] );
		$this->assertSame( $pageData["groups_allowed_to_edit"], [] );

		$this->assertSame( 1, $userData["id"] );
		$this->assertSame( true, $userData["is_logged_in"] );
		$this->assertSame( "TestUser", $userData["name"] );
		$this->assertSame( $userData["groups"], [ "*" ] );
		$this->assertSame( 10, $userData["edit_count"] );
		$this->assertSame( "5-99 edits", $userData["edit_count_bucket"] );
		$this->assertSame( 1427224089000, $userData["registration_timestamp"] );
		$this->assertSame( "zh", $userData["language"] );
		$this->assertSame( "zh-tw", $userData["language_variant"] );
		$this->assertSame( false, $userData["is_bot"] );
		$this->assertSame( true, $userData["can_probably_edit_page"] );

		$this->assertSame( "timeless", $siteData["skin"] );
		$this->assertSame( "1.37.0", $siteData["version"] );
		$this->assertSame( "zh", $siteData["site_content_language"] );

		$this->assertSame( "mobile web", $event["access_method"] );
		$this->assertSame( "mediawiki_php", $event["platform"] );
		$this->assertSame( "web", $event["platform_family"] );
		$this->assertSame( true, $event["is_production"] );
	}

}
