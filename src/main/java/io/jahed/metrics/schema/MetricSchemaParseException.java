package io.jahed.metrics.schema;

public class MetricSchemaParseException extends RuntimeException {
    public MetricSchemaParseException(String message) {
        super(message);
    }

    public MetricSchemaParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
