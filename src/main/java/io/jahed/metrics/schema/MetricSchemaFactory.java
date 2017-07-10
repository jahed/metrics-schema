package io.jahed.metrics.schema;

import com.github.wnameless.json.flattener.JsonFlattener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricSchemaFactory {

    public static MetricSchema create(String schemaPath) throws IOException {
        String schemaJson = getResourceAsString(schemaPath);
        Map<String, String> schemaMap = parseSchema(schemaJson);
        return new MetricSchema(schemaMap);
    }

    private static Map<String, String> parseSchema(String schemaJson) {
        return JsonFlattener
            .flattenAsMap(schemaJson)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> String.valueOf(e.getValue())
            ));
    }

    private static String getResourceAsString(String schemaPath) throws IOException {
        try {
            return new String(
                Files.readAllBytes(
                    Paths.get(
                        MetricSchema.class
                            .getResource(schemaPath)
                            .toURI()
                    )
                )
            );
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}
