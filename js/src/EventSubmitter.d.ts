interface EventSubmitter {

    /**
     * Submits to the event intake service or enqueues the event for submission to the event
     * intake service.
     */
    submitEvent: ( event: EventData ) => void;

    /**
     * Called when an event is enqueued for submission to the event intake service.
     */
    onSubmitEvent: ( event: EventData ) => void;
}
