package com.pollaroid;

import com.pollaroid.auth.AuthFeature;
import com.pollaroid.auth.PollaroidAuthFilter;
import com.pollaroid.auth.PollaroidAuthenticator;
import com.pollaroid.dummydata.*;
import com.pollaroid.database.*;
import com.pollaroid.models.*;
import com.pollaroid.resources.*;
import io.dropwizard.Application;
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.joda.time.DateTimeZone;
import unitedstates.US;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.*;
import java.sql.*;
import java.util.EnumSet;
import java.util.Properties;

/**
 * Runs the main Pollaroid server that connects to the database.
 */
public class PollaroidApplication extends Application<PollaroidConfiguration> {

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
    public void run(PollaroidConfiguration configuration, Environment environment) throws Exception {
        DistrictDAO districtDAO = new DistrictDAO(getConnection());
        VoterDAO voterDAO = new VoterDAO(getConnection(), districtDAO);
        districtDAO.setVoterDAO(voterDAO);

        PollOptionDAO pollOptionDAO = new PollOptionDAO(getConnection());
        PollDAO pollDAO = new PollDAO(getConnection(), pollOptionDAO, voterDAO, districtDAO);
        pollOptionDAO.setPollDAO(pollDAO);
        PollRecordDAO pollRecordDAO = new PollRecordDAO(getConnection(), pollDAO, pollOptionDAO, voterDAO);
        MessageDAO messageDAO = new MessageDAO(getConnection(), voterDAO);

        AccessTokenDAO tokenDAO = new AccessTokenDAO(getConnection(), voterDAO);

        districtDAO.createTable();
        voterDAO.createTable();
        pollDAO.createTable();
        pollOptionDAO.createTable();
        messageDAO.createTable();
        pollRecordDAO.createTable();
        tokenDAO.createTable();

        environment.jersey().setUrlPattern("/api/*");

        PollaroidAuthenticator authenticator = new PollaroidAuthenticator(voterDAO, tokenDAO);
        PollaroidAuthFilter filter = new PollaroidAuthFilter(authenticator);
        environment.jersey().register(new AuthFeature(filter));

        environment.jersey().register(new MessageResource(messageDAO, voterDAO));
        environment.jersey().register(new DistrictResource(districtDAO));
        environment.jersey().register(new PollResource(pollDAO, pollOptionDAO, pollRecordDAO));
        environment.jersey().register(new AuthResource(authenticator, voterDAO, tokenDAO));
        environment.jersey().register(new SignUpResource(voterDAO, districtDAO, tokenDAO));
        environment.jersey().register(new VoterResource(voterDAO));

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "*");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // DO NOT pass a preflight request to down-stream auth filters
        // unauthenticated preflight requests should be permitted by spec
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }

    @Override
    public void initialize(Bootstrap<PollaroidConfiguration> bootstrap) {
        // this resourcePath is actually ignored - refer to pollaroid.yml
        bootstrap.addBundle(new ConfiguredAssetsBundle("/frontend/build/", "/", "index.html"));
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

    /**
     * Runs the server, connecting and immediately disconnecting from the server.
     *
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) throws Exception {
        PollaroidApplication server = new PollaroidApplication();
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
