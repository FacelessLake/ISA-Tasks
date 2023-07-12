package nsu.belozerov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class People {
    private final HashMap<String, Person> peopleHashMap = new HashMap<>();
    private final ArrayList<Person> idLessPeopleList = new ArrayList<>();

    public Set<Map.Entry<String, Person>> getPeople() {
        return peopleHashMap.entrySet();
    }

    public ArrayList<Person> getIdLessPeopleList() {
        return idLessPeopleList;
    }

    public void addPerson(String id, Person person) {
        peopleHashMap.put(id, person);
    }

    public void addIdLessPerson(Person person){
        idLessPeopleList.add(person);
    }

    public void removeIdLessPerson(Person person) {
        idLessPeopleList.remove(person);
    }

    public Person getPersonById(String id) {
        if (id != null){
            return peopleHashMap.get(id);
        }
        return null;
    }

    public Person getIdLessPerson(String firstName, String lastName) {
        for (Person person : idLessPeopleList) {
            if (person.getFirstName().equals(firstName) && person.getSurname().equals(lastName)) {
                return person;
            }
        }
        return null;
    }
}
