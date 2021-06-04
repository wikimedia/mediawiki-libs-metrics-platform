/**
 * Client for producing events to the Wikimedia metrics platform.
 *
 * Produce events with MetricsClient.submit().
 */
( function () {

	var AssociationController = require( './AssociationController.js' ),
		SamplingController = require( './SamplingController.js' );

	/**
	 * @param {!Object} integration client integration object (see DefaultConfiguration.js)
	 * @param {!Object} streamConfigs stream configs
	 * @return {!Object}
	 * @constructor
	 */
	function MetricsClient( integration, streamConfigs ) {
		this.associationController = new AssociationController( integration );
		this.samplingController = new SamplingController( this.associationController );
		this.integration = integration;
		this.streamConfigs = streamConfigs;
	}

	/**
	 * @param {!string} streamName
	 * @return {?Object}
	 */
	MetricsClient.prototype.getStreamConfig = function ( streamName ) {
		// If streamConfigs are false, then stream config usage is not enabled.
		// Always return an empty object.
		// FIXME: naming
		if ( this.streamConfigs === false ) {
			return {};
		}

		if ( !this.streamConfigs[ streamName ] ) {
			// In case no config has been assigned to the given streamName,
			// return undefined, so that the developer can discern between
			// a stream that is not configured, and a stream with config = {}.
			return undefined;
		}

		return this.integration.clone( this.streamConfigs[ streamName ] );
	};

	/**
	 * Adds required fields:
	 * - meta.stream: the target stream name
	 * - meta.domain: the domain associated with this event
	 * - dt: the client-side timestamp (unless this is a migrated legacy event,
	 *       in which case the timestamp will already be present as client_dt).
	 *
	 * @param {!Object} eventData
	 * @param {!string} streamName
	 * @return {!Object}
	 */
	MetricsClient.prototype.addRequiredMetadata = function ( eventData, streamName ) {
		eventData.meta = eventData.meta || {};
		eventData.meta.stream = streamName;
		eventData.meta.domain = this.integration.getHostname();

		//
		// The 'dt' field is reserved for the internal use of this library,
		// and should not be set by any other caller.
		//
		// (1) 'dt' is a client-side timestamp for new events
		//      and a server-side timestamp for legacy events.
		// (2) 'dt' will be provided by EventGate if omitted here,
		//      so it should be omitted for legacy events (and
		//      deleted if present).
		//
		// We detect legacy events by looking for the 'client_dt' field
		// set in the .produce() method (see above).
		//
		if ( eventData.client_dt ) {
			delete eventData.dt;
		} else {
			eventData.dt = new Date().toISOString();
		}
		return eventData;
	};

	/**
	 * Submit an event according to the given stream's configuration.
	 * If DNT is enabled, this method does nothing.
	 *
	 * @param {!string} streamName name of the stream to send eventData to
	 * @param {!Object} eventData data to send to streamName
	 */
	MetricsClient.prototype.submit = function ( streamName, eventData ) {
		//
		// NOTE
		// If stream configuration is disabled (config.streamConfigs === false),
		// then client.streamConfig will return an empty object {},
		// i.e. a truthy value, for all stream names.
		//
		// FIXME
		// The convention that disabling stream configuration results
		// in enabling any caller to send any event, with no sampling,
		// etc., is correct in the sense of the boolean logic, but
		// counter-intuitive and likely hard to keep correct as more
		// behavior is added. We should revisit.
		var streamConfig = this.getStreamConfig( streamName );

		if ( !streamConfig ) {
			//
			// If stream configurations are enabled but no
			// stream configuration has been loaded for streamName
			// (and we are not in debugMode), we assume the client
			// is misconfigured. Rather than produce potentially
			// inconsistent data, the event submission does not
			// proceed.
			//
			// FIXME
			// See comment above; this should be made less
			// confusing.
			return;
		}

		// If stream is not in sample, do not log the event.
		if ( !this.samplingController.streamInSample( streamConfig ) ) {
			return;
		}

		if ( !eventData || !eventData.$schema ) {
			//
			// If the caller has not provided a $schema field
			// in eventData, the event submission does not
			// proceed.
			//
			// The $schema field represents the (versioned)
			// schema which the caller expects eventData
			// will validate against (once the appropriate
			// additions have been made by this client).
			//
			this.integration.logWarning(
				'submit( ' + streamName + ', eventData ) called with eventData ' +
				'missing required field "$schema". No event will issue.'
			);
			return;
		}

		this.addRequiredMetadata( eventData, streamName );

		this.integration.enqueueEvent( eventData );
	};

	module.exports = MetricsClient;

}() );
