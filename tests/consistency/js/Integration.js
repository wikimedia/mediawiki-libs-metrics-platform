class Integration {
	constructor( filename ) {
		this.data = require( filename );
	}

	enqueueEvent( event ) {
		console.log( JSON.stringify( event ) );
	}

	onSubmit( _streamName, _eventData ) {}

	getHostname() {
		return this.data.hostname;
	}

	logWarning( message ) {
		console.warn( message );
	}

	clone( obj ) {
		return Object.assign( {}, obj );
	}

	getContextAttributes() {
		return this.data;
	}

	getPageviewId() {
		return this.data.performer.pageview_id;
	}

	getSessionId() {
		return this.data.performer.session_id;
	}
}

module.exports = Integration;
