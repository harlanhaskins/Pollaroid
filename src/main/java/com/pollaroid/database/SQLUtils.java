package com.pollaroid.database;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLUtils {
    /**
     * Deletes everything, tables included, from the database.
     * @param conn The connection to the database.
     * @throws SQLException If anything went wrong while executing the query.
     */
    public static void dropEverything(Connection conn) throws SQLException {
        conn.prepareStatement("drop all objects;").execute();
    }
}
