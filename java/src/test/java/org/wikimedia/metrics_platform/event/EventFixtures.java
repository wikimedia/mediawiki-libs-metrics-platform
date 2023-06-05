package org.wikimedia.metrics_platform.event;

import static org.wikimedia.metrics_platform.event.EventProcessed.fromEvent;

import java.util.Arrays;

import org.wikimedia.metrics_platform.context.ClientDataFixtures;
import org.wikimedia.metrics_platform.context.PageData;
import org.wikimedia.metrics_platform.context.PerformerData;

public final class EventFixtures {

    private EventFixtures() {
        // Utility class, should never be instantiated
    }
    public static Event minimalEvent() {
        return new Event("test_schema", "test_stream", "test_event");
    }

    public static EventProcessed minimalEventProcessed() {
        EventProcessed eventProcessed = fromEvent(minimalEvent());
        eventProcessed.setClientData(ClientDataFixtures.getTestClientData());
        return eventProcessed;
    }

    public static EventProcessed getEvent() {
        Event event = new Event("test/event", "test.event", "testEvent");
        event.setPageData(
                PageData.builder().id(1).namespaceName("Talk").build()
        );

        EventProcessed eventProcessed = fromEvent(event);
        eventProcessed.setPerformerData(
                PerformerData.builder()
                        .groups(Arrays.asList("user", "autoconfirmed", "steward"))
                        .isLoggedIn(true).editCountBucket("1000+ edits")
                        .build()
        );
        return eventProcessed;
    }
}