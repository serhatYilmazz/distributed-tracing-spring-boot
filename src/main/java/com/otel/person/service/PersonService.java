package com.otel.person.service;

import com.otel.person.entity.Person;
import com.otel.person.repository.PersonRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    private Tracer tracer;

    private final PersonRepository personRepository;


    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getPeople() {
        Span span = tracer.spanBuilder("db-span").startSpan();
        List<Person> all = personRepository.findAll();
        span.end();
        return all;
    }

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }
}
