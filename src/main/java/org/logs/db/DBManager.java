package org.logs.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hsqldb.Server;
import org.logs.model.LogEvent;



public class DBManager {

    Logger logger = LogManager.getLogger(getClass());

    private static final String DB_NAME = "logs";
    private static final String DB_PATH = "file:logs";
    private static final String HOSTNAME = "localhost";
    private static final String TABLE_NAME = "alerts";
    private static final String TABLE_CREATION_SQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(id INTEGER IDENTITY PRIMARY KEY, logID VARCHAR(50) NOT NULL, duration BIGINT, host VARCHAR(50), type VARCHAR(50), alert BOOLEAN DEFAULT TRUE NOT NULL)";

    private Server hsqlServer = new Server();
    private Connection connection;

    /**
     * Start HSQL DB Server.
     */
    public void startHSQLDB() {
        hsqlServer.setLogWriter(null);
        hsqlServer.setSilent(true);
        hsqlServer.setDatabaseName(0, DB_NAME);
        hsqlServer.setDatabasePath(0, DB_PATH);
        hsqlServer.start();
        logger.info("HSQL Server started");
    }

    /**
     * Closes the existing connection and stops the HSQLDB server
     */
    public void stopHSQLDB() {

        if (connection != null) {
            try {
                connection.close();
                logger.info("Connection to HSQL Server closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        hsqlServer.stop();
        logger.info("HSQL Server stopped");
    }

    /**
     * Util method to create a connection
     * @return opened connection to the db
     */
    private Connection openConnection() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            return DriverManager.getConnection("jdbc:hsqldb:hsql://"+HOSTNAME+"/"+DB_NAME, "SA", "");
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return null;
    }

    /**
     * Util method to drop the existing HSQL Table
     */
    public void dropHSQLDBLTable() {
        Statement stmt = null;

        try {
            if (connection == null) {
                connection = openConnection();
            }
            stmt = connection.createStatement();
            stmt.executeUpdate("DROP TABLE "+TABLE_NAME);

        }  catch (SQLException e) {
            logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    /**
     * Creates the alerts table if not already existing
     */
    public void createHSQLDBTable() {
        Statement stmt = null;

        try {
            if (connection == null) {
                connection = openConnection();
            }

            //Check first if table exists
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME, new String[] {"TABLE"});

            if (!tables.next()) {
                stmt = connection.createStatement();
                stmt.executeUpdate(TABLE_CREATION_SQL);
                logger.info("Table "+TABLE_NAME+" created");
            }

        }  catch (SQLException  e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void insertEvent(LogEvent logEvent) {
        Statement stmt = null;
        int insertedRows = 0;

        try {
            if (connection == null) {
                connection = openConnection();
            }
            stmt = connection.createStatement();
            String updateStatement = "INSERT INTO "+TABLE_NAME+" (logID, duration, host, type) VALUES('"+logEvent.getId()+"',"+logEvent.getDuration()+",'"+logEvent.getHost().orElse("")+"','"+logEvent.getType().orElse("")+"')";
            insertedRows += stmt.executeUpdate(updateStatement);
            connection.commit();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
