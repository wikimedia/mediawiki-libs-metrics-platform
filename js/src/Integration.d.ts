/**
 * An adaptor for the environment that the Metrics Platform Client is executing in.
 */
interface Integration {

	/**
	 * Enqueues the event to be submitted to the event ingestion service.
	 */
	enqueueEvent: ( eventData: EventData ) => void;

	/**
	 * Called when an event is enqueued to be submitted to the event ingestion service.
	 */
	onSubmit: ( streamName: string, eventData: EventData ) => void;

	/**
	 * Gets the hostname of the current document.
	 */
	getHostname: () => string;

	/**
	 * Logs the warning to whatever logging backend that the execution environment, e.g. the
	 * console.
	 */
	logWarning: ( message: string ) => void;

	/**
	 * Gets a deep clone of the object.
	 */
	clone: ( obj: Object ) => Object;

	/**
	 * Gets the values for those context attributes that are available in the execution
	 * environment.
	 */
	getContextAttributes: () => ContextAttributes;

	// NOTE: The following are required for compatibility with the current impl. but the
	// information is also available via ::getContextualAttributes() above.

	/**
	 * Gets a token unique to the current pageview within the execution environment.
	 */
	getPageviewId: () => string;

	/**
	 * Gets a token unique to the current session within the execution environment.
	 */
	getSessionId: () => string;
}
