package com.otel.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.opentelemetry.sdk.metrics.InstrumentSelector;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.View;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenTelemetryConfiguration {

    @Value("${spring.application.name}")
    private String appName;


        @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
                .toBuilder()
                .put(ResourceAttributes.SERVICE_NAME, appName)
                .put(ResourceAttributes.SERVICE_VERSION, "1.0.0")
                .put(ResourceAttributes.K8S_POD_UID, "1234")
                .build();


        OtlpHttpSpanExporter otlpHttpSpanExporter = OtlpHttpSpanExporter.builder()
                .setEndpoint("http://localhost:4318")
                .build();

        /*
            JAVA_TOOL_OPTIONS=-javaagent:/Users/serhatyilmaz/Workspace/otel/opentelemetry-javaagent.jar
            OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
            OTEL_SERVICE_NAME=otel
         */
        OtlpHttpMetricExporter otlpHttpMetricExporter = OtlpHttpMetricExporter.builder()
                .setEndpoint("http://localhost:4318/v1/metrics")
                .build();
        OtlpHttpLogRecordExporter otlpHttpLogRecordExporter = OtlpHttpLogRecordExporter.builder()
                .setEndpoint("http://localhost:4318/v1/logs")
                .build();
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
//                .addSpanProcessor(SimpleSpanProcessor.create(otlpHttpSpanExporter))
                .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
                .setResource(resource)
                .build();

        SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.create(otlpHttpMetricExporter))
                .registerMetricReader(PeriodicMetricReader.create(LoggingMetricExporter.create()))
                .setResource(resource)
                .build();

        SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
                .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(SystemOutLogRecordExporter.create()).build()
                )
                .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(otlpHttpLogRecordExporter).build()
                )
                .setResource(resource)
                .build();

        SdkMeterProvider.builder()
                .registerView(
                        InstrumentSelector.builder()
                                .setName("serhat-hist")
                                .build(),
                        View.builder()
                                .setAggregation(Aggregation.explicitBucketHistogram(List.of(5.0, 10.0, 15.0, 20.0)))
                                .build()
                );

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setMeterProvider(sdkMeterProvider)
                .setLoggerProvider(sdkLoggerProvider)
                .setPropagators(ContextPropagators.create(TextMapPropagator.composite(W3CTraceContextPropagator.getInstance(), W3CBaggagePropagator.getInstance())))
                .build();

        return openTelemetry;
    }

}
