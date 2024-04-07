package com.otel.person.service;

import com.otel.person.entity.Person;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final Tracer tracer;

    public PersonService(OpenTelemetry openTelemetry) {
        tracer = openTelemetry.getTracer(PersonService.class.getName(), "1.1.1");
    }

    public List<Person> getPeople() {
        Span span = tracer.spanBuilder("PersonService:getPeope")
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();
        Context.current().with(span);
        try (Scope scope = span.makeCurrent()) {
            return List.of(new Person("Serhat", "Yilmaz"));
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            Attributes eventAttributes = Attributes.of(
                    AttributeKey.stringKey("key"), "value",
                    AttributeKey.longKey("result"), 0L);

            span.addEvent("End Computation", eventAttributes);
            span.end();
        }
    }
}
