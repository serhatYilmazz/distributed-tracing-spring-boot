package com.otel.person.service;

import com.otel.person.entity.Person;
import com.otel.person.repository.PersonRepository;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    private final Tracer tracer;

    public PersonService(OpenTelemetry openTelemetry, PersonRepository personRepository) {
        tracer = openTelemetry.getTracer(PersonService.class.getName(), "1.1.1");
        this.personRepository = personRepository;
    }

    public List<Person> getPeople() {
        return personRepository.findAll();
    }

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }
}
