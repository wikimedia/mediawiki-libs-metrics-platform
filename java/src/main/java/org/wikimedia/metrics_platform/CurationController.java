package org.wikimedia.metrics_platform;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import org.wikimedia.metrics_platform.config.CurationFilter;
import org.wikimedia.metrics_platform.event.Event;

@ThreadSafe
@ParametersAreNonnullByDefault
public class CurationController {

    public boolean eventPassesCurationRules(Event event, CurationFilter curationFilter) {
        return curationFilter.apply(event);
    }

}
