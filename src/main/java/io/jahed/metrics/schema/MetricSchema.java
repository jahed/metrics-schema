package io.jahed.metrics.schema;

import com.codahale.metrics.Metric;
import io.jahed.metrics.schema.validation.ValidationResult;

import java.util.Map;

import static io.jahed.metrics.schema.validation.ValidationResult.failure;
import static io.jahed.metrics.schema.validation.ValidationResult.success;

public class MetricSchema {

    private final Map<String, String> schema;

    public MetricSchema(Map<String, String> schema) {
        this.schema = schema;
    }

    public ValidationResult validate(String name, Metric metric) {
        String expectedClassName = schema.get(name);

        if (expectedClassName == null) {
            return failure(String.format(
                "Unknown metric name: %s",
                name
            ));
        }

        String className = metric.getClass().getSimpleName();

        if (!className.equals(expectedClassName)) {
            return failure(String.format(
                "Metric %s was passed an invalid type %s",
                name,
                className
            ));
        }

        return success(String.format("Metric %s of type %s is valid.", name, className));
    }
}
