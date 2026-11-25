package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Descompte;
import util.Connexio;

public class DescompteDAO {

    public void inserir(Descompte d) throws SQLException {
        String sql = "INSERT INTO Descomptes (producte_id, tipus, valor) VALUES (?, ?, ?)";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getProducteId());
            ps.setString(2, d.getTipus());
            ps.setDouble(3, d.getValor());
            ps.executeUpdate();
            try (ResultSet claus = ps.getGeneratedKeys()) {
                if (claus.next()) {
                    d.setId(claus.getInt(1));
                }
            }
        }
    }

    public List<Descompte> llistar() throws SQLException {
        List<Descompte> descomptes = new ArrayList<>();
        String sql = "SELECT id, producte_id, tipus, valor FROM Descomptes ORDER BY id";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                descomptes.add(new Descompte(rs.getInt("id"), rs.getInt("producte_id"), rs.getString("tipus"), rs.getDouble("valor")));
            }
        }
        return descomptes;
    }

    public boolean actualitzar(Descompte d) throws SQLException {
        String sql = "UPDATE Descomptes SET producte_id = ?, tipus = ?, valor = ? WHERE id = ?";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getProducteId());
            ps.setString(2, d.getTipus());
            ps.setDouble(3, d.getValor());
            ps.setInt(4, d.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Descomptes WHERE id = ?";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
