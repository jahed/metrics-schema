package io.jahed.metrics.schema.validation;

import io.jahed.metrics.schema.validation.ValidationResult;

public interface ValidationListener<T extends ValidationResult> {
    void notify(T result);
}
