/* eslint-disable no-console */
( function () {

	var IMetricsClientIntegration = require( '../src/IMetricsClientIntegration.js' );

	var TestIntegration = function () {};
	TestIntegration.prototype = IMetricsClientIntegration.prototype;

	TestIntegration.prototype.enqueueEvent = function ( eventData ) {
		console.log( JSON.stringify( eventData ) );
	};

	TestIntegration.prototype.getHostname = function () {
		return 'test.example.com';
	};

	TestIntegration.prototype.generateRandomId = function () {
		var i, rnds = new Array( 5 );
		for ( i = 0; i < 5; i++ ) {
			rnds[ i ] = Math.floor( Math.random() * 0x10000 );
		}
		return ( rnds[ 0 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
			( rnds[ 1 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
			( rnds[ 2 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
			( rnds[ 3 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
			( rnds[ 4 ] + 0x10000 ).toString( 16 ).slice( 1 );
	};

	TestIntegration.prototype.logWarning = function ( string ) {
		console.log( string );
	};

	// MediaWiki context accessors

	// Page

	TestIntegration.prototype.getPageId = function () {
		return 1;
	};

	TestIntegration.prototype.getPageNamespaceId = function () {
		return 0;
	};

	TestIntegration.prototype.getPageNamespaceText = function () {
		return '';
	};

	TestIntegration.prototype.getPageTitle = function () {
		return 'Test';
	};

	TestIntegration.prototype.getPageIsRedirect = function () {
		return false;
	};

	TestIntegration.prototype.getPageRevisionId = function () {
		return 1;
	};

	TestIntegration.prototype.getPageContentLanguage = function () {
		return 'zh';
	};

	TestIntegration.prototype.getPageWikidataId = function () {
		return 'Q1';
	};

	TestIntegration.prototype.getPageRestrictionEdit = function () {
		return [];
	};

	TestIntegration.prototype.getPageRestrictionMove = function () {
		return [];
	};

	// User

	TestIntegration.prototype.getUserId = function () {
		return 1;
	};

	TestIntegration.prototype.getUserIsLoggedIn = function () {
		return true;
	};

	TestIntegration.prototype.getUserName = function () {
		return 'TestUser';
	};

	TestIntegration.prototype.getUserGroups = function () {
		return [ '*' ];
	};

	TestIntegration.prototype.getUserEditCount = function () {
		return 10;
	};

	TestIntegration.prototype.getUserEditCountBucket = function () {
		return '5-99 edits';
	};

	TestIntegration.prototype.getUserRegistrationTimestamp = function () {
		return 1427224089000;
	};

	TestIntegration.prototype.getUserLanguage = function () {
		return 'zh';
	};

	TestIntegration.prototype.getUserLanguageVariant = function () {
		return 'zh-tw';
	};

	TestIntegration.prototype.getUserIsBot = function () {
		return false;
	};

	TestIntegration.prototype.getUserCanProbablyEditPage = function () {
		return true;
	};

	// MediaWiki/Site

	TestIntegration.prototype.getMediaWikiSkin = function () {
		return 'timeless';
	};

	TestIntegration.prototype.getMediaWikiSiteContentLanguage = function () {
		return 'zh';
	};

	TestIntegration.prototype.getMediaWikiVersion = function () {
		return '1.37.0';
	};

	// Other

	TestIntegration.prototype.getAccessMethod = function () {
		return 'mobile web';
	};

	TestIntegration.prototype.getPlatform = function () {
		return 'web';
	};

	TestIntegration.prototype.getPlatformFamily = function () {
		return 'web';
	};

	TestIntegration.prototype.isProduction = function () {
		return true;
	};

	// Utility methods

	TestIntegration.prototype.isDebugMode = function () {
		return false;
	};

	TestIntegration.prototype.clone = function ( obj ) {
		return JSON.parse( JSON.stringify( obj ) );
	};

	module.exports = TestIntegration;

}() );
