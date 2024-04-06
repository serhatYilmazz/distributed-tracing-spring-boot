package com.otel.person.entity;

import lombok.Data;

@Data
public class Person {
    private String name;
    private String surname;

    public Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }
}
