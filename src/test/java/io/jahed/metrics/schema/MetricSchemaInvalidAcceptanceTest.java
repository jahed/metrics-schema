package io.jahed.metrics.schema;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetricSchemaInvalidAcceptanceTest {

    private static final String NON_EXISTENT_SCHEMA_PATH = "/schema/invalid-schema-does-not-exist";
    private static final String NOT_JSON_SCHEMA_PATH = "/schema/invalid-schema-not-json.txt";
    private static final String UNKNOWN_TYPE_SCHEMA_PATH = "/schema/invalid-schema-unknown-type.json";
    private static final String NON_METRIC_TYPE_SCHEMA_PATH = "/schema/invalid-schema-non-metric-type.json";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldNotParseWhenNotFound() throws Exception {
        exception.expect(MetricSchemaParseException.class);
        exception.expectMessage(String.format("Could not read schema at %s.", NON_EXISTENT_SCHEMA_PATH));

        MetricSchemaFactory.createFromResource(NON_EXISTENT_SCHEMA_PATH);
    }

    @Test
    public void shouldNotParseWhenNotJson() throws Exception {
        exception.expect(MetricSchemaParseException.class);
        exception.expectMessage("Could not parse schema as JSON.");

        MetricSchemaFactory.createFromResource(NOT_JSON_SCHEMA_PATH);
    }

    @Test
    public void shouldNotParseWithUnknownMetricType() throws Exception {
        exception.expect(MetricSchemaParseException.class);
        exception.expectMessage("com.example.DoesNotExist does not exist.");

        MetricSchemaFactory.createFromResource(UNKNOWN_TYPE_SCHEMA_PATH);
    }

    @Test
    public void shouldNotParseWithNonMetricType() throws Exception {
        exception.expect(MetricSchemaParseException.class);
        exception.expectMessage("java.lang.String is not a com.codahale.metrics.Metric.");

        MetricSchemaFactory.createFromResource(NON_METRIC_TYPE_SCHEMA_PATH);
    }

}