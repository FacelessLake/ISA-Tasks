package nsu.belozerov;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

public class XMLSaver {

    public void save(People people) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        Document doc = docBuilderFactory.newDocumentBuilder().newDocument();

        Element root = doc.createElement("people");
        doc.appendChild(root);
        for (Map.Entry<String, Person> personEntry : people.getPeople()) {
            Element person = doc.createElement("person");
            person.setAttribute("id", personEntry.getValue().getId());

            Element fullName = doc.createElement("full-name");
            fullName.setAttribute("name", personEntry.getValue().getFirstName());
            fullName.setAttribute("surname", personEntry.getValue().getSurname());
            person.appendChild(fullName);

            Element gender = doc.createElement("gender");
            gender.setAttribute("value", personEntry.getValue().getGender());
            person.appendChild(gender);


            if (personEntry.getValue().getFather() != null) {
                Element father = doc.createElement("father");
                father.setAttribute("name", personEntry.getValue().getFather());
                person.appendChild(father);
            }
            if (personEntry.getValue().getMother() != null) {
                Element mother = doc.createElement("mother");
                mother.setAttribute("name", personEntry.getValue().getMother());
                person.appendChild(mother);
            }

            if (personEntry.getValue().getParentsSet() != null) {
                Element parents = doc.createElement("parents");
                for (String ch : personEntry.getValue().getParentsSet()) {
                    Element parent = doc.createElement("parent");
                    parent.setAttribute("id", ch);
                    parents.appendChild(parent);
                }
                person.appendChild(parents);
            }

            Element siblings = doc.createElement("siblings");
            if (personEntry.getValue().getSiblingNumber() != null) {
                siblings.setAttribute("amount", personEntry.getValue().getSiblingNumber());
            } else {
                siblings.setAttribute("amount", "0");
            }
            for (String siblingId : personEntry.getValue().getSiblingsSet()) {
                Person sibling = people.getPersonById(siblingId);
                if (sibling.getGender().equals("male")) {
                    Element brother = doc.createElement("brother");
                    brother.setAttribute("id", siblingId);
                    siblings.appendChild(brother);
                }
                if (sibling.getGender().equals("female")) {
                    Element sister = doc.createElement("sister");
                    sister.setAttribute("id", siblingId);
                    siblings.appendChild(sister);
                }
            }
            person.appendChild(siblings);

            Element partner = doc.createElement("partner");
            if (personEntry.getValue().getHusband() != null || personEntry.getValue().getWife() != null) {
                if (personEntry.getValue().getHusband() != null) {
                    partner.setAttribute("husband", personEntry.getValue().getHusband());
                }

                if (personEntry.getValue().getWife() != null) {
                    partner.setAttribute("wife", personEntry.getValue().getWife());
                }

//            if (personEntry.getValue().getSpouse() != null) {
//                partner.setAttribute("name", personEntry.getValue().getSpouse());
//            }
            } else {
                partner.setAttribute("spouse", "NONE");
            }
            person.appendChild(partner);


            Element children = doc.createElement("children");
            if (personEntry.getValue().getChildrenNumber() != null) {
                children.setAttribute("amount", personEntry.getValue().getChildrenNumber());
            } else {
                children.setAttribute("amount", "0");
            }
            for (String sn : personEntry.getValue().getSonsSet()) {
                Element son = doc.createElement("son");
                son.setAttribute("id", sn);
                children.appendChild(son);
            }
            for (String dt : personEntry.getValue().getDaughtersSet()) {
                Element daughter = doc.createElement("daughter");
                daughter.setAttribute("id", dt);
                children.appendChild(daughter);
            }
            person.appendChild(children);

            root.appendChild(person);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc), new StreamResult(new File("output.xml")));
    }
}
