package com.splitter.repositories;

import com.splitter.entities.GroupOfPeople;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends CrudRepository<GroupOfPeople, Long> {
    boolean existsByName(String groupName);
    Optional<GroupOfPeople> findByName(String groupName);

    default void saveOrUpdate(GroupOfPeople group) {
        Optional<GroupOfPeople> fetchedGroup = findById(group.getId());

        if (fetchedGroup.isPresent()) {
            // Group with the same name already exists, update it
            GroupOfPeople existingGroup = fetchedGroup.get();
            existingGroup.addPeople(group.getPeople());
            save(existingGroup);
        } else {
            // Group doesn't exist, create a new one
            save(group);
        }
    }
}
