package io.jahed.metrics.schema.validation;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import io.jahed.metrics.schema.MetricSchema;

import java.util.*;

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

    public MetricRegistryValidator startValidating() {
        if(!started) {
            registry.addListener(registryListener);
            started = true;
        }
        return this;
    }

    public MetricRegistryValidator stopValidating() {
        if(started) {
            registry.removeListener(registryListener);
            started = false;
        }
        return this;
    }

    public <T extends ValidationResult> MetricRegistryValidator on(Class<T> resultType, ValidationListener<T> listener) {
        Set<ValidationListener> resultListeners = getListenersForResultType(resultType);
        resultListeners.add(listener);
        return this;
    }

    public <T extends ValidationResult> MetricRegistryValidator off(Class<T> resultType, ValidationListener<T> listener) {
        Set<ValidationListener> resultListeners = getListenersForResultType(resultType);
        resultListeners.remove(listener);
        return this;
    }

    public MetricRegistryValidator off(ValidationListener<?> listener) {
        resultListeners.forEach((key, value) -> value.remove(listener));
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
