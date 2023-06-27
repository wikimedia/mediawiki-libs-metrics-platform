package org.wikimedia.metrics_platform;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.stream.Collectors;

import org.wikimedia.metrics_platform.event.EventProcessed;
import org.wikimedia.metrics_platform.json.GsonHelper;

import com.google.gson.Gson;

import lombok.extern.java.Log;

@Log
public class EventSenderDefault implements EventSender {

    private final Gson gson = GsonHelper.getGson();

    @Override
    public void sendEvents(URL baseUri, Collection<EventProcessed> events) throws IOException {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) baseUri.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            String agent = MetricsClient.METRICS_PLATFORM_VERSION + " Java";
            connection.setRequestProperty("User-Agent", "Metrics Platform Client/" + agent);
            connection.setDoOutput(true);

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), UTF_8))) {
                gson.toJson(events, writer);
                log.log(INFO, "Events sent: posted data to event logging!");
            }

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                log.log(WARNING, "Events NOT sent: response was " + status);
                String unsentEvents = gson.toJson(events);
                log.log(WARNING, "Unsent events: " + unsentEvents);
                throw new IOException("Could not send events, response status is not HTTP/OK, but is " + status);
            }

            if (log.isLoggable(FINE)) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8))) {
                    String result = br.lines().collect(Collectors.joining());
                    log.log(FINE, "Events sent: " + result);
                }
            }
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}
