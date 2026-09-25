import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class pt2 {

    public static void main(String[] args) {
        int clau = demanarClau();
        xifrar("entrada.txt", "xifrat.txt", clau);
        desxifrar("xifrat.txt", "desxifrat.txt", clau);
    }

    private static int demanarClau() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("Clau de xifrat (nombre enter): ");
                String entrada = sc.nextLine().trim();
                try {
                    return Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    System.out.println("Clau no vàlida: cal introduir un nombre enter. Torna-ho a provar.");
                }
            }
        }
    }

    private static void xifrar(String fitxerEntrada, String fitxerSortida, int clau) {
        try (BufferedReader br = new BufferedReader(new FileReader(fitxerEntrada));
                BufferedWriter bw = new BufferedWriter(new FileWriter(fitxerSortida))) {

            String linia;
            int totalLinies = 0;
            while ((linia = br.readLine()) != null) {
                String invertida = invertir(linia);
                String xifrada = desplacar(invertida, clau);
                bw.write(xifrada);
                bw.newLine();
                totalLinies++;
            }

            System.out.println("Xifrat complet: " + totalLinies + " línia(es) escrites a " + fitxerSortida);

        } catch (FileNotFoundException e) {
            System.out.println("El fitxer no existeix: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error d'entrada/sortida durant el xifrat: " + e.getMessage());
        }
    }

    private static void desxifrar(String fitxerEntrada, String fitxerSortida, int clau) {
        try (BufferedReader br = new BufferedReader(new FileReader(fitxerEntrada));
                BufferedWriter bw = new BufferedWriter(new FileWriter(fitxerSortida))) {

            String linia;
            int totalLinies = 0;
            while ((linia = br.readLine()) != null) {
                String desplacada = desplacar(linia, -clau);
                String original = invertir(desplacada);
                bw.write(original);
                bw.newLine();
                totalLinies++;
            }

            System.out.println("Desxifrat complet: " + totalLinies + " línia(es) escrites a " + fitxerSortida);

        } catch (FileNotFoundException e) {
            System.out.println("El fitxer no existeix: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error d'entrada/sortida durant el desxifrat: " + e.getMessage());
        }
    }

    private static String invertir(String text) {
        return new StringBuilder(text).reverse().toString();
    }

    private static String desplacar(String text, int clau) {
        StringBuilder resultat = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            resultat.append((char) (c + clau));
        }
        return resultat.toString();
    }
}
