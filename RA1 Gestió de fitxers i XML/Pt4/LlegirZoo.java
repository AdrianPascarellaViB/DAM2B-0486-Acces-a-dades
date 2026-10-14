import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LlegirZoo {

    private static final String FITXER = "zoo.xml";

    public static void main(String[] args) {
        File fitxer = new File(FITXER);
        if (!fitxer.exists()) {
            System.out.println("El fitxer " + FITXER + " no existeix. Executa primer CrearZoo.");
            return;
        }

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(fitxer);
            doc.getDocumentElement().normalize();

            NodeList animals = doc.getElementsByTagName("animal");

            System.out.println("=== Tots els animals ===\n");
            for (int i = 0; i < animals.getLength(); i++) {
                mostrar((Element) animals.item(i));
            }

            System.out.println("Total d'animals al zoo: " + animals.getLength());

            System.out.println("\n=== Animals que mengen Carn ===\n");
            int carnivors = 0;
            for (int i = 0; i < animals.getLength(); i++) {
                Element animal = (Element) animals.item(i);
                if (text(animal, "aliment").equalsIgnoreCase("Carn")) {
                    mostrar(animal);
                    carnivors++;
                }
            }
            System.out.println("Total d'animals que mengen Carn: " + carnivors);

        } catch (ParserConfigurationException e) {
            System.out.println("Error en configurar el parser XML: " + e.getMessage());
        } catch (SAXException e) {
            System.out.println("El fitxer XML no és vàlid: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error en llegir el fitxer: " + e.getMessage());
        }
    }

    private static void mostrar(Element animal) {
        System.out.println("Animal #" + animal.getAttribute("id"));
        System.out.println("Nom: " + text(animal, "nom"));
        System.out.println("Espècie: " + text(animal, "especie"));
        System.out.println("Aliment: " + text(animal, "aliment"));
        System.out.println("Edat: " + text(animal, "edat"));
        System.out.println();
    }

    private static String text(Element animal, String etiqueta) {
        NodeList nodes = animal.getElementsByTagName(etiqueta);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent().trim() : "";
    }
}
