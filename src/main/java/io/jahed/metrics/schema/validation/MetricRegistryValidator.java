package io.jahed.metrics.schema.validation;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import io.jahed.metrics.schema.MetricSchema;
import io.jahed.metrics.schema.validation.ValidationResult.Failure;
import io.jahed.metrics.schema.validation.ValidationResult.Success;

import java.util.*;

/**
 * A MetricRegistryValidator can listen to a MetricRegistry for registered metrics and validate their name and types
 * against a {@link MetricSchema}.
 *
 * To perform any actions off these validation results, you'll need to add a {@link ValidationListener}.
 */
public class MetricRegistryValidator {

    private final RegistryListener registryListener;
    private final MetricSchema schema;
    private final MetricRegistry registry;
    private final Map<Class<? extends ValidationResult>, Set<ValidationListener>> resultListeners;
    private boolean started;

    public MetricRegistryValidator(MetricSchema schema, MetricRegistry registry) {
        this.schema = schema;
        this.registry = registry;
        this.registryListener = new RegistryListener();
        this.resultListeners = new HashMap<>();
        this.started = false;
    }

    /**
     * Starts notifying of validation results.
     * @return This validator.
     */
    public MetricRegistryValidator startValidating() {
        if(!started) {
            registry.addListener(registryListener);
            started = true;
        }
        return this;
    }

    /**
     * Stops notifying of validation results.
     * @return This validator.
     */
    public MetricRegistryValidator stopValidating() {
        if(started) {
            registry.removeListener(registryListener);
            started = false;
        }
        return this;
    }

    /**
     * Adds a {@link ValidationResult} listener. i.e. a listener for all results, both failures and successes.
     * Calling this method multiple times will add multiple listeners and will ignore ones already added.
     * @param listener The listener to notify.
     * @return This validator.
     */
    public MetricRegistryValidator onResult(ValidationListener<ValidationResult> listener) {
        return on(ValidationResult.class, listener);
    }

    /**
     * Adds a {@link ValidationResult.Success} listener. i.e. a listener for all successes.
     * Calling this method multiple times will add multiple listeners and will ignore ones already added.
     * @param listener The listener to notify.
     * @return This validator.
     */
    public MetricRegistryValidator onSuccess(ValidationListener<Success> listener) {
        return on(Success.class, listener);
    }

    /**
     * Adds a {@link ValidationResult.Failure} listener. i.e. a listener for all failures.
     * Calling this method multiple times will add multiple listeners and will ignore ones already added.
     * @param listener The listener to notify.
     * @return This validator.
     */
    public MetricRegistryValidator onFailure(ValidationListener<Failure> listener) {
        return on(Failure.class, listener);
    }

    /**
     * Stops notifying the given listener.
     * @param listener The listener to stop notifying.
     * @return This validator.
     */
    public MetricRegistryValidator off(ValidationListener<?> listener) {
        resultListeners.forEach((key, value) -> value.remove(listener));
        return this;
    }

    private <T extends ValidationResult> MetricRegistryValidator on(Class<T> resultType, ValidationListener<T> listener) {
        Set<ValidationListener> resultListeners = getListenersForResultType(resultType);
        resultListeners.add(listener);
        return this;
    }

    private <T extends ValidationResult> Set<ValidationListener> getListenersForResultType(Class<T> resultType) {
        return Optional.ofNullable(resultListeners.get(resultType))
            .orElseGet(() -> {
                HashSet<ValidationListener> typeListeners = new HashSet<>();
                resultListeners.put(resultType, typeListeners);
                return typeListeners;
            });
    }

    private void validateAndNotify(String name, Metric metric) {
        ValidationResult result = schema.validate(name, metric);
        resultListeners.entrySet().stream()
            .filter(e -> e.getKey().isInstance(result))
            .forEach(e -> e.getValue().forEach(l -> l.notify(result)));
    }

    private class RegistryListener implements MetricRegistryListener {
        @Override
        public void onMeterAdded(String name, Meter meter) {
            validateAndNotify(name, meter);
        }

        @Override
        public void onMeterRemoved(String s) {
        }

        @Override
        public void onGaugeAdded(String name, Gauge<?> gauge) {
            validateAndNotify(name, gauge);
        }

        @Override
        public void onGaugeRemoved(String s) {
        }

        @Override
        public void onCounterAdded(String name, Counter counter) {
            validateAndNotify(name, counter);
        }

        @Override
        public void onCounterRemoved(String s) {
        }

        @Override
        public void onHistogramAdded(String name, Histogram histogram) {
            validateAndNotify(name, histogram);
        }

        @Override
        public void onHistogramRemoved(String s) {
        }

        @Override
        public void onTimerAdded(String name, Timer timer) {
            validateAndNotify(name, timer);
        }

        @Override
        public void onTimerRemoved(String s) {
        }
    }

}
