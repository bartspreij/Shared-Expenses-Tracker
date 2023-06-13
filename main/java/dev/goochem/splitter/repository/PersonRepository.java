package dev.goochem.splitter.repository;

import dev.goochem.splitter.entities.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    Person getByName(String name);
}
