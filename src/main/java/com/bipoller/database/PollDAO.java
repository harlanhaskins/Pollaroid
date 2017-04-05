package com.bipoller.database;

import com.bipoller.models.District;
import com.bipoller.models.Poll;
import com.bipoller.models.Voter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by harlan on 4/5/17.
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
