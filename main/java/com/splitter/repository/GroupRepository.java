package com.splitter.repository;

import com.splitter.entities.GroupOfPeople;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GroupRepository extends JpaRepository<GroupOfPeople, Integer> {
    boolean existsByName(String name);
    GroupOfPeople getByName(String name);
}
