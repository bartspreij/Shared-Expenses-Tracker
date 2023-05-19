package splitter.sharedexpenses;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
public class GroupOfPeople {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ElementCollection
    @OneToMany(fetch = FetchType.EAGER)
    private List<Person> people;

    private String name;

    public GroupOfPeople() {}

    public GroupOfPeople(String name, List<Person> people) {
        this.name = name;
        this.people = people;
    }

    public String getName() {
        return name;
    }

    public List<Person> getPeople() {
        Collections.sort(people);
        return people;
    }

    public int getSizeOfGroup () {
        return people.size();
    }

    public void setName(String name) {
        this.name = name;
    };

    public void setListOfPeople(List<Person> listOfpPeople) {
        this.people = listOfpPeople;
    }

    public void add(Person person) {
        people.add(person);
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
