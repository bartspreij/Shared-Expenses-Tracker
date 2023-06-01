package com.splitter.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GROUPS")
public class GroupOfPeople {

    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private List<Person> people = new ArrayList<>();

    @Column(name = "name")
    private String name;

    public GroupOfPeople() {}

    public GroupOfPeople(String name, List<Person> members) {
        this.name = name;
        this.people = members;
    }

    public List<Person> getPeople() {
        return people;
    }
}
