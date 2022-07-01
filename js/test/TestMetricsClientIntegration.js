/**
 * @constructor
 */
function TestMetricsClientIntegration() {
	this.pageviewId = this.generateRandomId();
	this.sessionId = this.generateRandomId();
}

TestMetricsClientIntegration.prototype.generateRandomId = function () {
	var rnds = new Array( 5 );
	for ( var i = 0; i < 5; i++ ) {
		rnds[ i ] = Math.floor( Math.random() * 0x10000 );
	}
	return ( rnds[ 0 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
		( rnds[ 1 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
		( rnds[ 2 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
		( rnds[ 3 ] + 0x10000 ).toString( 16 ).slice( 1 ) +
		( rnds[ 4 ] + 0x10000 ).toString( 16 ).slice( 1 );
};

/**
 * @param {EventData} eventData
 */
TestMetricsClientIntegration.prototype.enqueueEvent = function ( eventData ) {
	console.log( JSON.stringify( eventData ) );
};

/**
 * @param {string} streamEvent
 * @param {EventData} eventData
 */
TestMetricsClientIntegration.prototype.onSubmit = function ( streamEvent, eventData ) {
	console.log( streamEvent, JSON.stringify( eventData ) );
};

TestMetricsClientIntegration.prototype.getHostname = function () {
	return 'test.example.com';
};

/**
 * @param {string} string
 */
TestMetricsClientIntegration.prototype.logWarning = function ( string ) {
	console.log( string );
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
			client_platform_family: 'desktop_browser'
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
			pageview_id: this.getPageviewId(),
			groups: [ '*' ],
			is_bot: false,
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

module.exports = TestMetricsClientIntegration;
