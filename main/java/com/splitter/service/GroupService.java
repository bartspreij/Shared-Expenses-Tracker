package com.splitter.service;

import com.splitter.entities.GroupOfPeople;
import com.splitter.entities.Person;
import com.splitter.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository repository;

    @Autowired
    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    public List<String> getMembers(String groupName) {
        return repository.getByName(groupName).getPeople().stream()
                .map(Person::getName)
                .collect(Collectors.toList());
    }

    public String showMembers(String groupName) {
        return repository.getByName(groupName).getPeople().stream()
                .map(Person::getName)
                .sorted()
                .collect(Collectors.joining("\n"));
    }

    public boolean exists(String groupName) {
        return repository.existsByName(groupName);
    }

    public GroupOfPeople getGroup(String groupName) {
        return repository.getByName(groupName);
    }

    @Transactional
    public void createNewGroup(String groupName, List<Person> members) {
        GroupOfPeople group = repository.getByName(groupName);
        if (group != null) {
            deleteGroup(group);
        }
        repository.save(new GroupOfPeople(groupName, members));
    }

    @Transactional
    public void deleteGroup(GroupOfPeople group) {
        repository.delete(group);
    }

    @Transactional
    public void addMembers(String groupName, List<Person> persons) {
        repository.getByName(groupName).getPeople().addAll(persons);
    }

    @Transactional
    public void removeMembers(String groupName, List<Person> persons) {
        repository.getByName(groupName).getPeople()
                .removeIf(person -> persons.stream().map(Person::getName)
                        .toList()
                        .contains(person.getName()));
    }
}