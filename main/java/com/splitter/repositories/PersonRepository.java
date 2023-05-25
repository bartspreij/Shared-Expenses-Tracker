package com.splitter.repositories;

import com.splitter.entities.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    Person findByName(String name);
    boolean existsByName(String name);

    default void saveOrUpdate(List<Person> newPeople) {
        for (Person p : newPeople) {
            if (findByName(p.getName()) == null) { // add if person is new
                save(p);
            }
        }
    }
}
