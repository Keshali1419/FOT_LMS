package Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnetor {

    // Method to establish a connection to the MySQL database
    public Connection getMyConnection() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Return the connection to the database
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/tecmis", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Return null if connection fails
        }
    }
}
