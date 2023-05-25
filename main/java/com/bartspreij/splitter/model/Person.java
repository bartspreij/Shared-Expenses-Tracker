package com.bartspreij.splitter.model;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "PEOPLE")
public class Person implements Comparable<Person> {

    @Id
    @Column(name = "person_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(mappedBy = "people")
    private List<GroupOfPeople> groups;

    @Column(unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "secret_santa_recipient_id")
    private Person secretSantaRecipient;

    public Person() {};

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() { return id; }

    public Person getSecretSantaRecipient() {
        return secretSantaRecipient;
    }

    public void setSecretSantaRecipient(Person p) {
        secretSantaRecipient = p;
    }

    @Override
    public String toString () {
        return getName();
    }

    @Override
    public int compareTo(Person p) {
        return getName().compareTo(p.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
