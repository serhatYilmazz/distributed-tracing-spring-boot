package com.otel.person.controller;

import com.otel.person.entity.Person;
import com.otel.person.service.PersonService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/person")
@Slf4j
public class PersonController {

    private Tracer tracer;

    private final PersonService personService;

    public PersonController(OpenTelemetry openTelemetry, PersonService personService) {
        tracer = openTelemetry.getTracer(PersonController.class.getName(), "0.1.0");
        this.personService = personService;
    }

    @GetMapping("")
    public List<Person> getPeople(@RequestParam String id, HttpServletRequest request, HttpServletResponseWrapper r) {
        Span span = tracer.spanBuilder("PersonController:getPeople").setSpanKind(SpanKind.CLIENT).startSpan();
        span.setAttribute("http.method", "GET");
        span.setAttribute("http.url", "/person");

        try (Scope scope = span.makeCurrent()) {
            if (id.equals("1")) {
                throw new RuntimeException();
            }

            return personService.getPeople();
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, "Errorrr");
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
