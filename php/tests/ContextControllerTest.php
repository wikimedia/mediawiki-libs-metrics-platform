<?php

namespace Wikimedia\MetricsPlatform\Tests;

use PHPUnit\Framework\TestCase;
use Wikimedia\MetricsPlatform\ContextController;
use Wikimedia\MetricsPlatform\StreamConfig\StreamConfig;

require_once __DIR__ . '/TestIntegration.php';

/** @covers \Wikimedia\MetricsPlatform\ContextController */
class ContextControllerTest extends TestCase {

	/** @var ContextController */
	private $contextController;

	/** @var StreamConfig */
	private $streamConfig;

	protected function setUp(): void {
		$this->contextController = new ContextController( new TestIntegration() );
		$this->streamConfig = new StreamConfig( [
			"schema_title" => "test/event",
			"producers" => [
				"metrics_platform_client" => [
					"provide_values" => StreamConfig::CONTEXTUAL_ATTRIBUTES,
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

		$agentData = $event["agent"];
		$pageData = $event["page"];
		$userData = $event["performer"];
		$siteData = $event["mediawiki"];

		$this->assertArrayNotHasKey( "app_install_id", $agentData );
		$this->assertSame( "mediawiki_php", $agentData["client_platform"] );
		$this->assertArrayNotHasKey( "client_platform_family", $agentData );

		$this->assertSame( 1, $pageData["id"] );
		$this->assertSame( "Test", $pageData["title"] );
		$this->assertSame( 0, $pageData["namespace"] );
		$this->assertSame( "", $pageData["namespace_name"] );
		$this->assertSame( 1, $pageData["revision_id"] );
		$this->assertSame( "Q1", $pageData["wikidata_id"] );
		$this->assertSame( "zh", $pageData["content_language"] );
		$this->assertSame( false, $pageData["is_redirect"] );
		$this->assertSame( $pageData["user_groups_allowed_to_move"], [] );
		$this->assertSame( $pageData["user_groups_allowed_to_edit"], [] );

		$this->assertSame( "timeless", $siteData["skin"] );
		$this->assertSame( "1.37.0", $siteData["version"] );
		$this->assertSame( false, $siteData["is_production"] );
		$this->assertSame( true, $siteData["is_debug_mode"] );
		$this->assertSame( "zhwiki", $siteData['db_name'] );
		$this->assertSame( "zh", $siteData["site_content_language"] );
		$this->assertSame( "zh-tw", $siteData["site_content_language_variant"] );

		$this->assertSame( true, $userData["is_logged_in"] );
		$this->assertSame( 1, $userData["id"] );
		$this->assertSame( "TestUser", $userData["name"] );
		$this->assertArrayNotHasKey( "session_id", $userData );
		$this->assertArrayNotHasKey( "pageview_id", $userData );
		$this->assertSame( $userData["groups"], [ "*" ] );
		$this->assertSame( false, $userData["is_bot"] );
		$this->assertSame( "zh", $userData["language"] );
		$this->assertSame( "zh-tw", $userData["language_variant"] );
		$this->assertSame( true, $userData["can_probably_edit_page"] );
		$this->assertSame( 10, $userData["edit_count"] );
		$this->assertSame( "5-99 edits", $userData["edit_count_bucket"] );
		$this->assertSame( 1427224089000, $userData["registration_dt"] );
	}
}
