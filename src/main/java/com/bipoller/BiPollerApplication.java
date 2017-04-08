package com.bipoller;

import com.bipoller.auth.AuthFeature;
import com.bipoller.auth.BiPollerAuthFilter;
import com.bipoller.auth.BiPollerAuthenticator;
import com.bipoller.database.*;
import com.bipoller.models.*;
import com.bipoller.resources.*;
import io.dropwizard.Application;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.joda.time.DateTimeZone;
import unitedstates.US;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * Runs the main BiPoller server that connects to the database.
 */
public class BiPollerApplication extends Application<BiPollerConfiguration> {

    // The connection to the database
    private Connection conn;

    // The database config file path.
    public static String CONFIG_PATH = "./database.cfg";

    /**
     * Create a database connection with the given params
     *
     * @param location: path of where to place the database
     * @param user:     user name for the owner of the database
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
     * <p>
     * location=/path/to/your/location
     * user=your_username
     * password=your_password
     * <p>
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

    @Override
    public void run(BiPollerConfiguration configuration, Environment environment) throws Exception {
        SQLUtils.dropEverything(getConnection());

        DistrictDAO districtDAO = new DistrictDAO(getConnection());
        VoterDAO voterDAO = new VoterDAO(getConnection(), districtDAO);

        PollOptionDAO pollOptionDAO = new PollOptionDAO(getConnection());
        PollDAO pollDAO = new PollDAO(getConnection(), pollOptionDAO, voterDAO, districtDAO);
        pollOptionDAO.setPollDAO(pollDAO);
        PollRecordDAO pollRecordDAO = new PollRecordDAO(getConnection(), pollDAO, pollOptionDAO, voterDAO);

        AccessTokenDAO tokenDAO = new AccessTokenDAO(getConnection(), voterDAO);

        districtDAO.createTable();
        voterDAO.createTable();
        pollDAO.createTable();
        pollOptionDAO.createTable();
        pollRecordDAO.createTable();
        tokenDAO.createTable();

        District house = districtDAO.create(1, US.NEW_YORK, CongressionalBody.HOUSE);
        District senate = districtDAO.create(2, US.NEW_YORK, CongressionalBody.SENATE);
        System.out.println("created House with ID: " + house.getId());
        System.out.println("created Senate with ID: " + senate.getId());

        environment.jersey().setUrlPattern("/api/*");

        BiPollerAuthenticator authenticator = new BiPollerAuthenticator(voterDAO, tokenDAO);
        BiPollerAuthFilter filter = new BiPollerAuthFilter(authenticator);
        environment.jersey().register(new AuthFeature(filter));

        environment.jersey().register(new PollResource(pollDAO, pollOptionDAO, pollRecordDAO));
        environment.jersey().register(new AuthResource(authenticator, voterDAO, tokenDAO));
        environment.jersey().register(new SignUpResource(voterDAO, districtDAO));
        environment.jersey().register(new VoterResource(voterDAO));
    }

    @Override
    public void initialize(Bootstrap<BiPollerConfiguration> bootstrap) {
        // this resourcePath is actually ignored - refer to bipoller.yml
        bootstrap.addBundle(new ConfiguredAssetsBundle("/frontend/build/", "/", "index.html"));
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

    /**
     * Runs the server, connecting and immediately disconnecting from the server.
     *
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) throws Exception {
        BiPollerApplication server = new BiPollerApplication();
        server.createConnectionFromConfig();
        server.run(args);
    }

    /**
     * Prints an error message and exits with a non-zero exit code.
     *
     * @param message A message describing the error.
     */
    private static void error(String message) {
        System.out.println("error: " + message);
        System.exit(-1);
    }
}