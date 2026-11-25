package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Client;
import util.Connexio;

public class ClientDAO {

    public void inserir(Client c) throws SQLException {
        String sql = "INSERT INTO Clients (nom, correu) VALUES (?, ?)";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getCorreu());
            ps.executeUpdate();
            try (ResultSet claus = ps.getGeneratedKeys()) {
                if (claus.next()) {
                    c.setId(claus.getInt(1));
                }
            }
        }
    }

    public List<Client> llistar() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, nom, correu FROM Clients ORDER BY id";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clients.add(new Client(rs.getInt("id"), rs.getString("nom"), rs.getString("correu")));
            }
        }
        return clients;
    }

    public Client buscar(int id) throws SQLException {
        String sql = "SELECT id, nom, correu FROM Clients WHERE id = ?";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Client(rs.getInt("id"), rs.getString("nom"), rs.getString("correu")) : null;
            }
        }
    }

    public boolean actualitzar(Client c) throws SQLException {
        String sql = "UPDATE Clients SET nom = ?, correu = ? WHERE id = ?";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNom());
            ps.setString(2, c.getCorreu());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Clients WHERE id = ?";
        try (Connection con = Connexio.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
