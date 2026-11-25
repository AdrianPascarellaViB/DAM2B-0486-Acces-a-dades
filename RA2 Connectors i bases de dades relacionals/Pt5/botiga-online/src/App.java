import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dao.ClientDAO;
import dao.ComandaDAO;
import dao.ConsultesDAO;
import dao.DescompteDAO;
import dao.ProducteDAO;
import model.Client;
import model.Comanda;
import model.Descompte;
import model.LiniaComanda;
import model.Producte;

public class App {

    private static final Scanner sc = new Scanner(System.in);
    private static final ProducteDAO productes = new ProducteDAO();
    private static final ClientDAO clients = new ClientDAO();
    private static final DescompteDAO descomptes = new DescompteDAO();
    private static final ComandaDAO comandes = new ComandaDAO();
    private static final ConsultesDAO consultes = new ConsultesDAO();

    public static void main(String[] args) {
        int opcio;
        do {
            System.out.println("\n===== BOTIGA ONLINE =====");
            System.out.println("1. Gestionar Productes");
            System.out.println("2. Gestionar Clients");
            System.out.println("3. Gestionar Descomptes");
            System.out.println("4. Crear Comanda");
            System.out.println("5. Llistar Comandes d'un client");
            System.out.println("6. Totals de les comandes");
            System.out.println("0. Sortir");
            opcio = llegirEnter("Opció: ");
            try {
                switch (opcio) {
                    case 1 -> menuProductes();
                    case 2 -> menuClients();
                    case 3 -> menuDescomptes();
                    case 4 -> crearComanda();
                    case 5 -> consultes.comandesDeClient(llegirEnter("Id del client: ")).forEach(System.out::println);
                    case 6 -> consultes.totalsComandes().forEach(System.out::println);
                    case 0 -> System.out.println("Fins aviat!");
                    default -> System.out.println("Opció incorrecta.");
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcio != 0);
    }

    private static int submenu(String titol) {
        System.out.println("\n--- " + titol + " ---");
        System.out.println("1. Llistar  2. Afegir  3. Modificar  4. Eliminar  0. Tornar");
        return llegirEnter("Opció: ");
    }

    private static void menuProductes() throws SQLException {
        switch (submenu("Productes")) {
            case 1 -> productes.llistar().forEach(System.out::println);
            case 2 -> {
                Producte p = new Producte(0, llegirText("Nom: "), llegirDecimal("Preu: "), llegirEnter("Estoc: "));
                productes.inserir(p);
                System.out.println("Producte afegit amb id " + p.getId() + ".");
            }
            case 3 -> {
                int id = llegirEnter("Id: ");
                Producte p = new Producte(id, llegirText("Nom: "), llegirDecimal("Preu: "), llegirEnter("Estoc: "));
                informar(productes.actualitzar(p));
            }
            case 4 -> informar(productes.eliminar(llegirEnter("Id: ")));
            default -> { }
        }
    }

    private static void menuClients() throws SQLException {
        switch (submenu("Clients")) {
            case 1 -> clients.llistar().forEach(System.out::println);
            case 2 -> {
                Client c = new Client(0, llegirText("Nom: "), llegirText("Correu: "));
                clients.inserir(c);
                System.out.println("Client afegit amb id " + c.getId() + ".");
            }
            case 3 -> {
                int id = llegirEnter("Id: ");
                informar(clients.actualitzar(new Client(id, llegirText("Nom: "), llegirText("Correu: "))));
            }
            case 4 -> informar(clients.eliminar(llegirEnter("Id: ")));
            default -> { }
        }
    }

    private static void menuDescomptes() throws SQLException {
        switch (submenu("Descomptes")) {
            case 1 -> descomptes.llistar().forEach(System.out::println);
            case 2 -> {
                Descompte d = new Descompte(0, llegirEnter("Id del producte: "), llegirText("Tipus (% o €): "), llegirDecimal("Valor: "));
                descomptes.inserir(d);
                System.out.println("Descompte afegit amb id " + d.getId() + ".");
            }
            case 3 -> {
                int id = llegirEnter("Id: ");
                informar(descomptes.actualitzar(new Descompte(id, llegirEnter("Id del producte: "), llegirText("Tipus (% o €): "), llegirDecimal("Valor: "))));
            }
            case 4 -> informar(descomptes.eliminar(llegirEnter("Id: ")));
            default -> { }
        }
    }

    private static void crearComanda() throws SQLException {
        int clientId = llegirEnter("Id del client: ");
        List<LiniaComanda> linies = new ArrayList<>();
        int producteId = llegirEnter("Id del producte (0 per acabar): ");
        while (producteId != 0) {
            linies.add(new LiniaComanda(producteId, llegirEnter("Quantitat: ")));
            producteId = llegirEnter("Id del producte (0 per acabar): ");
        }
        Comanda c = comandes.crear(clientId, linies);
        System.out.printf("Comanda %d creada. Total: %.2f €%s%n", c.getId(), c.getTotal(), c.isDescompteAplicat() ? "" : " (sense descompte)");
    }

    private static void informar(boolean fet) {
        System.out.println(fet ? "Fet." : "No existeix aquest id.");
    }

    private static String llegirText(String missatge) {
        System.out.print(missatge);
        return sc.nextLine().trim();
    }

    private static int llegirEnter(String missatge) {
        while (true) {
            try {
                return Integer.parseInt(llegirText(missatge));
            } catch (NumberFormatException e) {
                System.out.println("Escriu un nombre enter.");
            }
        }
    }

    private static double llegirDecimal(String missatge) {
        while (true) {
            try {
                return Double.parseDouble(llegirText(missatge).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Escriu un nombre.");
            }
        }
    }
}
