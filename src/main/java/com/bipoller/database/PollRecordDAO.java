package com.bipoller.database;

import com.bipoller.models.Poll;
import com.bipoller.models.PollOption;
import com.bipoller.models.PollRecord;
import com.bipoller.models.Voter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A DAO for inserting and deleting PollRecords.
 */
public class PollRecordDAO extends BiPollerDAO<PollRecord, Long> {
    private PollDAO pollDAO;
    private PollOptionDAO pollOptionDAO;
    private VoterDAO voterDAO;

    public PollRecordDAO(Connection connection, PollDAO pollDAO, PollOptionDAO pollOptionDAO, VoterDAO voterDAO) {
        super(connection);
        this.pollDAO = pollDAO;
        this.pollOptionDAO = pollOptionDAO;
        this.voterDAO = voterDAO;
    }

    @Override
    public String getSQLInsertPath() {
        return "sql/insert_poll_record.sql";
    }

    @Override
    public String getSQLGetByIdPath() {
        return "sql/get_poll_record_by_id.sql";
    }

    @Override
    public String getTableName() {
        return "poll_record";
    }

    @Override
    public String getSQLCreateTablePath() {
        return "sql/create_poll_record_table.sql";
    }

    @Override
    public PollRecord createFromResultSet(ResultSet r) throws SQLException {
        long id = r.getLong("id");
        Poll poll = pollDAO.getByIdOrThrow(r.getLong("poll_id"));
        PollOption option = pollOptionDAO.getByIdOrThrow(r.getLong("option_id"));
        Voter voter = voterDAO.getByIdOrThrow(r.getLong("voter_id"));
        return new PollRecord(id, poll, voter, option);
    }

    /**
     * Gets all responses to the poll.
     * @param poll The poll you're inspecting.
     * @return All recorded responses to that poll.
     * @throws SQLException if anything happened retrieving the data.
     */
    public List<PollRecord> getResponses(Poll poll) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_all_poll_records_by_poll_id.sql");
        stmt.setLong(1, poll.getId());
        ResultSet r = stmt.executeQuery();

        ArrayList<PollRecord> records = new ArrayList<>();
        while (r.next()) {
            records.add(createFromResultSet(r));
        }
        return records;
    }

    public Optional<PollRecord> getVoterResponse(Voter voter, Poll poll) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_poll_record_by_voter_and_poll.sql");
        stmt.setLong(1, voter.getId());
        stmt.setLong(2, voter.getId());
        ResultSet r = stmt.executeQuery();

        if (r.next()) {
            return Optional.of(createFromResultSet(r));
        }
        return Optional.empty();
    }

    public void delete(PollRecord record) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/delete_poll_record.sql");
        stmt.setLong(1, record.getId());
        stmt.execute();
    }

    public PollRecord create(Poll poll, PollOption option, Voter voter) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());
        stmt.setLong(1, poll.getId());
        stmt.setLong(2, option.getId());
        stmt.setLong(3, voter.getId());
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return getByIdOrThrow(keys.getLong(1));
        }
        throw new SQLException("PollRecord insert did not return ID");
    }
}
