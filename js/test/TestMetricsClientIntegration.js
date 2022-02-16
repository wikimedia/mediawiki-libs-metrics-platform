/* eslint-disable no-console */
var IMetricsClientIntegration = require( '../src/IMetricsClientIntegration.js' );

/**
 * @constructor
 * @implements IMetricsClientIntegration
 */
var TestMetricsClientIntegration = function () {
	this.pageviewId = this.generateRandomId();
	this.sessionId = this.generateRandomId();
};

TestMetricsClientIntegration.prototype = IMetricsClientIntegration.prototype;

TestMetricsClientIntegration.prototype.enqueueEvent = function ( eventData ) {
	console.log( JSON.stringify( eventData ) );
};

TestMetricsClientIntegration.prototype.getHostname = function () {
	return 'test.example.com';
};

TestMetricsClientIntegration.prototype.generateRandomId = function () {
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

TestMetricsClientIntegration.prototype.logWarning = function ( string ) {
	console.log( string );
};

// MediaWiki context accessors

// Page

TestMetricsClientIntegration.prototype.getPageId = function () {
	return 1;
};

TestMetricsClientIntegration.prototype.getPageNamespaceId = function () {
	return 0;
};

TestMetricsClientIntegration.prototype.getPageNamespaceText = function () {
	return '';
};

TestMetricsClientIntegration.prototype.getPageTitle = function () {
	return 'Test';
};

TestMetricsClientIntegration.prototype.getPageIsRedirect = function () {
	return false;
};

TestMetricsClientIntegration.prototype.getPageRevisionId = function () {
	return 1;
};

TestMetricsClientIntegration.prototype.getPageContentLanguage = function () {
	return 'zh';
};

TestMetricsClientIntegration.prototype.getPageWikidataId = function () {
	return 'Q1';
};

TestMetricsClientIntegration.prototype.getPageRestrictionEdit = function () {
	return [];
};

TestMetricsClientIntegration.prototype.getPageRestrictionMove = function () {
	return [];
};

// User

TestMetricsClientIntegration.prototype.getUserId = function () {
	return 1;
};

TestMetricsClientIntegration.prototype.getUserIsLoggedIn = function () {
	return true;
};

TestMetricsClientIntegration.prototype.getUserName = function () {
	return 'TestUser';
};

TestMetricsClientIntegration.prototype.getUserGroups = function () {
	return [ '*' ];
};

TestMetricsClientIntegration.prototype.getUserEditCount = function () {
	return 10;
};

TestMetricsClientIntegration.prototype.getUserEditCountBucket = function () {
	return '5-99 edits';
};

TestMetricsClientIntegration.prototype.getUserRegistrationTimestamp = function () {
	return 1427224089000;
};

TestMetricsClientIntegration.prototype.getUserLanguage = function () {
	return 'zh';
};

TestMetricsClientIntegration.prototype.getUserLanguageVariant = function () {
	return 'zh-tw';
};

TestMetricsClientIntegration.prototype.getUserIsBot = function () {
	return false;
};

TestMetricsClientIntegration.prototype.getUserCanProbablyEditPage = function () {
	return true;
};

// MediaWiki/Site

TestMetricsClientIntegration.prototype.getMediaWikiSkin = function () {
	return 'timeless';
};

TestMetricsClientIntegration.prototype.getMediaWikiSiteContentLanguage = function () {
	return 'zh';
};

TestMetricsClientIntegration.prototype.getMediaWikiVersion = function () {
	return '1.37.0';
};

// Other

TestMetricsClientIntegration.prototype.getAccessMethod = function () {
	return 'mobile web';
};

TestMetricsClientIntegration.prototype.getPlatform = function () {
	return 'web';
};

TestMetricsClientIntegration.prototype.getPlatformFamily = function () {
	return 'web';
};

TestMetricsClientIntegration.prototype.isProduction = function () {
	return true;
};

// Utility methods

TestMetricsClientIntegration.prototype.isDebugMode = function () {
	return false;
};

TestMetricsClientIntegration.prototype.clone = function ( obj ) {
	return JSON.parse( JSON.stringify( obj ) );
};

TestMetricsClientIntegration.prototype.getPageviewId = function () {
	return this.pageviewId;
};

TestMetricsClientIntegration.prototype.getSessionId = function () {
	return this.sessionId;
};

module.exports = TestMetricsClientIntegration;
