package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Comanda;
import model.LiniaComanda;
import util.Connexio;

public class ComandaDAO {

    // Crea la comanda en una sola transaccio. Si falla alguna cosa abans del savepoint, no queda res guardat.
    public Comanda crear(int clientId, List<LiniaComanda> linies) throws SQLException {
        if (linies == null || linies.isEmpty()) {
            throw new SQLException("La comanda no té cap línia.");
        }
        try (Connection con = Connexio.getConnection()) {
            con.setAutoCommit(false);
            try {
                int comandaId = inserirComanda(con, clientId);
                BigDecimal total = BigDecimal.ZERO;
                for (LiniaComanda linia : linies) {
                    BigDecimal preu = reservarEstoc(con, linia);
                    inserirLinia(con, comandaId, linia, preu);
                    total = total.add(preu.multiply(BigDecimal.valueOf(linia.getQuantitat())));
                }
                actualitzarTotal(con, comandaId, total);

                Savepoint despresLinies = con.setSavepoint("DespresLinies");
                boolean descompteAplicat = true;
                try {
                    total = aplicarDescomptes(con, comandaId, linies, total);
                } catch (SQLException e) {
                    con.rollback(despresLinies);
                    descompteAplicat = false;
                    System.out.println("No s'ha pogut aplicar el descompte (" + e.getMessage() + "). Es guarda sense descompte.");
                }

                con.commit();

                Comanda comanda = new Comanda();
                comanda.setId(comandaId);
                comanda.setClientId(clientId);
                comanda.setTotal(total.doubleValue());
                comanda.setDescompteAplicat(descompteAplicat);
                comanda.setLinies(linies);
                return comanda;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private int inserirComanda(Connection con, int clientId) throws SQLException {
        String sql = "INSERT INTO Comandes (client_id, total) VALUES (?, 0)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, clientId);
            ps.executeUpdate();
            try (ResultSet claus = ps.getGeneratedKeys()) {
                claus.next();
                return claus.getInt(1);
            }
        }
    }

    // Comprova l'estoc, el decrementa i torna el preu actual del producte.
    private BigDecimal reservarEstoc(Connection con, LiniaComanda linia) throws SQLException {
        if (linia.getQuantitat() <= 0) {
            throw new SQLException("La quantitat ha de ser més gran que zero.");
        }
        BigDecimal preu;
        String consulta = "SELECT preu, estoc FROM Productes WHERE id = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(consulta)) {
            ps.setInt(1, linia.getProducteId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("No existeix el producte " + linia.getProducteId() + ".");
                }
                if (rs.getInt("estoc") < linia.getQuantitat()) {
                    throw new SQLException("Estoc insuficient del producte " + linia.getProducteId() + ".");
                }
                preu = rs.getBigDecimal("preu");
            }
        }
        String sql = "UPDATE Productes SET estoc = estoc - ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, linia.getQuantitat());
            ps.setInt(2, linia.getProducteId());
            ps.executeUpdate();
        }
        return preu;
    }

    private void inserirLinia(Connection con, int comandaId, LiniaComanda linia, BigDecimal preu) throws SQLException {
        String sql = "INSERT INTO LiniesComanda (comanda_id, producte_id, quantitat, preuUnitari) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, comandaId);
            ps.setInt(2, linia.getProducteId());
            ps.setInt(3, linia.getQuantitat());
            ps.setBigDecimal(4, preu);
            ps.executeUpdate();
        }
        linia.setComandaId(comandaId);
        linia.setPreuUnitari(preu.doubleValue());
    }

    private void actualitzarTotal(Connection con, int comandaId, BigDecimal total) throws SQLException {
        String sql = "UPDATE Comandes SET total = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, total);
            ps.setInt(2, comandaId);
            ps.executeUpdate();
        }
    }

    // El % s'aplica sobre la linia i els € per cada unitat. Un descompte incorrecte llanca una excepcio.
    private BigDecimal aplicarDescomptes(Connection con, int comandaId, List<LiniaComanda> linies, BigDecimal total) throws SQLException {
        for (LiniaComanda linia : linies) {
            for (Object[] descompte : descomptesDe(con, linia.getProducteId())) {
                BigDecimal preu = BigDecimal.valueOf(linia.getPreuUnitari());
                BigDecimal import_ = calcular((String) descompte[0], (BigDecimal) descompte[1], preu, linia.getQuantitat());
                total = total.subtract(import_);
                actualitzarTotal(con, comandaId, total);
            }
        }
        if (total.signum() < 0) {
            throw new SQLException("El total no pot ser negatiu.");
        }
        return total;
    }

    private List<Object[]> descomptesDe(Connection con, int producteId) throws SQLException {
        List<Object[]> descomptes = new ArrayList<>();
        String sql = "SELECT tipus, valor FROM Descomptes WHERE producte_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, producteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    descomptes.add(new Object[] { rs.getString("tipus"), rs.getBigDecimal("valor") });
                }
            }
        }
        return descomptes;
    }

    private BigDecimal calcular(String tipus, BigDecimal valor, BigDecimal preu, int quantitat) throws SQLException {
        if (valor.signum() <= 0) {
            throw new SQLException("El valor del descompte ha de ser positiu.");
        }
        BigDecimal unitats = BigDecimal.valueOf(quantitat);
        if ("%".equals(tipus)) {
            if (valor.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new SQLException("Un descompte no pot superar el 100 %.");
            }
            return preu.multiply(unitats).multiply(valor).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        if (valor.compareTo(preu) > 0) {
            throw new SQLException("El descompte supera el preu del producte.");
        }
        return valor.multiply(unitats);
    }
}
