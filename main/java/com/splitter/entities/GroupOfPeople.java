package com.splitter.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "GROUPS")
public class GroupOfPeople {

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Set<Person> people = new HashSet<>();

    @Column(name = "name")
    private String name;

    public GroupOfPeople() {}

    public GroupOfPeople(String name, Set<Person> members) {
        this.name = name;
        this.people = members;
    }

    public Set<Person> getPeople() {
        return people;
    }
}
