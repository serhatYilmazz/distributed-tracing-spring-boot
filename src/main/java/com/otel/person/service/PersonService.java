package com.otel.person.service;

import com.otel.person.entity.Person;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
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
        Span span = tracer.spanBuilder("PersonService:getPeope").startSpan();
        Context.current().with(span);
        try (Scope scope = span.makeCurrent()) {
            return List.of(new Person("Serhat", "Yilmaz"));
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
