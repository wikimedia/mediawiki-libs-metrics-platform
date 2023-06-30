package org.wikimedia.metrics_platform;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.INFO;

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
            }

            int status = connection.getResponseCode();
            if (status < 300) {
                log.log(INFO, "Sent " + events.size() + " events successfully.");
            } else {
                String message = connection.getResponseMessage();
                if (status < 500) {
                    // attempt to read the error message body and pass it back.
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), UTF_8))) {
                        message += ": " + br.lines().collect(Collectors.joining());
                    }
                }
                throw new IOException(message);
            }
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}
