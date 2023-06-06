package org.wikimedia.metrics_platform.context;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.wikimedia.metrics_platform.json.GsonHelper;

import com.google.gson.Gson;

class CustomDataTest {

    @Test void testFormatCustomDataByCustomSerialization() {
        Set<CustomData> customDataSet = getTestCustomData();
        // Sort the customDataSet for test assertions.
        List<CustomData> sortedCustomDataList = customDataSet.stream()
            .sorted(Comparator.comparing(CustomData::getName))
            .collect(toList());

        List<String> jsonStringList = new ArrayList<>();

        for (CustomData customData : sortedCustomDataList) {
            Gson gson = GsonHelper.getGson();
            jsonStringList.add(gson.toJson(customData));

        }
        String jsonCustomData = String.join(",", jsonStringList);

        assertThat(jsonCustomData)
            .isEqualTo("{\"action\":{\"data_type\":\"string\",\"value\":\"click\"}}," +
                "{\"is_full_width\":{\"data_type\":\"boolean\",\"value\":\"true\"}}," +
                "{\"screen_size\":{\"data_type\":\"number\",\"value\":\"1080\"}}");
    }

    private Set<CustomData> getTestCustomData() {
        Set<CustomData> customDataSet = new HashSet<>();
        CustomData customData1 = new CustomData("screen_size", CustomDataType.NUMBER, "1080");
        CustomData customData2 = new CustomData("is_full_width", CustomDataType.BOOLEAN, "true");
        CustomData customData3 = new CustomData("action", CustomDataType.STRING, "click");
        customDataSet.add(customData1);
        customDataSet.add(customData2);
        customDataSet.add(customData3);
        return customDataSet;
    }
}
