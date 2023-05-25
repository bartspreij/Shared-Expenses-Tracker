package com.bartspreij.splitter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.bartspreij.splitter.model.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    Person findByName(String name);
}
