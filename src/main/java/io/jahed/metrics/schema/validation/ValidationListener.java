package io.jahed.metrics.schema.validation;

/**
 * A Listener assigned to {@link MetricRegistryValidator} to be notified on
 * validation results.
 *
 * If you care about whether or not results are successes or failures, assign a listener for the individual types.
 *
 * @param <T> The type of {@link ValidationResult to listen to.}
 */
public interface ValidationListener<T extends ValidationResult> {
    void notify(T result);
}
