# Metrics Schema

Define your [Metrics](https://github.com/dropwizard/metrics) in a JSON file so you can keep track of what you're 
expecting and what's actually going out.

## Installation

*Maven Central access is pending.*

## Usage

Create a JSON to define your schema.

```json
{
  "application": {
    "metric-meter": "com.codahale.metrics.Meter",
    "metric-timer": "com.codahale.metrics.Timer"
  }
}
```

Here we're saying that we expect 2 metrics to be used. A `Meter` called `application.metric-meter` and a `Timer` called 
`application.metric-timer`.

The Metric must be a full canonical name of a class which implements `com.codahale.metrics.Metric`.

Once you've defined your schema, you can use it to validator your `MetricRegistry`.

```java
MetricRegistry registry = new MetricRegistry();
MetricSchema schema = MetricSchemaFactory.createFromResource("/resource/path/to/your/schema.json");
new MetricRegistryValidator(schema, registry)
    .onSuccess(success -> logger.debug(success.getMessage()))
    .onFailure(failure -> logger.warn(failure.getMessage()))
    .startValidating();
```

If you ever want to stop validating, you can do the following:

```java
validator.stopValidating();
```

## License

See LICENSE file.