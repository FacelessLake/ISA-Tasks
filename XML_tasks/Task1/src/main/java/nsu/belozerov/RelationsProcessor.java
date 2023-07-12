package nsu.belozerov;

import java.util.*;

public class RelationsProcessor {

    private final People people;
    private final HashMap<String, String> nameIdMap = new HashMap<>();
    private final HashMap<String, List<String>> namesakes = new HashMap<>();
    private final HashMap<String, List<Person>> troubleMakers = new HashMap<>();

    RelationsProcessor(People people) {
        this.people = people;
    }

    public void resolveRelations() {
        stashedSafeMerge();
        troubleMakersResolve();
        resolveRelationsByID();
        resolveParamsByID();
        formalise();
    }

    private void resolveRelationsByID() {
        for (Map.Entry<String, Person> entry : people.getPeople()) {
            Person person = entry.getValue();

            boolean foundParents = true;
            boolean foundSiblings = true;
            LinkedHashSet<String> parents = new LinkedHashSet<>(person.getParentsSet());
            LinkedHashSet<String> siblings = new LinkedHashSet<>(person.getSiblingsSet());
            LinkedHashSet<String> toCheck = new LinkedHashSet<>();

            while (foundParents || foundSiblings) {
                for (String id : parents) {
                    Person parent = people.getPersonById(id);
                    if (parent != null) {
                        siblings.addAll(parent.getSonsSet());
                        siblings.addAll(parent.getDaughtersSet());

                        String parent2 = parent.getWife() != null ? parent.getWife() : parent.getHusband();
                        if (parent2 != null) {
                            toCheck.add(parent2);
                        }
                    }
                }
                foundParents = parents.addAll(toCheck);
                toCheck.clear();

                for (String id : siblings) {
                    Person sibling = people.getPersonById(id);
                    if (sibling != null) {
                        toCheck.addAll(sibling.getSiblingsSet());
                        foundParents = parents.addAll(sibling.getParentsSet());
                    }
                }
                foundSiblings = siblings.addAll(toCheck);
                toCheck.clear();
            }
            siblings.remove(person.getId());
            person.addSiblings(siblings);
            person.addParents(parents);
        }

    }

    private void nameTrimmerSetter(String fullName, Person person) {
        String[] nameArr = fullName.split("\\s+");
        person.setFirstName(nameArr[0]);
        person.setSurname(nameArr[1]);
    }

    private void resolveParamsByID() {
        for (Map.Entry<String, Person> entry : people.getPeople()) {
            Person person = entry.getValue();

            //Parents resolving
            HashSet<String> parents = person.getParentsSet();

            for (String parentId : parents) {
                if (person.getFather() != null) {
                    Person father = people.getPersonById(parentId);
                    if (father != null && Objects.equals(father.getGender(), "male")) {
                        nameTrimmerSetter(person.getFather(), father);
                    }
                    if (father != null && Objects.equals(father.getFullName(), person.getFather())) {
                        father.setGender("male");
                    }
                }

                if (person.getMother() != null) {
                    Person mother = people.getPersonById(parentId);
                    if (mother != null && Objects.equals(mother.getGender(), "female")) {
                        nameTrimmerSetter(person.getMother(), mother);
                    }
                    if (mother != null && Objects.equals(mother.getFullName(), person.getMother())) {
                        mother.setGender("female");
                    }
                }
            }
            ArrayList<Person> parentList = new ArrayList<>();
            for (String parentId : parents) {
                parentList.add(people.getPersonById(parentId));
            }
            if (parentList.size() > 1) {
                //Default case - father
                int parent1 = 0, parent2 = 1;

                if (Objects.equals(parentList.get(0).getGender(), "female")) {
                    parent1 = 1;
                    parent2 = 0;
                }

                if (parentList.get(parent1).getWife() == null && parentList.get(parent1).getHusband() == null) {
                    parentList.get(parent1).setSpouse(parentList.get(parent2).getFullName());
                    parentList.get(parent2).setSpouse(parentList.get(parent1).getFullName());
                }
                if (person.getFather() == null) {
                    person.setFather(parentList.get(parent1).getFullName());
                }
                if (person.getMother() == null) {
                    person.setMother(parentList.get(parent2).getFullName());
                }
            }

            //Partner resolving
            String partnerId = person.getWife() != null ? person.getWife() : person.getHusband();
            Person partner = people.getPersonById(partnerId);

            if (person.getSpouse() != null) {
                if (partner != null) {
                    nameTrimmerSetter(person.getSpouse(), partner);
                }
            }
            if (partner != null) {
                if (person.getFullName() != null) {
                    partner.setSpouse(person.getFullName());
                }
            }
        }
    }

    private void countPeople() {
        for (Map.Entry<String, Person> entry : people.getPeople()) {
            Person person = entry.getValue();
            int children = person.getChildrenSet().size();
            int siblings = person.getSiblingsSet().size();

            if (person.getChildrenNumber() == null) {
                person.setChildrenNumber(Integer.toString(children));
            }
            if (children < Integer.parseInt(person.getChildrenNumber())) {
                System.out.println(person.getId() + " missing children!");
            }

            if (person.getSiblingNumber() == null) {
                person.setSiblingNumber(Integer.toString(siblings));
            }
            if (siblings < Integer.parseInt(person.getSiblingNumber())) {
                System.out.println(person.getId() + " missing siblings!");
            }
        }
    }

    private void stashedSafeMerge() {
        namesakeCheck();

        ArrayList<Person> lost = new ArrayList<>();
        for (Person person : people.getIdLessPeopleList()) {
            String fullName = person.getFullName(); //no null check?
            //We ignore these guys for now

            if (namesakes.get(fullName) == null) {
                String id = nameIdMap.get(fullName);
                Person search = people.getPersonById(id);
                if (search != null) {
                    person.mergeToPerson(search);
                } else {
                    lost.add(person);
                }
            } else {
                if (troubleMakers.get(fullName) != null) {
                    troubleMakers.get(fullName).add(person);
                } else {
                    ArrayList<Person> list = new ArrayList<>();
                    list.add(person);
                    troubleMakers.put(fullName, list);
                }
            }
        }
        if (lost.size() == 0) {
            System.out.println("No loss!");
        }
    }

    private void stashedNamesakeCheck() {
        HashMap<String, List<Person>> relations = new HashMap<>();
        for (Person person : people.getIdLessPeopleList()) {
            String fullName = person.getFullName();
            if (namesakes.get(fullName) == null) {
                if (relations.get(fullName) != null) {
                    relations.get(fullName).add(person);
                } else {
                    ArrayList<Person> list = new ArrayList<>();
                    list.add(person);
                    relations.put(fullName, list);
                }
            }
        }
        for (Map.Entry<String, List<Person>> entry : relations.entrySet()) {
            List<Person> persons = entry.getValue();
            if (persons.size() > 1) {
                Person head = persons.get(0);
                for (int i = 1; i < persons.size(); i++) {
                    persons.get(i).mergeToCheck(head);
                }
            }
        }

        System.out.println("done!");
    }

    private void troubleMakersCheck() {
        for (Map.Entry<String, List<Person>> entry : troubleMakers.entrySet()) {
            List<Person> persons = entry.getValue();
            if (persons.size() > 1) {
                Person head = persons.get(0);
                for (int i = 1; i < persons.size(); i++) {
                    persons.get(i).mergeToCheck(head);
                }
            }
        }

        System.out.println("done!");
    }

    private void namesakeCheck() {
        for (Map.Entry<String, Person> personData : people.getPeople()) {
            Person person = personData.getValue();
            String fullName = person.getFullName();
            if (person.getFullName() != null) {
                if (nameIdMap.get(fullName) != null) {
                    if (namesakes.get(fullName) != null) {
                        namesakes.get(fullName).add(person.getId());
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(nameIdMap.get(fullName));
                        list.add(person.getId());
                        namesakes.put(fullName, list);
                    }
                } else {
                    nameIdMap.put(fullName, person.getId());
                }
            }
        }
//        System.out.println("done!");
    }

    private void troubleMakersResolve() {
        int counter = 0;
        for (Map.Entry<String, List<Person>> entry : troubleMakers.entrySet()) {
            List<Person> troubleMaker = entry.getValue();
            troubleMaker.sort((tm1, tm2) -> tm2.getNotNullParametersCount() - tm1.getNotNullParametersCount());

            counter += troubleMaker.size();
            for (Person falsePerson : troubleMaker) {
                for (String nameSakeId : namesakes.get(entry.getKey())) {
                    Person person = people.getPersonById(nameSakeId);
                    if (falsePerson.resolveConflictMergeTo(person)) {
                        falsePerson.mergeToPerson(person);
                        counter--;
                        break;
                    }
                }
            }
        }
        if (counter < 1) {
            System.out.println("All conflicts resolved!");
        }
    }

    private void formalise(){
        for (Map.Entry<String, Person> entry : people.getPeople()) {
            Person person = entry.getValue();

                for (String brotherFullName : person.getBrothersSet()){
                    person.addSibling(nameIdMap.get(brotherFullName));
                }
                for (String sisterFullName : person.getSistersSet()){
                    person.addSibling(nameIdMap.get(sisterFullName));
                }
                for (String childFullName : person.getChildrenSet()){
                    Person child = people.getPersonById(nameIdMap.get(childFullName));
                    if (Objects.equals(child.getGender(), "male")){
                        person.addSon(child.getId());
                    }
                    if (Objects.equals(child.getGender(), "female")){
                        person.addDaughter(child.getId());
                    }
                }
            if (person.getSiblingsSet().size() < person.getBrothersSet().size() + person.getSistersSet().size()){
                System.out.println(person.getId()+ " missing siblings!");
            }
            if (person.getChildrenSet().size() > person.getSonsSet().size() + person.getDaughtersSet().size()){
                System.out.println(person.getId()+ " missing children!");
            }
        }
    }
}
