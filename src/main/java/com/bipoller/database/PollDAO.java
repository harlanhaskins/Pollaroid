package com.bipoller.database;

import com.bipoller.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A DAO for working with Polls.
 */
public class PollDAO extends BiPollerDAO<Poll, Long> {
    private PollOptionDAO pollOptionDAO;
    private VoterDAO voterDAO;
    private DistrictDAO districtDAO;

    public PollDAO(Connection connection, PollOptionDAO pollOptionDAO, VoterDAO voterDAO, DistrictDAO districtDAO) {
        super(connection);
        this.pollOptionDAO = pollOptionDAO;
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

    private List<PollOption> getOptions(long id) throws SQLException {
        ArrayList<PollOption> options = new ArrayList<>();
        PreparedStatement stmt = prepareStatementFromFile("sql/get_options_for_poll.sql");
        stmt.setLong(1, id);

        ResultSet r = stmt.executeQuery();
        while (r.next()) {
            options.add(pollOptionDAO.createFromResultSet(r));
        }

        return options;
    }

    public List<Poll> getPollsInDistricts(District house, District senate) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_polls_in_districts.sql");
        stmt.setLong(1, house.getId());
        stmt.setLong(2, senate.getId());
        ResultSet r = stmt.executeQuery();

        ArrayList<Poll> polls = new ArrayList<>();
        while (r.next()) {
            polls.add(createFromResultSet(r));
        }
        return polls;
    }

    @Override
    public Poll createFromResultSet(ResultSet r) throws SQLException {
        long id = r.getLong("id");
        return new Poll(id,
                        voterDAO.getByIdOrThrow(r.getLong("submitter_id")),
                        districtDAO.getByIdOrThrow(r.getLong("district_id")),
                        r.getString("title"),
                        getOptions(id));
    }

    /**
     * Creates a new Poll in the database with the provided fields.
     * @param submitter The submitter of the poll.
     * @param district The district the poll applies to.
     * @param title The title of the poll.
     * @return A new Poll if it was created successfully.
     * @throws SQLException If there was a problem interacting with the database.
     */
    public Poll create(Voter submitter, District district, String title, List<String> options) throws SQLException {
        return executeInTransaction(() -> {
            PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());
            stmt.setLong(1, submitter.getId());
            stmt.setLong(2, district.getId());
            stmt.setString(3, title);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                long id = keys.getLong(1);

                // Create options for the new poll...
                for (String option : options) {
                    pollOptionDAO.create(id, option);
                }

                return getByIdOrThrow(id);
            }
            throw new SQLException("Poll insert did not return an ID");
        });
    }

    public List<Poll> getTopPolls(int numberOfPolls) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_top_polls.sql");
        stmt.setInt(1, numberOfPolls);
        ResultSet r = stmt.executeQuery();

        ArrayList<Poll> polls = new ArrayList<>();
        while (r.next()) {
            polls.add(createFromResultSet(r));
        }
        return polls;
    }
}
