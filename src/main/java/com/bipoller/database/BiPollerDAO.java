package com.bipoller.database;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * An abstract class that defines a Data Access Object that's responsible for marshalling Java objects to and from
 * the database.
 */
public abstract class BiPollerDAO<T, IdType> {
    protected Connection connection;

    public BiPollerDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * The name of the table this DAO will be interacting with.
     * @return A SQL table name.
     */
    public abstract String getTableName();

    /**
     * @return A path to a SQL file that contains the command to create the table this DAO is interfacing with.
     */
    public abstract String getSQLCreateTablePath();

    /**
     * @return A path to a SQL file that contains the command to insert a new object into the table.
     */
    public abstract String getSQLInsertPath();

    /**
     * @return A path to a SQL file that contains the command to get the object keyed by the DAO's IdType
     *         from the table.
     */
    public abstract String getSQLGetByIdPath();

    /**
     * Creates a Java object from the provided ResultSet. This will read the rows in the result set one-by-one
     * and return a fully-formed object from those rows.
     * @param r The ResultSet to deserialize from.
     * @return A fully-formed Java object containing the deserialized values.
     * @throws SQLException If the result set was, in any way, invalid.
     */
    public abstract T createFromResultSet(ResultSet r) throws SQLException;

    /**
     * Creates the table this DAO represents.
     * @throws SQLException If anything went wrong creating the table.
     */
    public void createTable() throws SQLException {
        prepareStatementFromFile(getSQLCreateTablePath()).execute();
    }

    /**
     * Gets an object from the table keyed by the provided ID, if such an object exists.
     * @param id The object's ID in the database.
     * @return The object in the database with the provided ID, or Optional.empty() if there was no object.
     * @throws SQLException If there was a problem interacting with the database. If an object was not found, then
     *                      this method will return Optional.empty(), not throw.
     */
    public Optional<T> getById(IdType id) throws SQLException {

        // If they passed null for the ID, just give them empty().
        if (id == null) {
            return Optional.empty();
        }

        PreparedStatement stmt = prepareStatementFromFile(getSQLGetByIdPath());

        // There isn't a possible way to be generic on this insertion, so if we have an ID that's not a
        // String, Integer, or Long, then we'll have to manually add it here.
        if (id instanceof Long) {
            stmt.setLong(1, (Long)id);
        } else if (id instanceof String) {
            stmt.setString(1, (String)id);
        } else if (id instanceof Integer) {
            stmt.setInt(1, (Integer)id);
        } else if (id instanceof UUID) {
            stmt.setString(1, id.toString());
        } else {
            System.err.println("Invalid ID value: " + id);
            System.exit(-1);
        }

        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return Optional.of(createFromResultSet(r));
        }
        return Optional.empty();
    }

    /**
     * Gets an object from the table keyed by the provided ID, if such an object exists, or throws a SQLException.
     * @param id The object's ID in the database.
     * @return The object in the database with the provided ID.
     * @throws SQLException If there was a problem interacting with the database, or if the object was not found.
     */
    public T getByIdOrThrow(IdType id) throws SQLException {
        Optional<T> value = getById(id);
        if (value.isPresent()) {
            return value.get();
        }
        throw new SQLException("`" + getTableName() + "` with id `" + id + "` not found.");
    }

    /**
     * Creates a prepared statement from the SQL query at the provided path.
     * @param path The sql file's path.
     * @implNote This will crash if the file is not there.
     */
    @Nonnull
    protected PreparedStatement prepareStatementFromFile(String path) throws SQLException {
        try {
            String sql = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            return connection.prepareStatement(sql);
        } catch (IOException e) {
            throw new SQLException("Could not find file at " + path + ", aborting...");
        }
    }
}
