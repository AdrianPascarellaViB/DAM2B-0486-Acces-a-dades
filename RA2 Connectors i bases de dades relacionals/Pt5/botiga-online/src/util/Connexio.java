package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexio {

    // Valors per defecte de XAMPP. Es poden canviar amb -Dbotiga.url, -Dbotiga.usuari i -Dbotiga.contrasenya
    private static final String URL = System.getProperty("botiga.url", "jdbc:mysql://localhost:3306/botiga");
    private static final String USER = System.getProperty("botiga.usuari", "root");
    private static final String PASSWORD = System.getProperty("botiga.contrasenya", "");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
