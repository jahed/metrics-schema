package io.jahed.metrics.schema;

/**
 * Thrown when parsing a MetricSchema JSON fails.
 */
public class MetricSchemaParseException extends RuntimeException {
    public MetricSchemaParseException(String message) {
        super(message);
    }

    public MetricSchemaParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
