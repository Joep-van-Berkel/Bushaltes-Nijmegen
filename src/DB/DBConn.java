package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

    public static DBConn instance;
    public Connection conn;

    // check if there is not already an instance of DB.DBConn when being made.
    public DBConn() {
        if (instance == null) {
            instance = this;
            makeConnection();
        }
    }

    public boolean makeConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/bushaltes_Nijmegen", "root", "");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
        return true;
    }

    public boolean closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
        return true;
    }
}
