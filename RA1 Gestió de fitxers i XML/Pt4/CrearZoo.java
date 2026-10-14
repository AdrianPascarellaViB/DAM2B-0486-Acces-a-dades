import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CrearZoo {

    private static final String FITXER = "zoo.xml";

    private static final String[][] ANIMALS = {
            { "Pingüí", "Aptenodytes forsteri", "Peix", "5" },
            { "Lleó", "Panthera leo", "Carn", "8" },
            { "Elefant", "Loxodonta africana", "Herba", "25" },
            { "Tigre", "Panthera tigris", "Carn", "6" },
            { "Girafa", "Giraffa camelopardalis", "Fulles", "12" },
            { "Llop", "Canis lupus", "Carn", "4" }
    };

    public static void main(String[] args) {
        try {
            Document doc = crearDocument();
            desar(doc, FITXER);
            System.out.println("Fitxer " + FITXER + " creat amb " + ANIMALS.length + " animal(s).");
        } catch (ParserConfigurationException e) {
            System.out.println("Error en crear el document XML: " + e.getMessage());
        } catch (TransformerException e) {
            System.out.println("Error en escriure el fitxer XML: " + e.getMessage());
        }
    }

    private static Document crearDocument() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();

        Element zoo = doc.createElement("zoo");
        doc.appendChild(zoo);

        for (int i = 0; i < ANIMALS.length; i++) {
            String[] dades = ANIMALS[i];
            Element animal = doc.createElement("animal");
            animal.setAttribute("id", String.valueOf(i + 1));
            afegirFill(doc, animal, "nom", dades[0]);
            afegirFill(doc, animal, "especie", dades[1]);
            afegirFill(doc, animal, "aliment", dades[2]);
            afegirFill(doc, animal, "edat", dades[3]);
            zoo.appendChild(animal);
        }

        return doc;
    }

    private static void afegirFill(Document doc, Element pare, String nom, String text) {
        Element fill = doc.createElement(nom);
        fill.setTextContent(text);
        pare.appendChild(fill);
    }

    private static void desar(Document doc, String fitxer) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc), new StreamResult(new File(fitxer)));
    }
}
