/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author filip
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Comparable<Person> {

    private static final String DEL = ",";

    @XmlAttribute
    private int id;

    @XmlElement(name = "firstname")
    private String firstName;

    @XmlElement(name = "lastname")
    private String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person() {
    }

    public Person(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static Set<Person> getPersonsFromData(String data) {

        Set persons = new TreeSet<>();

        String[] items = data.split(DEL);

        for (String item : items) {
            Person currentPerson = new Person();

            String[] splitName = item.trim().split(" ");

            currentPerson.firstName = splitName[0].trim();

            StringBuilder lastNameBuilder = new StringBuilder();

            for (int i = 1; i < splitName.length; i++) {
                lastNameBuilder.append(splitName[i]);
                lastNameBuilder.append(" ");
            }

            currentPerson.lastName = lastNameBuilder.toString().trim();

            persons.add(currentPerson);
        }

        return persons;

    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.firstName);
        hash = 67 * hash + Objects.hashCode(this.lastName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Person o) {
        return this.firstName.toLowerCase().compareTo(o.firstName.toLowerCase());
    }

}
