package com.otel.person.controller;

import com.otel.person.entity.Person;
import com.otel.person.service.PersonService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
@Slf4j
public class PersonController {

    private Tracer tracer;

    private Meter meter;

    private final OpenTelemetry openTelemetry;

    private final PersonService personService;

    public PersonController(OpenTelemetry openTelemetry, PersonService personService) {
        tracer = openTelemetry.getTracer(PersonController.class.getName(), "0.1.0");
        meter = openTelemetry.meterBuilder(this.getClass().getName()).build();
        this.openTelemetry = openTelemetry;
        this.personService = personService;
    }

    @GetMapping("")
    public List<Person> getPeople(@RequestParam String id, HttpServletRequest request, HttpServletResponseWrapper r) {
        meter.counterBuilder("person.getPeople")
                .setDescription("Total number of getPeople")
                .build()
                .add(1);


        DoubleHistogram histogram = openTelemetry.getMeter("serhat-hist")
                .histogramBuilder("dice-kanka")
                .setUnit("points")
                .build();

        histogram.record(Double.parseDouble(id));

        if (id.equals("1")) {
            throw new RuntimeException("id can not be 1");
        }

        return personService.getPeople();
    }

    @PostMapping("")
    public Person createPerson(@RequestBody Person person) {
        return personService.createPerson(person);
    }
}
