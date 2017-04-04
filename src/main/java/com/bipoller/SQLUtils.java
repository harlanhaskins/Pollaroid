package com.bipoller;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUtils {
    /**
     * Creates a prepared statement from the SQL query at the provided path.
     * @param conn The connection creating the statement.
     * @param path The sql file's path.
     * @implNote This will crash if the file is not there.
     */
    @Nonnull
    public static PreparedStatement prepareStatementFromFile(Connection conn, String path) throws SQLException {
        try {
            String sql = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            return conn.prepareStatement(sql);
        } catch (IOException e) {
            System.err.println("Could not find file at " + path + ", aborting...");
            System.exit(-1);
            return null; // Thanks, Java...
        }
    }

    public static void dropEverything(Connection conn) throws SQLException {
        conn.prepareStatement("drop all objects;").execute();
    }
}
