import java.sql.*;
public class jdbcConnection {
    
    public static void main(String[] args) throws Exception {
        // Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/jdbcdb";
        String user = "root";
        String password = "root";

        Connection conn = java.sql.DriverManager.getConnection(url, user, password);
        if(conn != null){
            System.out.println("Connected to the database!");
        } else {
            System.out.println("Failed to make connection!");
        }
        Statement stmt=conn.createStatement();
        // String query="CREATE TABLE student("+"id INT,"+"name VARCHAR(50) NOT NULL"+")";
        // stmt.executeUpdate(query);
        // System.out.println("Table is Created");
        conn.close();

    }
}