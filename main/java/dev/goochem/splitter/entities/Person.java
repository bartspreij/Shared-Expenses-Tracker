package dev.goochem.splitter.entities;

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

    public String getName() {
        return name;
    }
    public long getId() { return id;}
    public Person getSecretSantaRecipient() {
        return secretSantaRecipient;
    }
    public void setSecretSantaRecipient(Person recipient) {
        secretSantaRecipient = recipient;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int compareTo(Person o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != person.id) return false;
        return getName() != null ? getName().equals(person.getName()) : person.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}




