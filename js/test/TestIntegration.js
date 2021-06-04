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

	// Utility methods

	TestIntegration.prototype.isDebugMode = function () {
		return false;
	};

	TestIntegration.prototype.clone = function ( obj ) {
		return JSON.parse( JSON.stringify( obj ) );
	};

	module.exports = TestIntegration;

}() );
