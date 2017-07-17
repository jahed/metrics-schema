package io.jahed.metrics.schema;

import com.codahale.metrics.Metric;
import com.github.wnameless.json.flattener.JsonFlattener;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class MetricSchemaFactory {

    public static MetricSchema create(String schemaPath) {
        String schemaJson = getResourceAsString(schemaPath);
        Map<String, Class<? extends Metric>> schemaMap = parseSchema(schemaJson);
        return new MetricSchema(schemaMap);
    }

    private static Map<String, Class<? extends Metric>> parseSchema(String schemaJson) {
        return flattenJson(schemaJson)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> {
                    String className = String.valueOf(e.getValue());
                    Class<Metric> expectedSubClass = Metric.class;

                    try {
                        Class<?> providedClass = Class.forName(className);
                        if(expectedSubClass.isAssignableFrom(providedClass)) {
                            return (Class<? extends Metric>)providedClass;
                        } else {
                            throw new MetricSchemaParseException(String.format(
                                "%s is not a %s.",
                                providedClass.getCanonicalName(),
                                expectedSubClass.getCanonicalName()
                            ));
                        }
                    } catch (ClassNotFoundException e1) {
                        throw new MetricSchemaParseException(
                            String.format("%s does not exist.",  className),
                            e1
                        );
                    }
                }
            ));
    }

    private static Map<String, Object> flattenJson(String json) {
        try {
            return JsonFlattener.flattenAsMap(json);
        } catch (RuntimeException e) {
            throw new MetricSchemaParseException(
                "Could not parse schema as JSON.",
                e
            );
        }
    }

    private static String getResourceAsString(String path) {
        try {
            return new String(
                Files.readAllBytes(
                    Paths.get(
                        MetricSchema.class
                            .getResource(path)
                            .toURI()
                    )
                )
            );
        } catch (Exception e) {
            throw new MetricSchemaParseException(
                String.format(
                    "Could not read schema at %s.",
                    path
                ),
                e
            );
        }
    }

}
