package com.splitter.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "PEOPLE")
public class Person {

    @Id
    @Column(name = "person_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToMany(mappedBy = "people", cascade = {CascadeType.MERGE})
    private List<GroupOfPeople> groups;

    @Column(unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "secret_santa_recipient_id")
    private Person secretSantaRecipient;

    public Person(String name) {
        this.name = name;
    }
}




