/**
 * Controller for associating events with pageviews and sessions.
 *
 * @param {!Object} integration
 * @constructor
 */
function AssociationController( integration ) {
	this.integration = integration;
}

AssociationController.prototype.generateId = function () {
	return this.integration.generateRandomId();
};

AssociationController.prototype.getPageviewId = function () {
	if ( !this.pageviewId ) {
		this.pageviewId = this.generateId();
	}
	return this.pageviewId;
};

AssociationController.prototype.getSessionId = function () {
	if ( !this.sessionId ) {
		this.sessionId = this.generateId();
	}
	return this.sessionId;
};

AssociationController.prototype.resetPageviewId = function () {
	this.pageviewId = null;
};

AssociationController.prototype.resetSessionId = function () {
	this.sessionId = null;
};

module.exports = AssociationController;
