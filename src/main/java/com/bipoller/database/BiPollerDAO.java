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
 * An abstract class that defines
 */
public abstract class BiPollerDAO<T, IdType> {
    protected Connection connection;

    public BiPollerDAO(Connection connection) {
        this.connection = connection;
    }

    public abstract String getTableName();

    public abstract String getSQLCreateTablePath();
    public abstract String getSQLInsertPath();
    public abstract String getSQLGetByIdPath();

    public abstract T createFromResultSet(ResultSet r) throws SQLException;

    public void createTable() throws SQLException {
        prepareStatementFromFile(getSQLCreateTablePath()).execute();
    }

    public Optional<T> getById(IdType id) throws SQLException {
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
            System.err.println("Could not find file at " + path + ", aborting...");
            System.exit(-1);
            return null; // Thanks, Java...
        }
    }
}
