package io.jahed.metrics.schema;

import com.codahale.metrics.MetricRegistry;
import io.jahed.metrics.schema.validation.MetricRegistryValidator;
import io.jahed.metrics.schema.validation.ValidationListener;
import io.jahed.metrics.schema.validation.ValidationResult;
import io.jahed.metrics.schema.validation.ValidationResult.Failure;
import io.jahed.metrics.schema.validation.ValidationResult.Success;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MetricSchemaValidAcceptanceTest {

    private static final String VALID_SCHEMA_PATH = "/schema/valid-schema.json";
    private static final String VALID_METER_NAME = "application.metric-meter";
    private static final String VALID_TIMER_NAME = "application.metric-timer";
    private static final String INVALID_METRIC_NAME = "application.doesnt.exist";

    @Mock
    private ValidationListener<ValidationResult> resultListener;

    @Mock
    private ValidationListener<Failure> failureListener;

    @Mock
    private ValidationListener<Success> successListener;

    private final MetricRegistry registry = new MetricRegistry();

    @Test
    public void shouldNotifyListenersOfInvalidMetricNames() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);
        new MetricRegistryValidator(schema, registry)
            .onFailure(failureListener)
            .startValidating();

        registry.meter(INVALID_METRIC_NAME);

        verify(failureListener, times(1)).notify(any(Failure.class));
    }

    @Test
    public void shouldNotifyListenersOfInvalidMetricTypes() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);
        new MetricRegistryValidator(schema, registry)
            .onFailure(failureListener)
            .startValidating();

        registry.timer(VALID_METER_NAME);

        verify(failureListener, times(1)).notify(any(Failure.class));
    }

    @Test
    public void shouldNotNotifyRemovedListeners() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);
        new MetricRegistryValidator(schema, registry)
            .onFailure(failureListener)
            .off(failureListener)
            .startValidating();

        registry.meter(INVALID_METRIC_NAME);

        verify(failureListener, never()).notify(any());
    }

    @Test
    public void shouldNotNotifyFailureListenersOnSuccess() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);
        new MetricRegistryValidator(schema, registry)
            .onFailure(failureListener)
            .startValidating();

        registry.meter(VALID_METER_NAME);

        verify(failureListener, never()).notify(any());
    }

    @Test
    public void shouldNotNotifySuccessListenersOnFailure() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);
        new MetricRegistryValidator(schema, registry)
            .onSuccess(successListener)
            .startValidating();

        registry.meter(INVALID_METRIC_NAME);

        verify(successListener, never()).notify(any());
    }

    @Test
    public void shouldNotifySuccessListenersOnSuccess() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);

        new MetricRegistryValidator(schema, registry)
            .onSuccess(successListener)
            .startValidating();

        registry.meter(VALID_METER_NAME);

        verify(successListener, times(1)).notify(any());
    }

    @Test
    public void shouldNotNotifyListenersForRepeatedMetrics() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);
        new MetricRegistryValidator(schema, registry)
            .onSuccess(successListener)
            .startValidating();

        registry.meter(VALID_METER_NAME);
        registry.meter(VALID_METER_NAME);

        verify(successListener, times(1)).notify(any());
    }

    @Test
    public void shouldNotifySuccessListenersForEveryNewMetric() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);
        new MetricRegistryValidator(schema, registry)
            .onSuccess(successListener)
            .startValidating();

        registry.meter(VALID_METER_NAME);
        registry.timer(VALID_TIMER_NAME);

        verify(successListener, times(2)).notify(any());
    }

    @Test
    public void shouldNotifyResultListenersForEveryNewMetric() throws Exception {
        MetricSchema schema = MetricSchemaFactory.createFromResource(VALID_SCHEMA_PATH);
        new MetricRegistryValidator(schema, registry)
            .onResult(resultListener)
            .startValidating();

        registry.meter(VALID_METER_NAME);
        registry.meter(INVALID_METRIC_NAME);

        verify(resultListener, times(2)).notify(any());
    }

}