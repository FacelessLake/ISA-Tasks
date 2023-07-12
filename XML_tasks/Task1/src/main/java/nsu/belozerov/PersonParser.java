package nsu.belozerov;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class PersonParser {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader reader;

    public PersonParser(File file) throws XMLStreamException, FileNotFoundException {
        reader = xmlInputFactory.createXMLEventReader(new FileInputStream(file));
    }

    public People startParse() throws XMLStreamException {
        People people = new People();
        Person person = new Person();

        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                switch (startElement.getName().getLocalPart()) {

                    //main
                    case "person" -> {
                        person = new Person();
                        Attribute id = startElement.getAttributeByName(new QName("id"));
                        if (id != null) {
                            person.setId(id.getValue());
                        }
                        Attribute name = startElement.getAttributeByName(new QName("name"));
                        if (name != null) {
                            String[] nameArr = name.getValue().trim().split("\\s+");
                            person.setFirstName(nameArr[0]);
                            person.setSurname(nameArr[1]);
                        }
                    }

                    //attribute
                    case "id" -> {
                        Attribute id = startElement.getAttributeByName(new QName("value"));
                        if (id != null) {
                            person.setId(id.getValue().trim());
                        }
                    }
                    case "firstname" -> {
                        Attribute firstname = startElement.getAttributeByName(new QName("value"));
                        if (firstname != null) {
                            person.setFirstName(firstname.getValue().trim());
                        }
                        else {
                            nextEvent = reader.nextEvent();
                            person.setFirstName(nextEvent.asCharacters().getData().trim());
                        }
                    }
                    case "surname" -> {
                        Attribute surname = startElement.getAttributeByName(new QName("value"));
                        if (surname != null) {
                            person.setSurname(surname.getValue().trim());
                        }
                    }

                    //data
                    case "first" -> {
                        nextEvent = reader.nextEvent();
                        person.setFirstName(nextEvent.asCharacters().getData().trim());
                    }
                    case "family", "family-name" -> {
                        nextEvent = reader.nextEvent();
                        person.setSurname(nextEvent.asCharacters().getData().trim());
                    }

                    //data or attribute
                    case "gender" -> {
                        Attribute gender = startElement.getAttributeByName(new QName("value"));
                        if (gender != null) {
                            person.setGender(gender.getValue());
                            break;
                        }
                        nextEvent = reader.nextEvent();
                        if (nextEvent.asCharacters().getData().trim().equals("M")) {
                            person.setGender("male");
                        }
                        if (nextEvent.asCharacters().getData().trim().equals("F")) {
                            person.setGender("female");
                        }
                    }

                    //OTHER PEOPLE

                    //full name data only
                    case "father" -> {
                        nextEvent = reader.nextEvent();
                        String fullName = nextEvent.asCharacters().getData().trim().replaceAll("\s+", " ");
                        person.setFather(fullName);
                    }
                    case "mother" -> {
                        nextEvent = reader.nextEvent();
                        String fullName = nextEvent.asCharacters().getData().trim().replaceAll("\s+", " ");
                        person.setMother(fullName);
                    }
                    case "brother" -> {
                        nextEvent = reader.nextEvent();
                        String fullName = nextEvent.asCharacters().getData().trim().replaceAll("\s+", " ");
                        person.addBrother(fullName);
                    }
                    case "sister" -> {
                        nextEvent = reader.nextEvent();
                        String fullName = nextEvent.asCharacters().getData().trim().replaceAll("\s+", " ");
                        person.addSister(fullName);
                    }
                    case "child" -> {
                        nextEvent = reader.nextEvent();
                        String fullName = nextEvent.asCharacters().getData().trim().replaceAll("\s+", " ");
                        person.addChild(fullName);
                    }

                    //NONE or full name attribute
                    case "spouce" -> {
                        Attribute spouse = startElement.getAttributeByName(new QName("value"));
                        if (spouse != null && !Objects.equals(spouse.getValue(), "NONE")) {
                            String[] nameArr = spouse.getValue().trim().split("\\s+");
                            String fullName = nameArr[0] + " " + nameArr[1];
                            person.setSpouse(fullName);
                        }
                    }

                    //id attribute only
                    case "wife" -> {
                        person.setGender("male");
                        Attribute wife = startElement.getAttributeByName(new QName("value"));
                        if (wife != null) {
                            person.setWife(wife.getValue().trim());

                            //Checking whether this person exists, if not - creating new
                            Person woman = new Person();
                            woman.setId(wife.getValue().trim());
                            woman.setGender("female");
                            if (person.getId()!= null) {
                                woman.setHusband(person.getId());
                            }
                            if (person.getFullName() != null) {
                                woman.setSpouse(person.getFullName());
                            }

                            Person search = people.getPersonById(woman.getId());
                            if (search != null) {
                                woman.mergeToPerson(search);
                            } else {
                                people.addPerson(woman.getId(), woman);
                            }
                        }
                    }
                    case "husband" -> {
                        person.setGender("female");
                        Attribute husband = startElement.getAttributeByName(new QName("value"));
                        if (husband != null) {
                            person.setHusband(husband.getValue().trim());

                            //Checking whether this person exists, if not - creating new
                            Person man = new Person();
                            man.setId(husband.getValue().trim());
                            man.setGender("male");
                            if (person.getId()!= null) {
                                man.setWife(person.getId());
                            }
                            if (person.getFullName() != null) {
                                man.setSpouse(person.getFullName());
                            }

                            Person search = people.getPersonById(man.getId());
                            if (search != null) {
                                man.mergeToPerson(search);
                            } else {
                                people.addPerson(man.getId(), man);
                            }
                        }
                    }
                    case "parent" -> { //Could be "UNKNOWN"
                        Attribute parent = startElement.getAttributeByName(new QName("value"));
                        if (parent != null && !Objects.equals(parent.getValue().trim(), "UNKNOWN")) {
                            person.addParent(parent.getValue().trim());

                            //Checking whether this person exists, if not - creating new
                            Person ancestor = new Person();
                            ancestor.setId(parent.getValue().trim());
                            if (person.getFullName() != null) {
                                ancestor.setSpouse(person.getFullName());
                            }
                            if (person.getId()!= null) {
                                if (Objects.equals(person.getGender(), "male")) {
                                    ancestor.addSon(person.getId());
                                }
                                if (Objects.equals(person.getGender(), "female")) {
                                    ancestor.addDaughter(person.getId());
                                }
                            }

                            Person search = people.getPersonById(ancestor.getId());
                            if (search != null) {
                                ancestor.mergeToPerson(search);
                            } else {
                                people.addPerson(ancestor.getId(), ancestor);
                            }
                        }
                    }
                    case "siblings-number" -> {
                        Attribute siblingNumber = startElement.getAttributeByName(new QName("value"));
                        if (siblingNumber != null) {
                            person.setSiblingNumber(siblingNumber.getValue().trim());
                        }
                    }
                    case "children-number" -> {
                        Attribute childrenNumber = startElement.getAttributeByName(new QName("value"));
                        if (childrenNumber != null) {
                            person.setChildrenNumber(childrenNumber.getValue().trim());
                        }
                    }
                    case "daughter" -> {
                        Attribute daughter = startElement.getAttributeByName(new QName("id"));
                        if (daughter != null) {
                            person.addDaughter(daughter.getValue().trim());

                            //Checking whether this person exists, if not - creating new
                            Person child = new Person();
                            child.setGender("female");
                            child.setId(daughter.getValue().trim());
                            if (person.getId()!= null) {
                                child.addParent(person.getId());
                            }

                            if (person.getFullName() != null) {
                                if (Objects.equals(person.getGender(), "male")) {
                                    child.setFather(person.getFullName());
                                }
                                if (Objects.equals(person.getGender(), "female")) {
                                    child.setMother(person.getFullName());
                                }
                            }

                            Person search = people.getPersonById(child.getId());
                            if (search != null) {
                                child.mergeToPerson(search);
                            } else {
                                people.addPerson(child.getId(), child);
                            }
                        }
                    }
                    case "son" -> {
                        Attribute son = startElement.getAttributeByName(new QName("id"));
                        if (son != null) {
                            person.addSon(son.getValue().trim());

                            //Checking whether this person exists, if not - creating new
                            Person child = new Person();
                            child.setGender("male");
                            child.setId(son.getValue().trim());
                            if (person.getId()!= null) {
                                child.addParent(person.getId());
                            }

                            if (person.getFullName() != null) {
                                if (Objects.equals(person.getGender(), "male")) {
                                    child.setFather(person.getFullName());
                                }
                                if (Objects.equals(person.getGender(), "female")) {
                                    child.setMother(person.getFullName());
                                }
                            }

                            Person search = people.getPersonById(child.getId());
                            if (search != null) {
                                child.mergeToPerson(search);
                            } else {
                                people.addPerson(child.getId(), child);
                            }
                        }
                    }

                    //number of ids
                    case "siblings" -> {
                        Attribute siblings = startElement.getAttributeByName(new QName("val"));
                        if (siblings != null) {
                            HashSet<String> siblingSet = new HashSet<>(Arrays.stream(siblings.getValue().split("\\s+")).toList());
                            person.addSiblings(siblingSet);

                            //Checking whether this people exist, if not - creating new
                            for (String sibling : siblingSet) {
                                Person siblingPerson = new Person();
                                siblingPerson.setId(sibling);
                                if (person.getId()!= null){
                                    siblingPerson.addSibling(person.getId());
                                }

                                //Everyone should receive all siblings except themselves
                                HashSet<String> remainedSiblings = new HashSet<>(siblingSet);
                                remainedSiblings.remove(sibling);
                                siblingPerson.addSiblings(remainedSiblings);

                                if (person.getFullName() != null) {
                                    if (Objects.equals(person.getGender(), "male")) {
                                        siblingPerson.addBrother(person.getFullName());
                                    }
                                    if (Objects.equals(person.getGender(), "female")) {
                                        siblingPerson.addSister(person.getFullName());
                                    }
                                }

                                Person search = people.getPersonById(siblingPerson.getId());
                                if (search != null) {
                                    siblingPerson.mergeToPerson(search);
                                } else {
                                    people.addPerson(siblingPerson.getId(), siblingPerson);
                                }
                            }
                        }
                    }

                }
            }

            if (nextEvent.isEndElement()) {
                EndElement endElement = nextEvent.asEndElement();
                if (endElement.getName().getLocalPart().equals("person")) {
                    String id = person.getId();

                    if (id != null) {
                        Person search = people.getPersonById(id);
                        if (search != null) {
                            person.mergeToPerson(search);
                        } else {
                            people.addPerson(id, person);
                        }
                    } else {
                        people.addIdLessPerson(person);
                    }
                }
            }
        }
        return people;
    }
}
