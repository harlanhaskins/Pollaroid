package com.pollaroid.database;

import com.pollaroid.models.Message;
import com.pollaroid.models.Voter;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A DAO for working with Message.
 */
public class MessageDAO extends PollaroidDAO<Message, Long> {

    private VoterDAO voterDAO;

    public MessageDAO(Connection connection, VoterDAO voterDAO) {
        super(connection);
        this.voterDAO = voterDAO;
    }

    @Override
    public String getSQLCreateTablePath() {
        return "sql/create_message_table.sql";
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
        Timestamp ts = r.getTimestamp("time_sent");
        ZonedDateTime utcDateTime = ZonedDateTime.ofInstant(ts.toInstant(), ZoneId.of("UTC"));
        return new Message(r.getLong("id"),
                r.getLong("sender_id"),
                r.getLong("receiver_id"),
                r.getString("message_text"),
                utcDateTime);
    }





    public List<Message> getRepMessagesById(long id) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_rep_messages.sql");
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        ArrayList<Message> messages = new ArrayList<>();
        while (r.next()) {
            messages.add(createFromResultSet(r));
        }
        return messages;
    }

    public List<Message> getSentMessagesById(long id) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_sent_messages.sql");
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        ArrayList<Message> messages = new ArrayList<>();
        while (r.next()) {
            messages.add(createFromResultSet(r));
        }
        return messages;
    }

    public List<Message> getConversationById(long senderId, long receiverId) throws SQLException{
        PreparedStatement stmt = prepareStatementFromFile("sql/get_message_conversation.sql");
        stmt.setLong(1, senderId);
        stmt.setLong(2,receiverId);
        ResultSet r = stmt.executeQuery();
        ArrayList<Message> messages = new ArrayList<>();
        while (r.next()) {
            messages.add(createFromResultSet(r));
        }
        return messages;
    }

    /**
     * Gets a list of all voters.
     * @return A list of all messages in the database.
     * @throws SQLException If anything went wrong while executing the query.
     */
    public List<Message> all() throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/all_messages.sql");
        ResultSet results = stmt.executeQuery();
        ArrayList<Message> messages = new ArrayList<>();
        while (results.next()) {
            messages.add(createFromResultSet(results));
        }
        return messages;
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
