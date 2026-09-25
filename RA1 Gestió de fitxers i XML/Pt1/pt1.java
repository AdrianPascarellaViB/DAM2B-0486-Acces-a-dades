import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class pt1 {

    public static void main(String[] args) {
        int numCaracters = 0;
        int numLinies = 0;
        int numParaules = 0;
        boolean dinsParaula = false;
        boolean hiHaContingut = false;
        char ultimCaracter = '\n';
        int[] frequencia = new int[65536];

        try (FileReader fr = new FileReader("text.txt")) {
            int c;
            while ((c = fr.read()) != -1) {
                char caracter = (char) c;
                hiHaContingut = true;
                ultimCaracter = caracter;

                if (caracter != '\n' && caracter != '\r') {
                    numCaracters++;
                }

                if (caracter == '\n') {
                    numLinies++;
                }

                boolean esSeparador = caracter == ' ' || caracter == '\t'
                        || caracter == '\n' || caracter == '\r';

                if (esSeparador) {
                    dinsParaula = false;
                } else {
                    if (!dinsParaula) {
                        numParaules++;
                    }
                    dinsParaula = true;
                    frequencia[caracter]++;
                }
            }

            // L'última línia no acaba en salt de línia, però encara compta
            if (hiHaContingut && ultimCaracter != '\n') {
                numLinies++;
            }

            int caracterMesRepetit = -1;
            int maxFrequencia = 0;
            for (int i = 0; i < frequencia.length; i++) {
                if (frequencia[i] > maxFrequencia) {
                    maxFrequencia = frequencia[i];
                    caracterMesRepetit = i;
                }
            }

            System.out.println("Nombre de caràcters: " + numCaracters);
            System.out.println("Nombre de línies: " + numLinies);
            System.out.println("Nombre de paraules: " + numParaules);
            if (caracterMesRepetit != -1) {
                System.out.println("Caràcter més repetit: " + (char) caracterMesRepetit
                        + " (" + maxFrequencia + " vegades)");
            } else {
                System.out.println("Caràcter més repetit: cap (fitxer sense caràcters vàlids)");
            }

        } catch (FileNotFoundException e) {
            System.out.println("El fitxer no existeix.");
        } catch (IOException e) {
            System.out.println("S'ha produït un error de lectura.");
        } catch (SecurityException e) {
            System.out.println("No tens permisos per accedir al fitxer.");
        }
    }
}
