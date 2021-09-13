( function () {
	var TestMetricsClientIntegration = require( './TestMetricsClientIntegration.js' );
	var AssociationController = require( '../src/AssociationController.js' );

	var integration = new TestMetricsClientIntegration();
	var associationController = new AssociationController( integration );

	QUnit.module( 'AssociationController' );

	QUnit.test( 'resetSessionId()', function ( assert ) {
		var before = associationController.getSessionId();
		associationController.resetSessionId();
		var after = associationController.getSessionId();
		assert.notEqual( before, after );
	} );

	QUnit.test( 'resetPageviewId()', function ( assert ) {
		var before = associationController.getPageviewId();
		associationController.resetPageviewId();
		var after = associationController.getPageviewId();
		assert.notEqual( before, after );
	} );
}() );
