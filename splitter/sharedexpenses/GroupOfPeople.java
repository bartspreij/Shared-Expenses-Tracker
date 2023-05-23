package splitter.sharedexpenses;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@Entity
@Table
public class GroupOfPeople {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany
    @JoinColumn(name = "group_id")
    private List<Person> people = new ArrayList<>();

    private String name;

    public GroupOfPeople() {}

    public GroupOfPeople(String name, List<Person> people) {
        this.name = name;
        this.people = people;
    }

    public long getId() {
        return id;
    }

    public List<Person> getPeople() {
        Collections.sort(people);
        return people;
    }

    public int getSizeOfGroup () {
        return people.size();
    }

    public void add(Person person) {
        people.add(person);
    }

    public void addPeople(List<Person> peopleToAdd) {
        people.addAll(peopleToAdd);
    }

    public void remove(Person person) {
        people.removeIf(p -> p.equals(person));
    }

    public void printGroup () {
        if (people.isEmpty()) {
            System.out.println("Group is empty");
        }

        Collections.sort(people);
        for (Person person : people) {
            System.out.println(person);
        }
    }

}
