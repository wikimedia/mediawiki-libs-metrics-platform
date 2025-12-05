'use strict';

const crypto = require( 'crypto' );

const NodeIntegration = require( './../../src/DefaultIntegration' );

/**
 * @type {Integration}
 */

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
	const rnds = new Uint16Array( 5 );

	// Crypto#getRandomValues() was added in Node v15.0.0 (see
	// https://nodejs.org/api/webcrypto.html#cryptogetrandomvaluestypedarray).
	//
	// eslint-disable-next-line n/no-unsupported-features/node-builtins
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
