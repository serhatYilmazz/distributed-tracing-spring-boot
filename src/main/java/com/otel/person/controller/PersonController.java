package com.otel.person.controller;

import com.otel.person.entity.Person;
import com.otel.person.service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/person")
@Slf4j
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("")
    public List<Person> getPeople(@RequestParam String id, HttpServletRequest request, HttpServletResponseWrapper r) {
        if (id.equals("1")) {
            throw new RuntimeException("id can not be 1");
        }

        return personService.getPeople();
    }

    @PostMapping("")
    public Person createPerson(@RequestBody Person person) {
        return personService.createPerson(person);
    }

    @GetMapping("/callOther/{id}")
    public String callOther(@PathVariable Integer id)  {
        RestTemplate restTemplate = new RestTemplate();
        personService.getPeople();
        if (id == 1) {
            return "-1";
        }
        return restTemplate.getForEntity("http://app1:8080/person/callOther/1", String.class).getBody();
    }
}
