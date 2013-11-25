/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package issuetrackinglite.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
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
    static final String DB_NAME = "IssueTrackingLite";
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
        String connectionUrl = "jdbc:derby://"+dbHost+":"+dbPort+"/"+dbName;
        Properties p = new Properties();
        p.setProperty("user", "admin");
        p.setProperty("password", "geheim123");
    //    p.setProperty("create", "true");
        
        logger.info("Connection URL: "+connectionUrl);
        
        // First, try to connect to an existing database.
        // JavaDB connection format: "jdbc:derby://localhost:1527/MyDbName"
        // If db does not exist yet, create it and load some data.
        
        try {
            conn = DriverManager.getConnection(connectionUrl,p);
        } catch (SQLNonTransientConnectionException e ) {
            // This is kind of a dirty hack sql. code 080004 can have a few other 
            // causes other than missing database name....
            // However, the error msg contains "[...]database <name> not found[...]"
            if (e.getSQLState().equals("08004") && e.getMessage().contains("not found")) {
                logger.warning("Database "+dbName+" does not exist.");
                
                // Try to connect again, this time create the db
                p.setProperty("create", "true");
                conn = DriverManager.getConnection(connectionUrl,p);
               
                // load some stuff....
                initializeDatabase();
            } else {
                // Error other than db not exists error occured.
                throw e;
            }
        }
    }

    public Connection getConnection() {
        return conn;
    }
    public static Database getInstance() throws SQLException {
        if (instance == null) {
            instance = new Database(DB_NAME,DB_PORT,DB_HOST);
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
            ss.append("\n\tSQLState : " + sqle.getSQLState());
            ss.append("\n\tSeverity : " + sqle.getErrorCode());
            ss.append("\n\tMessage  : " + sqle.getMessage());
            ss.append("\n-------------------------");
            sqle = sqle.getNextException();
        }
        return ss.toString();
    }

    /**
     * Database schema:
     * Project Table
     *  - Id
     *  - Name
     * Issue Table
     *  - Id
     *  - ProjectId
     *  - Description
     *  - 
     */
    private void initializeDatabase() throws SQLException {
        loadSchema();
        loadData();
       
    }

    private void loadSchema() throws SQLException {
        logger.info("Loading db table schema ...");

        // try-with-resource.  s will be closed regardless an exception
        // has been thrown inside the following block.
        try (Statement s = conn.createStatement()) {
            logger.fine("Creating table PROJECT ...");
            s.executeUpdate("create table Project("
                    + "Id INTEGER PRIMARY KEY, "
                    + "Name VARCHAR(512) "
                    + ")");
            logger.fine("Creating table ISSUE ...");
            s.executeUpdate("create table Issue ("
                    + "Id INTEGER PRIMARY KEY, "
                    + "ProjectId INTEGER, " // foreign key to project related to issue
                    + "Status INTEGER, "
                    + "Synopsis VARCHAR(1024), "
                    + "Description VARCHAR(32000) "  // TODO: this should be probably a blob or clob? max length is 32672
                    + ")");
        }
        logger.info("DB tables loaded successfully.");
    }

    private void loadData() throws SQLException {
        logger.info("Loading db data ...");
//                for (String s : newList("Project1", "Project2", "Project3", "Project4")) {

        try (Statement s = conn.createStatement()) {
            logger.fine("Loading PROJECT data ...");
            int nIssue = 0;
            for (int i = 1; i < 10; i++) {
                s.executeUpdate(
                        "insert into Project values(" + i + ", 'Project" + i + "')");

                logger.fine("Loading ISSUE data for Project ID" + i + "...");
                for (int j = 0; j < 50; j++) {
                    s.executeUpdate(
                            "insert into Issue "
                            + "values(" + nIssue + ", "
                            + i + ", "
                            + "0, "
                            + "'Synopsis " + j + "', "
                            + "'Description " + j + "'"
                            + ")");
                    nIssue++;
                }
            }
        }

    }

}
