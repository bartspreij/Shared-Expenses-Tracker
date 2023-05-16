package splitter.sharedexpenses;

import java.util.Objects;

public class Person implements Comparable<Person> {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName () {
        return name;
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
