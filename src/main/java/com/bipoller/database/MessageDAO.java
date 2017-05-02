package com.bipoller.database;

import com.bipoller.models.District;
import com.bipoller.models.Message;
import com.bipoller.models.Voter;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A DAO for working with Message.
 */
public class MessageDAO extends BiPollerDAO<Voter, Long> {

    private VoterDAO voterDAO;

    public MessageDAO(Connection connection, VoterDAO voterDAO) {
        super(connection);
        this.voterDAO = voterDAO;
    }

    @Override
    public String getSQLCreateTablePath() {
        return "sql/create_mesage_table.sql";
    }

    @Override
    public String getSQLInsertPath() {
        return "sql/insert_message.sql";
    }

    @Override
    public String getSQLGetByIdPath() {
        return "sql/get_message_by_id.sql";
    }

    @Override
    public String getTableName() {
        return "message";
    }

    @Override
    public Message createFromResultSet(ResultSet r) throws SQLException {

        return new Message(r.getLong("id"),
                r.getLong("sender_id"),
                r.getLong("reciever_id"),
                r.getString("message_text"),
                r.getTimestamp("time_sent"),
                r.getString("email"));
    }


    public List<Message> getMessageById(long id) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_message_by_id.sql");
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return Optional.of(createFromResultSet(r));
        }
        return Optional.empty();
    }


    public List<Message> getRepMessagesById(long id) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_rep_messages.sql");
        stmt.setLong(3, id);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return Optional.of(createFromResultSet(r));
        }
        return Optional.empty();
    }

    /**
     * Gets a list of all voters.
     * @return A list of all messages in the database.
     * @throws SQLException If anything went wrong while executing the query.
     */
    public List<Voter> all() throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/all_messages.sql");
        ResultSet results = stmt.executeQuery();
        ArrayList<Voter> voters = new ArrayList<>();
        while (results.next()) {
            voters.add(createFromResultSet(results));
        }
        return voters;
    }

    /**
     *
     * @param from The user sending the message
     * @param to The user recieving the message
     * @param text The message
     * @return a Message type if the Message was created
     * @throws SQLException
     */
    public Message create(Voter from, Voter to, String text) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());

        stmt.setLong(1, from.getId());
        stmt.setLong(2, to.getId());
        stmt.setString(3, text);
        ZonedDateTime time = ZonedDateTime.now(ZoneId.of("UTC"));
        Timestamp newTimestamp = Timestamp.from(time.toInstant());
        stmt.setTimestamp(4,newTimestamp);


        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return getByIdOrThrow(keys.getLong(1));
        }
        throw new SQLException("Voter insert did not return an ID");
    }
}
