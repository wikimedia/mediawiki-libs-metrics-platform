interface EventSubmitter {

    /**
     * Submits to the event ingestion service or enqueues the event for submission to the event
     * ingestion service.
     */
    submitEvent: ( event: EventData ) => void;

    /**
     * Called when an event is enqueued for submission to the event ingestion service.
     */
    onSubmitEvent: ( event: EventData ) => void;
}