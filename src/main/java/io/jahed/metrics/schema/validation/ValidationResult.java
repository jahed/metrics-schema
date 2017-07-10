package io.jahed.metrics.schema.validation;

public interface ValidationResult {
    String getMessage();

    interface Success extends ValidationResult {}
    interface Failure extends ValidationResult {}

    static Success success(String message) {
        return () -> message;
    }

    static Failure failure(String message) {
        return () -> message;
    }
}
