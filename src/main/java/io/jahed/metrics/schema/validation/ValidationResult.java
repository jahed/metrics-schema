package io.jahed.metrics.schema.validation;

/**
 * A result from the {@link MetricRegistryValidator}. Can be a {@link Success} or {@link Failure}.
 */
public interface ValidationResult {
    /**
     * @return A message describing the result.
     */
    String getMessage();

    interface Success extends ValidationResult {}
    interface Failure extends ValidationResult {}

    /**
     * Convenience method for creating a {@link Success} result.
     * @param message A message describing the result.
     * @return The result.
     */
    static Success success(String message) {
        return () -> message;
    }

    /**
     * Convenience method for creating a {@link Failure} result.
     * @param message A message describing the result.
     * @return The result.
     */
    static Failure failure(String message) {
        return () -> message;
    }
}
