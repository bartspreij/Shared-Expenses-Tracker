package splitter.sharedexpenses;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "GROUPS")
public class GroupOfPeople {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    @JoinTable(
            name = "GROUP_PERSON",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> people = new ArrayList<>();

    @Column(unique = true)
    private String name;

    public GroupOfPeople() {}

    public GroupOfPeople(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public List<Person> getPeople() {
        return people;
    }

    public int getSizeOfGroup() {
        return people.size();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void add(Person person) {
        people.add(person);
    }

    public void addPeople(List<Person> peopleToAdd) {
        people.addAll(peopleToAdd);
    }

    public void setPeople(List<Person> peopleToAdd) {
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
