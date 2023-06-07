package org.wikimedia.metrics_platform.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.json.GsonHelper;

import com.google.gson.Gson;

class CustomDataTest {

    @Test void testFormatCustomDataByCustomSerialization() {
        Map<String, CustomData> customData = getTestCustomData();

        Gson gson = GsonHelper.getGson();
        String jsonCustomData = gson.toJson(customData);

        assertThat(jsonCustomData)
            .isEqualTo("{\"is_full_width\":{\"data_type\":\"boolean\",\"value\":\"true\"}," +
                "\"action\":{\"data_type\":\"string\",\"value\":\"click\"}," +
                "\"screen_size\":{\"data_type\":\"number\",\"value\":\"1080\"}}");
    }

    private Map<String, CustomData> getTestCustomData() {
        Map<String, CustomData> customData = new HashMap<String, CustomData>();
        customData.put("is_full_width", new CustomData(CustomDataType.BOOLEAN, "true"));
        customData.put("action", new CustomData(CustomDataType.STRING, "click"));
        customData.put("screen_size", new CustomData(CustomDataType.NUMBER, "1080"));
        return customData;
    }
}
