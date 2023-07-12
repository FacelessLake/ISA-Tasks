package nsu.belozerov;
// XML person parser java realisation

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws XMLStreamException, FileNotFoundException, ParserConfigurationException, TransformerException {
        File file = new File("D:\\WhiteLake\\University\\InfoStorage&Analysis\\XML_tasks\\Task1\\src\\main\\resources\\people.xml");
        PersonParser ssp = new PersonParser(file);
        People people = ssp.startParse();

        RelationsProcessor processor = new RelationsProcessor(people);
        processor.resolveRelations();

        XMLSaver saver = new XMLSaver();
        saver.save(people);
    }
}