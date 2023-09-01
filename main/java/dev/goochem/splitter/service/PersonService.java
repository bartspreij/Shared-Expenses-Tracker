
package dev.goochem.splitter.service;

import dev.goochem.splitter.entities.Person;
import dev.goochem.splitter.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private final PersonRepository repository;

    @Autowired
    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public Person getOrAdd(String name) {
        Person person = repository.getByName(name);
        return person == null ? repository.save(new Person(name)) : person;
    }
}