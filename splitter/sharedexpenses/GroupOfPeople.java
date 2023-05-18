package splitter.sharedexpenses;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collections;

@Entity
public class GroupOfPeople {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionId;

    private ArrayList<Person> people;
    private String name;

    public GroupOfPeople(String name, ArrayList<Person> people) {
        this.name = name;
        this.people = people;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Person> getPeople() {
        Collections.sort(people);
        return people;
    }

    public int getSizeOfGroup () {
        return people.size();
    }

    public void setName(String name) {
        this.name = name;
    };

    public void setListOfPeople(ArrayList<Person> listOfpPeople) {
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
