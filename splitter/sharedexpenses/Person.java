package splitter.sharedexpenses;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Person implements Comparable<Person> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "secret_santa_recipient")
    private Person secretSantaRecipient;

    public Person() {};

    public Person(String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

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
        return Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
