package io.jahed.metrics.schema;

import com.codahale.metrics.Metric;
import io.jahed.metrics.schema.validation.ValidationResult;

import java.util.Map;

import static io.jahed.metrics.schema.validation.ValidationResult.failure;
import static io.jahed.metrics.schema.validation.ValidationResult.success;

/**
 * A MetricSchema can be used to validate Metric names against their types.
 *
 * To create a MetricSchema, the current recommendation is to use
 * {@link io.jahed.metrics.schema.MetricSchemaFactory#createFromResource(String)}.
 *
 * @see io.jahed.metrics.schema.MetricSchemaFactory#createFromResource(String)
 * @author Jahed Ahmed
 */
public class MetricSchema {

    private final Map<String, Class<? extends Metric>> schema;

    /**
     * Creates a MetricSchema.
     * @param schema The schema, mapping Metric names to Metric types.
     */
    public MetricSchema(Map<String, Class<? extends Metric>> schema) {
        this.schema = schema;
    }

    /**
     * Validates a Metric name and type against this schema.
     * @param name The name of the Metric.
     * @param metric The Metric assigned to the name.
     * @return The result of the validation.
     */
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
