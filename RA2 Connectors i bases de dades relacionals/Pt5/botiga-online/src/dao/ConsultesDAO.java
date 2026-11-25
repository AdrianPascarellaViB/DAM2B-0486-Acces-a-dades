package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.Connexio;

public class ConsultesDAO {

    // Comandes d'un client amb les linies i els productes.
    public List<String> comandesDeClient(int clientId) throws SQLException {
        List<String> files = new ArrayList<>();
        String sql = "SELECT co.id, co.data, cl.nom AS client, p.nom AS producte, l.quantitat, l.preuUnitari "
                + "FROM Comandes co "
                + "JOIN Clients cl ON cl.id = co.client_id "
                + "JOIN LiniesComanda l ON l.comanda_id = co.id "
                + "JOIN Productes p ON p.id = l.producte_id "
                + "WHERE co.client_id = ? ORDER BY co.id, l.id";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    files.add(String.format("Comanda %d (%s, %s): %d x %s a %.2f €", rs.getInt("id"), rs.getString("client"),
                            rs.getTimestamp("data").toLocalDateTime().toLocalDate(), rs.getInt("quantitat"),
                            rs.getString("producte"), rs.getDouble("preuUnitari")));
                }
            }
        }
        return files;
    }

    // Total final de cada comanda. El subtotal surt de les linies i la diferencia es el descompte.
    public List<String> totalsComandes() throws SQLException {
        List<String> files = new ArrayList<>();
        String sql = "SELECT co.id, cl.nom AS client, SUM(l.quantitat * l.preuUnitari) AS subtotal, co.total "
                + "FROM Comandes co "
                + "JOIN Clients cl ON cl.id = co.client_id "
                + "JOIN LiniesComanda l ON l.comanda_id = co.id "
                + "GROUP BY co.id, cl.nom, co.total ORDER BY co.id";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double subtotal = rs.getDouble("subtotal");
                double total = rs.getDouble("total");
                files.add(String.format("Comanda %d (%s): subtotal %.2f €, descompte %.2f €, total %.2f €", rs.getInt("id"),
                        rs.getString("client"), subtotal, subtotal - total, total));
            }
        }
        return files;
    }
}
