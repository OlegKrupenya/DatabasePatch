package com.testdev.sha3dbpatch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.testdev.service.HashService;
import com.testdev.service.HashService.Size;

public class App {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/SBSS?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "javaUser";
    private static final String PASS = "85be963f-0868-4cce-9bc1-f553cabadad1";

    public static void main(String[] args) {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Connecting to the database...");
        String url = DB_URL;
        String user = USER;
        String pwd = PASS;
        if (args != null && args.length == 3) {
            url = args[0];
            user = args[1];
            pwd = args[2];
        }
        System.out.println("DB_URL: " + url);
        System.out.println("USER: " + user);
        System.out.println("PASSWORD: " + pwd);
        
        try (Connection conn = DriverManager.getConnection(url, user, pwd); 
                Statement stmt = conn.createStatement()) {
            String sql = "SELECT idLogin, Password FROM Login";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Long idLogin = rs.getLong("idLogin");
                String password = rs.getString("Password");
                if (password != null && password.length() < 128) {
                    String hashedPassword = HashService.digest(password, Size.S512); 
                    String updateTableSQL = "UPDATE Login SET Password = ? WHERE idLogin = ?";
                    PreparedStatement preparedStatement = conn.prepareStatement(updateTableSQL);
                    preparedStatement.setString(1, hashedPassword);
                    preparedStatement.setLong(2, idLogin);
                    preparedStatement.executeUpdate();
                    System.out.println(idLogin);
                }
            }
            rs.close();
            System.out.println("The database has been patched.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Houston, we have a problem");
        }
    }
}
