package com.bipoller;

import javax.validation.constraints.NotNull;
import java.sql.*;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public final class AccessToken {
    @NotNull
    private UUID uuid;

    @NotNull
    private long voterID;

    @NotNull
    private ZonedDateTime expiration;

    /**
     * Creates a new AccessToken with the provided values.
     * @param uuid The UUID of the token to create.
     * @param voterID The ID of the voter who owns this token.
     * @param expiration The date time this access token will expire.
     */
    private AccessToken(UUID uuid, long voterID, ZonedDateTime expiration) {
        this.uuid = uuid;
        this.voterID = voterID;
        this.expiration = expiration;
    }

    public AccessToken(ResultSet r) throws SQLException {
        String uuidString = r.getString("uuid");
        this.uuid = UUID.fromString(uuidString);
        this.voterID = r.getInt("voter_id");
        Timestamp expirationStamp = r.getTimestamp("expiration_date");
        this.expiration = ZonedDateTime.ofInstant(expirationStamp.toInstant(),
                ZoneId.of("UTC"));
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getVoterID() {
        return voterID;
    }

    public ZonedDateTime getExpiration() {
        return expiration;
    }

    public boolean isExpired() {
        return getExpiration().isBefore(ZonedDateTime.now(ZoneId.of("UTC")));
    }

    /**
     * Extends the access token to expire two days later, and updates the database.
     */
    public void extendLifetime(Connection conn) throws SQLException {
        this.expiration = getExpiration().plus(Duration.ofDays(2));
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn,
                "sql/update_token_timestamp.sql");
        stmt.setTimestamp(1, Timestamp.from(expiration.toInstant()));
        stmt.setString(2, uuid.toString());
        stmt.executeUpdate();
    }

    /**
     * Deserializes an AccessToken from the currently-pointed row in the provided
     * ResultSet.
     * @param r The result set you're getting data from.
     * @return An access token that corresponds to the data in the pointed-at row in the result set
     * @throws SQLException If anything happened while interacting with the database.
     */
    private static Optional<AccessToken> getFromResultSet(ResultSet r) throws SQLException {
        if (!r.next()) {
            return Optional.empty();
        }
        AccessToken token = new AccessToken(r);
        if (token.isExpired()) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

    /**
     * Gets the access token with the provided Voter ID.
     * @param voterID The ID of the voter for whose token you're looking.
     * @return A optional value that will be present if there is an unexpired token registered for the provided user.
     */
    public static Optional<AccessToken> getByVoterID(Connection conn, long voterID) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/get_token_by_voter_id.sql");
        stmt.setLong(1, voterID);
        return getFromResultSet(stmt.executeQuery());

    }/**
     * Gets the access token with the provided UUID.
     * @param uuid The UUID of the token you're looking for.
     * @return A optional value that will be present if there is an unexpired token with the provided UUID.
     */
    public static Optional<AccessToken> getByUUID(Connection conn, UUID uuid) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/get_token_by_uuid.sql");
        stmt.setString(1, uuid.toString());
        return getFromResultSet(stmt.executeQuery());
    }

    public void delete(Connection conn) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/delete_token.sql");
        stmt.setString(1, uuid.toString());
        stmt.executeUpdate();
    }

    public static AccessToken create(Connection conn, long voterID) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/insert_token.sql");
        UUID newUUID = UUID.randomUUID();
        ZonedDateTime time = ZonedDateTime.now(ZoneId.of("UTC")).plus(Duration.ofDays(2));
        Timestamp newTimestamp = Timestamp.from(time.toInstant());
        stmt.setString(1, newUUID.toString());
        stmt.setLong(2, voterID);
        stmt.setTimestamp(3, newTimestamp);
        stmt.executeUpdate();
        return new AccessToken(newUUID, voterID, time);
    }

    public static void createTable(Connection connection) throws SQLException {
        SQLUtils.prepareStatementFromFile(connection, "sql/create_token_table.sql").execute();
    }
}