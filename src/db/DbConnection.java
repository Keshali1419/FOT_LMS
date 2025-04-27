package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String url = "jdbc:mysql://localhost:3306/tecmis";
    private static final String user = "root";
    private static final String password = "1234";

    private static void registerMyConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error in registering the drive class" + e.getMessage());
        }
    }

    public static Connection getMyConnection(){
        registerMyConnection();
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Error in getting connection" + e.getMessage());
        }
        return null;
    }
}
