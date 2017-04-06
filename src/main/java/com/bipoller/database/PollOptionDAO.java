package com.bipoller.database;

import com.bipoller.models.Poll;
import com.bipoller.models.PollOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A DAO for working with PollOptions.
 */
public class PollOptionDAO extends BiPollerDAO<PollOption, Long> {
    private PollDAO pollDAO;

    public PollOptionDAO(Connection connection) {
        super(connection);
    }

    @Override
    public String getSQLCreateTablePath() {
        return "sql/create_poll_option_table.sql";
    }

    @Override
    public String getTableName() {
        return "poll_option";
    }

    @Override
    public String getSQLGetByIdPath() {
        return "sql/get_poll_option_by_id.sql";
    }

    @Override
    public String getSQLInsertPath() {
        return "sql/insert_poll_option.sql";
    }

    public void setPollDAO(PollDAO pollDAO) {
        this.pollDAO = pollDAO;
    }

    public Poll getPoll(PollOption option) throws SQLException {
        return pollDAO.getByIdOrThrow(option.getPollID());
    }

    @Override
    public PollOption createFromResultSet(ResultSet r) throws SQLException {
        return new PollOption(r.getLong("id"),
                              r.getLong("poll_id"),
                              r.getString("option"));
    }

    public PollOption create(long pollID, String option) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());
        stmt.setLong(1, pollID);
        stmt.setString(2, option);

        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return getByIdOrThrow(keys.getLong(1));
        }
        throw new SQLException("PollOption insert did not return ID");
    }
}