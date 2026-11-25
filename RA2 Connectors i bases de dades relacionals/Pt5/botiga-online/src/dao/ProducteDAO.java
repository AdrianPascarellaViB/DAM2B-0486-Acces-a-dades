package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Producte;
import util.Connexio;

public class ProducteDAO {

    public void inserir(Producte p) throws SQLException {
        String sql = "INSERT INTO Productes (nom, preu, estoc) VALUES (?, ?, ?)";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNom());
            ps.setDouble(2, p.getPreu());
            ps.setInt(3, p.getEstoc());
            ps.executeUpdate();
            try (ResultSet claus = ps.getGeneratedKeys()) {
                if (claus.next()) {
                    p.setId(claus.getInt(1));
                }
            }
        }
    }

    public List<Producte> llistar() throws SQLException {
        List<Producte> productes = new ArrayList<>();
        String sql = "SELECT id, nom, preu, estoc FROM Productes ORDER BY id";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productes.add(llegir(rs));
            }
        }
        return productes;
    }

    public Producte buscar(int id) throws SQLException {
        String sql = "SELECT id, nom, preu, estoc FROM Productes WHERE id = ?";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? llegir(rs) : null;
            }
        }
    }

    public boolean actualitzar(Producte p) throws SQLException {
        String sql = "UPDATE Productes SET nom = ?, preu = ?, estoc = ? WHERE id = ?";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setDouble(2, p.getPreu());
            ps.setInt(3, p.getEstoc());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // Els descomptes del producte s'esborren amb ell. Si ja surt en alguna comanda, no es pot eliminar.
    public boolean eliminar(int id) throws SQLException {
        try (Connection con = Connexio.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement descomptes = con.prepareStatement("DELETE FROM Descomptes WHERE producte_id = ?");
                    PreparedStatement producte = con.prepareStatement("DELETE FROM Productes WHERE id = ?")) {
                descomptes.setInt(1, id);
                descomptes.executeUpdate();
                producte.setInt(1, id);
                boolean eliminat = producte.executeUpdate() > 0;
                con.commit();
                return eliminat;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private Producte llegir(ResultSet rs) throws SQLException {
        return new Producte(rs.getInt("id"), rs.getString("nom"), rs.getDouble("preu"), rs.getInt("estoc"));
    }
}
