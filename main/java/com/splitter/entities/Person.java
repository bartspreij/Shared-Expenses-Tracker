package com.splitter.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PEOPLE")
public class Person implements Comparable<Person>{

    @Id
    @Column(name = "person_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany(mappedBy = "people", fetch = FetchType.EAGER)
    private List<GroupOfPeople> groups = new ArrayList<>();

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name = "secret_santa_recipient_id")
    private Person secretSantaRecipient;

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Person o) {
        return this.name.compareTo(o.name);
    }

    public String getName() {
        return name;
    }

    public void setSecretSantaRecipient(Person recipient) {
        secretSantaRecipient = recipient;
    }

    public Person getSecretSantaRecipient() {
        return secretSantaRecipient;
    }
}




