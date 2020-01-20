import org.apache.commons.csv.CSVRecord;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.Map;

public class JSON_Formatter {

    String key;
    String value;
    JsonObject jsonValue;

    JSON_Formatter(List<String> headers, CSVRecord values){
        key = values.get(0);
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        for (int i = 1; i < values.size(); i++){
            jsonBuilder.add(headers.get(i), values.get(i));
        }
        jsonValue = jsonBuilder.build();
        value = jsonValue.toString();
    }

    @Override
    public String toString() {
        return "JSON_Formatter{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
