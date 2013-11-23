/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class Database {
    static final Logger logger = Logger.getLogger(Database.class.getName());
    
    // Database properties. Hardcoded for now.
    static final String DATABASE_NAME = "IssueTrackingLite";
    static final int DB_PORT = 1527;
    static final String DB_HOST = "localhost";

    
    private Connection conn=null;
    
    private static Database instance=null;  // The singleton instance
    
    /**
     * This is a singleton class. Hence, disable constructor
     */
    private Database() {}

    private Database(String dbName, int dbPort, String dbHost) throws SQLException {
        logger.info("Initialising JavaDB database '"+dbName+"' on host "+dbHost+ " Port: "+dbPort );
        
        // JavaDB connection attributes
        Properties p = new Properties();
        p.setProperty("user", "admin");
        p.setProperty("password", "geheim123");
        p.setProperty("create", "true");
        
        // First, try to connect to an existing database.
        // JavaDB connection format: "jdbc:derby://localhost:1527/MyDbName"
        String connectionUrl = "jdbc:derby://"+dbHost+":"+dbPort+"/"+dbName;
        conn = DriverManager.getConnection(connectionUrl,p);    
    }

    public static Database getInstance() throws SQLException {
        if (instance == null) {
            instance = new Database(DATABASE_NAME,DB_PORT,DB_HOST);
        }
        return instance;
    }

    public static String errorString(Throwable e) {
        if (e instanceof SQLException)
            return SQLExceptionString((SQLException) e);
         else 
            return e.toString();
    }

    static String SQLExceptionString(SQLException sqle) {
        StringBuilder ss = new StringBuilder();
        while (sqle != null) {
            ss.append("---SQLException Caught---");
            ss.append("\n\tSQLState : " + (sqle).getSQLState());
            ss.append("\n\tSeverity : " + (sqle).getErrorCode());
            ss.append("\n\tMessage  : " + (sqle).getMessage());
            ss.append("\n-------------------------");
            sqle = sqle.getNextException();
        }
        return ss.toString();
    }
    
}
