var crypto = require( 'crypto' );

var NodeIntegration = require( './../../src/DefaultIntegration' );

/**
 * @param {EventData} eventData
 */
NodeIntegration.prototype.enqueueEvent = function ( eventData ) {
	fetch( this.eventGateUrl, {
		method: 'POST',
		body: JSON.stringify( eventData )
	} );
};

/**
 * @return {string}
 */
NodeIntegration.prototype.getHostname = function () {
	return require( 'os' ).hostname();
};

/**
 * A smaller (but not as performant) version of
 * [`mw.user.generateRandomSessionId()`](https://gerrit.wikimedia.org/g/mediawiki/core/+/master/resources/src/mediawiki.user.js#47).
 * The version of that method which used the Web Crypto API was implemented in
 * https://gerrit.wikimedia.org/r/c/mediawiki/core/+/187876.
 *
 * @return {string}
 */
function generateRandomId() {
	var rnds = new Uint16Array( 5 );

	crypto.webcrypto.getRandomValues( rnds );

	return rnds.map( ( rnd ) => rnd + 0x10000 ).join( '' );
}

/**
 * @return {string}
 */
NodeIntegration.prototype.getPageviewId = function () {
	return generateRandomId();
};

/**
 * @return {string}
 */
NodeIntegration.prototype.getSessionId = function () {
	return generateRandomId();
};

module.exports = NodeIntegration;
