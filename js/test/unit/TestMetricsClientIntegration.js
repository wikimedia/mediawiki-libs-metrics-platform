'use strict';

/**
 * @constructor
 */
function TestMetricsClientIntegration() {
	this.pageviewId = this.generateRandomId();
	this.sessionId = this.generateRandomId();
	this.activeBrowsingSessionToken = this.generateRandomId();
}

TestMetricsClientIntegration.prototype.generateRandomId = function () {
	const rnds = new Array( 5 );
	for ( let i = 0; i < 5; i++ ) {
		rnds[ i ] = Math.floor( Math.random() * 0x10000 );
	}
	return ( rnds[ 0 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
		( rnds[ 1 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
		( rnds[ 2 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
		( rnds[ 3 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
		( rnds[ 4 ] + 0x10000 ).toString( 16 ).slice( 1 );
};

/**
 * @return {Promise<StreamConfigs>}
 */
TestMetricsClientIntegration.prototype.fetchStreamConfigs = function () {
	return Promise.resolve( {} );
};

TestMetricsClientIntegration.prototype.getHostname = function () {
	return 'test.example.com';
};

/**
 * @param {Object} obj
 * @return {Object}
 */
TestMetricsClientIntegration.prototype.clone = function ( obj ) {
	return JSON.parse( JSON.stringify( obj ) );
};

TestMetricsClientIntegration.prototype.getContextAttributes = function () {
	/* eslint-disable camelcase */
	return {
		agent: {
			client_platform: 'mediawiki_js',
			client_platform_family: 'desktop_browser',
			ua_string: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36'
		},
		page: {
			id: 1,
			title: 'Test',
			namespace_id: 0,
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
			site_content_language: 'zh',
			site_content_language_variant: 'zh-tw'
		},
		performer: {
			is_logged_in: true,
			id: 1,
			name: 'TestUser',
			session_id: this.getSessionId(),
			active_browsing_session_token: this.getActiveBrowsingSessionToken(),
			pageview_id: this.getPageviewId(),
			groups: [ '*' ],
			is_bot: false,
			is_temp: false,
			language: 'en',
			can_probably_edit_page: true,
			edit_count: 10,
			edit_count_bucket: '5-99 edits'

			// For whatever reason, there was a problem fetching the registration date of the user.
			// registration_dt: 1427224089000
		}
	};
	/* eslint-enable camelcase */
};

TestMetricsClientIntegration.prototype.getPageviewId = function () {
	return this.pageviewId;
};

TestMetricsClientIntegration.prototype.getSessionId = function () {
	return this.sessionId;
};

TestMetricsClientIntegration.prototype.getActiveBrowsingSessionToken = function () {
	return this.activeBrowsingSessionToken;
};

module.exports = TestMetricsClientIntegration;
