package com.otel.person.controller;

import com.otel.person.entity.Person;
import com.otel.person.service.PersonService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
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

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/person")
@Slf4j
public class PersonController {

    private Tracer tracer;

    private Meter meter;

    private final PersonService personService;

    public PersonController(OpenTelemetry openTelemetry, PersonService personService) {
        tracer = openTelemetry.getTracer(PersonController.class.getName(), "0.1.0");
        meter = openTelemetry.meterBuilder(this.getClass().getName()).build();
        this.personService = personService;
    }

    @GetMapping("")
    public List<Person> getPeople(@RequestParam String id, HttpServletRequest request, HttpServletResponseWrapper r) {
        meter.counterBuilder("person.getPeople")
                .setDescription("Total number of getPeople")
                .build()
                .add(1);

        LongHistogram histogram = meter.histogramBuilder("dice-lib.rolls")
                .ofLongs() // Required to get a LongHistogram, default is DoubleHistogram
                .setDescription("A distribution of the value of the rolls.")
//                .setExplicitBucketBoundariesAdvice(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L))
                .setUnit("points")
                .build();

        histogram.record(7);

        return personService.getPeople();
    }
}
