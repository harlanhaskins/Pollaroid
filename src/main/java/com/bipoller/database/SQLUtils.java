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
import java.util.function.Function;

public class SQLUtils {
    public static void dropEverything(Connection conn) throws SQLException {
        conn.prepareStatement("drop all objects;").execute();
    }
}
