package nsu.belozerov;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class Person {
    private final HashMap<String, String> param = new HashMap<>();
    //    private String id, firstName, lastName, gender, spouse, wife, husband, father, mother, siblingNumber, childrenNumber;
    private HashSet<String> parentsSet, childrenSet, siblingsSet, daughtersSet, sonsSet, brothersSet, sistersSet;

    public Person() {
        this.parentsSet = new HashSet<>();
        this.childrenSet = new HashSet<>();
        this.siblingsSet = new HashSet<>();
        this.daughtersSet = new HashSet<>();
        this.sonsSet = new HashSet<>();
        this.brothersSet = new HashSet<>();
        this.sistersSet = new HashSet<>();
    }

    public String getId() {
        return param.get("id");
    }

    public void setId(String id) {
        param.put("id", id);
    }

    public String getFirstName() {
        return param.get("firstName");
    }

    public void setFirstName(String firstName) {
        param.put("firstName", firstName);
    }

    public String getSurname() {
        return param.get("surname");
    }

    public void setSurname(String surname) {
        param.put("surname", surname);
    }

    public String getGender() {
        return param.get("gender");
    }

    public void setGender(String gender) {
        param.put("gender", gender);
    }

    public String getSpouse() {
        return param.get("spouse");
    }

    public void setSpouse(String spouse) {
        param.put("spouse", spouse);
    }

    public String getWife() {
        return param.get("wife");
    }

    public void setWife(String wife) {
        param.put("wife", wife);
    }

    public String getHusband() {
        return param.get("husband");
    }

    public void setHusband(String husband) {
        param.put("husband", husband);
    }

    public String getFather() {
        return param.get("father");
    }

    public void setFather(String father) {
        param.put("father", father);
    }

    public String getMother() {
        return param.get("mother");
    }

    public void setMother(String mother) {
        param.put("mother", mother);
    }

    public String getSiblingNumber() {
        return param.get("siblingNumber");
    }

    public void setSiblingNumber(String siblingNumber) {
        param.put("siblingNumber", siblingNumber);
    }

    public String getChildrenNumber() {
        return param.get("childrenNumber");
    }

    public void setChildrenNumber(String childrenNumber) {
        param.put("childrenNumber", childrenNumber);
    }

    public HashSet<String> getParentsSet() {
        return parentsSet;
    }

    public void setParentsSet(HashSet<String> parentsSet) {
        this.parentsSet = parentsSet;
    }

    public HashSet<String> getChildrenSet() {
        return childrenSet;
    }

    public void setChildrenSet(HashSet<String> childrenSet) {
        this.childrenSet = childrenSet;
    }

    public HashSet<String> getSiblingsSet() {
        return siblingsSet;
    }

    public void setSiblingsSet(HashSet<String> siblingsSet) {
        this.siblingsSet = siblingsSet;
    }

    public HashSet<String> getDaughtersSet() {
        return daughtersSet;
    }

    public void setDaughtersSet(HashSet<String> daughtersSet) {
        this.daughtersSet = daughtersSet;
    }

    public HashSet<String> getSonsSet() {
        return sonsSet;
    }

    public void setSonsSet(HashSet<String> sonsSet) {
        this.sonsSet = sonsSet;
    }

    public HashSet<String> getBrothersSet() {
        return brothersSet;
    }

    public void setBrothersSet(HashSet<String> brothersSet) {
        this.brothersSet = brothersSet;
    }

    public HashSet<String> getSistersSet() {
        return sistersSet;
    }

    public void setSistersSet(HashSet<String> sistersSet) {
        this.sistersSet = sistersSet;
    }

    public void addParent(String parent) {
        this.parentsSet.add(parent);
    }

    public void addParents(HashSet<String> parentsSet) {
        this.parentsSet.addAll(parentsSet);
    }

    public void addChild(String child) {
        this.childrenSet.add(child);
    }

    public void addSibling(String sibling) {
        this.siblingsSet.add(sibling);
    }

    public void addSiblings(HashSet<String> siblingsSet) {
        this.siblingsSet.addAll(siblingsSet);
    }

    public void addDaughter(String daughter) {
        this.daughtersSet.add(daughter);
    }

    public void addSon(String son) {
        this.sonsSet.add(son);
    }

    public void addBrother(String brother) {
        this.brothersSet.add(brother);
    }

    public void addSister(String sister) {
        this.sistersSet.add(sister);
    }

    public void removeParent(String parent) {
        this.parentsSet.remove(parent);
    }

    public void removeChild(String child) {
        this.childrenSet.remove(child);
    }

    public void removeSibling(String sibling) {
        this.siblingsSet.remove(sibling);
    }

    public void removeDaughter(String daughter) {
        this.daughtersSet.remove(daughter);
    }

    public void removeSon(String son) {
        this.sonsSet.remove(son);
    }

    public void removeBrother(String brother) {
        this.brothersSet.remove(brother);
    }

    public void removeSister(String sister) {
        this.sistersSet.remove(sister);
    }

    public void clearParentsArray() {
        this.parentsSet.clear();
    }

    public void clearChildrenArray() {
        this.childrenSet.clear();
    }

    public void clearSiblingsArray() {
        this.siblingsSet.clear();
    }

    public Integer getNotNullParametersCount() {
        return param.size();
    }

    public String getFullName() {
        if (param.get("firstName") != null && param.get("surname") != null) {
            return (param.get("firstName") + " " + param.get("surname"));
        }
        return null;
    }

    public void mergeToPerson(Person person) {
        param.forEach((key, value) -> {
            if (value != null) {
                person.param.merge(key, value, (oldValue, newValue) -> newValue);
            }

        });
        person.parentsSet.addAll(parentsSet);
        person.childrenSet.addAll(childrenSet);
        person.siblingsSet.addAll(siblingsSet);
        person.daughtersSet.addAll(daughtersSet);
        person.sonsSet.addAll(sonsSet);
        person.brothersSet.addAll(brothersSet);
        person.sistersSet.addAll(sistersSet);
    }

    public void mergeToCheck(Person person) {
        param.forEach((key, value) -> {
            if (value != null) {
                person.param.merge(key, value, (oldVal, newVal) -> {
                    if (!Objects.equals(oldVal, newVal)) {
                        System.out.println(person.getFullName());
                    }
                    return newVal;
                });
            }
        });
        person.parentsSet.addAll(parentsSet);
        person.childrenSet.addAll(childrenSet);
        person.siblingsSet.addAll(siblingsSet);
        person.daughtersSet.addAll(daughtersSet);
        person.sonsSet.addAll(sonsSet);
        person.brothersSet.addAll(brothersSet);
        person.sistersSet.addAll(sistersSet);
    }

    public boolean resolveConflictMergeTo(Person person) {
        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (person.param.get(entry.getKey()) != null) {
                if (!entry.getValue().equals(person.param.get(entry.getKey()))) {
                    return false;
                }
            }
        }
        return true;
    }
}
