package com.bipoller;
import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * Runs the main BiPoller server that connects to the database.
 */
public class BiPollerServer {

    // The connection to the database
    private Connection conn;

    // The database config file path.
    public static String CONFIG_PATH = "./database.cfg";

    /**
     * Create a database connection with the given params
     * @param location: path of where to place the database
     * @param user: user name for the owner of the database
     * @param password: password of the database owner
     */
    public void createConnection(String location,
                                 String user,
                                 String password) {
        try {

            //This needs to be on the front of your location
            String url = "jdbc:h2:" + location;

            //This tells it to use the h2 driver
            Class.forName("org.h2.Driver");

            //creates the connection
            conn = DriverManager.getConnection(url,
                    user,
                    password);
        } catch (SQLException | ClassNotFoundException e) {
            //You should handle this better
            e.printStackTrace();
        }
    }

    /**
     * Creates a connection to the database by reading the database.cfg file in the working directory.
     * The config file must be set up as such:
     *
     * location=/path/to/your/location
     * user=your_username
     * password=your_password
     *
     * This file will not be tracked in git, as that would be a security vulnerability.
     */
    public void createConnectionFromConfig() {
        Properties dbProperties = new Properties();
        try {
            InputStream fileStream = new FileInputStream(CONFIG_PATH);
            dbProperties.load(fileStream);
        } catch (FileNotFoundException e) {
            error("could not find properties file at " + CONFIG_PATH);
        } catch (IOException e) {
            error("could not read " + CONFIG_PATH + "file: " + e.getMessage());
        }
        createConnection(dbProperties.getProperty("location"),
                         dbProperties.getProperty("user"),
                         dbProperties.getProperty("password"));
    }

    /**
     * The underlying database connection this server holds.
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Closes the connection.
     */
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the server, connecting and immediately disconnecting from the server.
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) {
        BiPollerServer server = new BiPollerServer();
        server.createConnectionFromConfig();
        server.closeConnection();
    }

    /**
     * Prints an error message and exits with a non-zero exit code.
     * @param message A message describing the error.
     */
    private static void error(String message) {
        System.out.println("error: " + message);
        System.exit(-1);
    }
}