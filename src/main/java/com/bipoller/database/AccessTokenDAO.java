package com.bipoller.database;

import com.bipoller.models.AccessToken;
import com.bipoller.models.Voter;

import java.sql.*;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * A DAO for working with AccessTokens.
 */
public class AccessTokenDAO extends BiPollerDAO<AccessToken, UUID> {
    private VoterDAO voterDAO;

    public AccessTokenDAO(Connection connection, VoterDAO voterDAO) {
        super(connection);
        this.voterDAO = voterDAO;
    }

    @Override
    public String getSQLInsertPath() {
        return "sql/insert_token.sql";
    }

    @Override
    public String getSQLGetByIdPath() {
        return "sql/get_token_by_uuid.sql";
    }

    @Override
    public String getTableName() {
        return "token";
    }

    @Override
    public String getSQLCreateTablePath() {
        return "sql/create_token_table.sql";
    }

    @Override
    public AccessToken createFromResultSet(ResultSet r) throws SQLException {
        String uuidString = r.getString("uuid");
        Timestamp expirationStamp = r.getTimestamp("expiration_date");
        long voterID = r.getInt("voter_id");
        return new AccessToken(UUID.fromString(uuidString),
                               voterDAO.getByIdOrThrow(voterID),
                               ZonedDateTime.ofInstant(expirationStamp.toInstant(),
                                  ZoneId.of("UTC")));
    }


    /**
     * Extends the access token to expire two days later, and updates the database.
     */
    public void extendLifetime(AccessToken token) throws SQLException {
        ZonedDateTime expiration = token.getExpiration().plus(Duration.ofDays(2));
        token.setExpiration(expiration);
        PreparedStatement stmt = prepareStatementFromFile("sql/update_token_timestamp.sql");
        stmt.setTimestamp(1, Timestamp.from(expiration.toInstant()));
        stmt.setString(2, token.getUuid().toString());
        stmt.executeUpdate();
    }

    /**
     * Deserializes an AccessToken from the currently-pointed row in the provided
     * ResultSet.
     * @param r The result set you're getting data from.
     * @return An access token that corresponds to the data in the pointed-at row in the result set
     * @throws SQLException If anything happened while interacting with the database.
     */
    private Optional<AccessToken> getIfNotExpired(ResultSet r) throws SQLException {
        if (!r.next()) {
            return Optional.empty();
        }
        AccessToken token = createFromResultSet(r);
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
    public Optional<AccessToken> getByVoterID(long voterID) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_token_by_voter_id.sql");
        stmt.setLong(1, voterID);
        return getIfNotExpired(stmt.executeQuery());
    }

    public void delete(AccessToken token) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/delete_token.sql");
        stmt.setString(1, token.getUuid().toString());
        stmt.executeUpdate();
    }

    public AccessToken create(Voter voter) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/insert_token.sql");
        UUID newUUID = UUID.randomUUID();
        ZonedDateTime time = ZonedDateTime.now(ZoneId.of("UTC")).plus(Duration.ofDays(2));
        Timestamp newTimestamp = Timestamp.from(time.toInstant());
        stmt.setString(1, newUUID.toString());
        stmt.setLong(2, voter.getId());
        stmt.setTimestamp(3, newTimestamp);
        stmt.executeUpdate();
        return new AccessToken(newUUID, voter, time);
    }
}
