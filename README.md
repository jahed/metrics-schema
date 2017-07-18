# Metrics Schema

[![Travis](https://img.shields.io/travis/jahed/metrics-schema.svg)](https://travis-ci.org/jahed/metrics-schema)
[![Maven Central](https://img.shields.io/maven-central/v/io.jahed/metrics-schema.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22io.jahed%22%20AND%20a%3A%22metrics-schema%22)
[![Javadocs](https://www.javadoc.io/badge/io.jahed/metrics-schema.svg)](https://www.javadoc.io/doc/io.jahed/metrics-schema)

Define your [Metrics](https://github.com/dropwizard/metrics) in a JSON file so you can keep track of what you're 
expecting and what's actually going out.

## Installation

### Maven

Add this dependency to your `pom.xml`. Update the `version` to the one you want.

```xml
<dependency>
    <groupId>io.jahed</groupId>
    <artifactId>metrics-schema</artifactId>
    <version>1.0</version>
</dependency>
```

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

Once you've defined your schema, you can use it to validate your `MetricRegistry`.

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
