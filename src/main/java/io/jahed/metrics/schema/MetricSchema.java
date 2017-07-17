package io.jahed.metrics.schema;

import com.codahale.metrics.Metric;
import io.jahed.metrics.schema.validation.ValidationResult;

import java.util.Map;

import static io.jahed.metrics.schema.validation.ValidationResult.failure;
import static io.jahed.metrics.schema.validation.ValidationResult.success;

public class MetricSchema {

    private final Map<String, Class<? extends Metric>> schema;

    public MetricSchema(Map<String, Class<? extends Metric>> schema) {
        this.schema = schema;
    }

    public ValidationResult validate(String name, Metric metric) {
        Class<?> expectedClass = schema.get(name);

        if (expectedClass == null) {
            return failure(String.format(
                "Unknown metric name: %s",
                name
            ));
        }

        if (!expectedClass.isInstance(metric)) {
            return failure(String.format(
                "Metric %s was passed an invalid type %s. Expected %s.",
                name,
                metric.getClass().getCanonicalName(),
                expectedClass.getCanonicalName()
            ));
        }

        return success(
            String.format(
                "Metric %s of type %s is valid.",
                name,
                metric.getClass().getCanonicalName()
            )
        );
    }
}
