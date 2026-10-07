import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class pt3 {

    private static final String FITXER = "videojocs.dat";

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ArrayList<Videojoc> videojocs = carregarVideojocs();

        int opcio;
        do {
            System.out.println("\n--- Gestió de Videojocs ---");
            System.out.println("1. Afegir videojoc");
            System.out.println("2. Llistar tots els videojocs");
            System.out.println("3. Cercar videojocs per títol");
            System.out.println("4. Actualitzar un videojoc");
            System.out.println("5. Eliminar un videojoc");
            System.out.println("6. Sortir");
            opcio = llegirEnter("Tria una opció: ");

            switch (opcio) {
                case 1:
                    afegir(videojocs);
                    break;
                case 2:
                    llistar(videojocs);
                    break;
                case 3:
                    cercar(videojocs);
                    break;
                case 4:
                    actualitzar(videojocs);
                    break;
                case 5:
                    eliminar(videojocs);
                    break;
                case 6:
                    desarVideojocs(videojocs);
                    System.out.println("Sortint del programa...");
                    break;
                default:
                    System.out.println("Opció incorrecta. Torna-ho a provar.");
            }
        } while (opcio != 6);

        sc.close();
    }

    private static void afegir(ArrayList<Videojoc> videojocs) {
        String titol = llegirText("Títol: ");
        String genere = llegirText("Gènere: ");
        int any = llegirEnter("Any de llançament: ");
        String plataforma = llegirText("Plataforma: ");
        double preu = llegirDecimal("Preu: ");

        videojocs.add(new Videojoc(titol, genere, any, plataforma, preu));
        desarVideojocs(videojocs);
        System.out.println("Videojoc afegit correctament.");
    }

    private static void llistar(ArrayList<Videojoc> videojocs) {
        if (videojocs.isEmpty()) {
            System.out.println("No hi ha videojocs desats.");
            return;
        }
        System.out.println("--- Llista de videojocs ---");
        for (int i = 0; i < videojocs.size(); i++) {
            System.out.println((i + 1) + ". " + videojocs.get(i));
        }
    }

    private static void cercar(ArrayList<Videojoc> videojocs) {
        String text = llegirText("Text a cercar: ").toLowerCase();
        int trobats = 0;
        for (int i = 0; i < videojocs.size(); i++) {
            Videojoc v = videojocs.get(i);
            if (v.getTitol().toLowerCase().contains(text)) {
                System.out.println((i + 1) + ". " + v);
                trobats++;
            }
        }
        if (trobats == 0) {
            System.out.println("Cap videojoc coincideix amb \"" + text + "\".");
        }
    }

    private static void actualitzar(ArrayList<Videojoc> videojocs) {
        Videojoc v = triarVideojoc(videojocs, "actualitzar");
        if (v == null) {
            return;
        }
        System.out.println("Deixa el camp buit per mantenir el valor actual.");

        String titol = llegirTextOpcional("Títol [" + v.getTitol() + "]: ");
        if (!titol.isEmpty()) {
            v.setTitol(titol);
        }
        String genere = llegirTextOpcional("Gènere [" + v.getGenere() + "]: ");
        if (!genere.isEmpty()) {
            v.setGenere(genere);
        }
        String any = llegirTextOpcional("Any de llançament [" + v.getAnyLlancament() + "]: ");
        if (!any.isEmpty()) {
            try {
                v.setAnyLlancament(Integer.parseInt(any));
            } catch (NumberFormatException e) {
                System.out.println("Any no vàlid, es manté " + v.getAnyLlancament() + ".");
            }
        }
        String plataforma = llegirTextOpcional("Plataforma [" + v.getPlataforma() + "]: ");
        if (!plataforma.isEmpty()) {
            v.setPlataforma(plataforma);
        }
        String preu = llegirTextOpcional("Preu [" + v.getPreu() + "]: ");
        if (!preu.isEmpty()) {
            try {
                v.setPreu(Double.parseDouble(preu.replace(',', '.')));
            } catch (NumberFormatException e) {
                System.out.println("Preu no vàlid, es manté " + v.getPreu() + ".");
            }
        }

        desarVideojocs(videojocs);
        System.out.println("Videojoc actualitzat: " + v);
    }

    private static void eliminar(ArrayList<Videojoc> videojocs) {
        Videojoc v = triarVideojoc(videojocs, "eliminar");
        if (v == null) {
            return;
        }
        videojocs.remove(v);
        desarVideojocs(videojocs);
        System.out.println("Videojoc eliminat: " + v.getTitol());
    }

    private static Videojoc triarVideojoc(ArrayList<Videojoc> videojocs, String accio) {
        if (videojocs.isEmpty()) {
            System.out.println("No hi ha videojocs desats.");
            return null;
        }
        llistar(videojocs);
        int num = llegirEnter("Número del videojoc a " + accio + " (0 per cancel·lar): ");
        if (num == 0) {
            System.out.println("Operació cancel·lada.");
            return null;
        }
        if (num < 1 || num > videojocs.size()) {
            System.out.println("No existeix cap videojoc amb el número " + num + ".");
            return null;
        }
        return videojocs.get(num - 1);
    }

    private static String llegirText(String missatge) {
        while (true) {
            String text = llegirTextOpcional(missatge);
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("El camp no pot estar buit.");
        }
    }

    private static String llegirTextOpcional(String missatge) {
        System.out.print(missatge);
        if (!sc.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada tancada. Els canvis ja estan desats, es surt.");
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    private static int llegirEnter(String missatge) {
        while (true) {
            try {
                return Integer.parseInt(llegirText(missatge));
            } catch (NumberFormatException e) {
                System.out.println("Cal introduir un nombre enter.");
            }
        }
    }

    private static double llegirDecimal(String missatge) {
        while (true) {
            try {
                return Double.parseDouble(llegirText(missatge).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Cal introduir un nombre (ex: 59.99).");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Videojoc> carregarVideojocs() {
        File fitxer = new File(FITXER);
        if (!fitxer.exists()) {
            System.out.println("No existeix " + FITXER + ", es comença amb un catàleg buit.");
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fitxer))) {
            ArrayList<Videojoc> videojocs = (ArrayList<Videojoc>) ois.readObject();
            System.out.println("Carregats " + videojocs.size() + " videojoc(s) de " + FITXER + ".");
            return videojocs;
        } catch (EOFException e) {
            System.out.println("El fitxer " + FITXER + " està buit o incomplet, es comença amb un catàleg buit.");
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("Error carregant " + FITXER + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private static void desarVideojocs(ArrayList<Videojoc> videojocs) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FITXER))) {
            oos.writeObject(videojocs);
        } catch (IOException e) {
            System.out.println("Error desant " + FITXER + ": " + e.getMessage());
        }
    }
}
