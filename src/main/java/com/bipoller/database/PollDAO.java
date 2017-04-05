package com.bipoller.database;

import com.bipoller.models.District;
import com.bipoller.models.Poll;
import com.bipoller.models.Voter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A DAO for working with Polls.
 */
public class PollDAO extends BiPollerDAO<Poll, Long> {

    VoterDAO voterDAO;
    DistrictDAO districtDAO;

    public PollDAO(Connection connection, VoterDAO voterDAO, DistrictDAO districtDAO) {
        super(connection);
        this.voterDAO = voterDAO;
        this.districtDAO = districtDAO;
    }

    @Override
    public String getSQLCreateTablePath() {
        return "sql/create_poll_table.sql";
    }

    @Override
    public String getTableName() {
        return "poll";
    }

    @Override
    public String getSQLGetByIdPath() {
        return "sql/get_poll_by_id.sql";
    }

    @Override
    public String getSQLInsertPath() {
        return "sql/insert_poll.sql";
    }

    @Override
    public Poll createFromResultSet(ResultSet r) throws SQLException {
        return new Poll(r.getLong("id"),
                        voterDAO.getByIdOrThrow(r.getLong("submitter_id")),
                        districtDAO.getByIdOrThrow(r.getLong("district_id")),
                        r.getString("title"));
    }

    /**
     * Creates a new Poll in the database with the provided fields.
     * @param submitter The submitter of the poll.
     * @param district The district the poll applies to.
     * @param title The title of the poll.
     * @return A new Poll if it was created successfully.
     * @throws SQLException If there was a problem interacting with the database.
     */
    public Poll create(Voter submitter, District district, String title) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());
        stmt.setLong(1, submitter.getId());
        stmt.setLong(2, district.getId());
        stmt.setString(3, title);
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return getByIdOrThrow(keys.getLong(1));
        }
        throw new SQLException("Poll insert did not return an ID");
    }
}
