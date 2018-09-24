package net.bigmir.venzor.simpleclass;

import lombok.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Data
public class SMSSender {
private static final String DB_URL = "jdbc:mysql://94.249.146.189/users";
    private static final String DB_USER = "user";
    private static final String DB_PASSOWOD = "password";
    private static final String alfaName = "Msg";

    private static Connection connection;

    public static void sendSMScode(String phone, int code) throws SQLException {
        connection= DriverManager.getConnection(DB_URL, DB_USER, DB_PASSOWOD);
        String msg = String.valueOf(code);
        prepareSending(connection, phone, msg);
    }

    public static void sendSMSmsg(String phone, String msg) throws SQLException {
        connection= DriverManager.getConnection(DB_URL, DB_USER, DB_PASSOWOD);
        prepareSending(connection, phone, msg);
    }

    private static void prepareSending(Connection connection, String phone, String msg) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO `user` (`number`, `sign`, `message`) VALUES (?, ?, ?)");
        try {
            ps.setString(1, phone);
            ps.setString(2, alfaName);
            ps.setString(3, msg);
            ps.executeUpdate();
        }finally {
            ps.close();
        }
    }
}
