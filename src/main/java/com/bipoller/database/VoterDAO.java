package com.bipoller.database;

import com.bipoller.models.District;
import com.bipoller.models.Voter;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by harlan on 4/5/17.
 */
public class VoterDAO extends BiPollerDAO<Voter, Long> {

    private DistrictDAO districtDAO;

    public VoterDAO(Connection connection, DistrictDAO districtDAO) {
        super(connection);
        this.districtDAO = districtDAO;
    }

    @Override
    public String getSQLCreateTablePath() {
        return "sql/create_voter_table.sql";
    }

    @Override
    public String getSQLInsertPath() {
        return "sql/insert_voter.sql";
    }

    @Override
    public String getSQLGetByIdPath() {
        return "sql/get_voter_by_id.sql";
    }

    @Override
    public String getTableName() {
        return "voter";
    }

    @Override
    public Voter createFromResultSet(ResultSet r) throws SQLException {
        long houseDistrictID  = r.getLong("house_district_id");
        long senateDistrictID = r.getLong("senate_district_id");


        Optional<District> representingDistrict = Optional.empty();

        // Have to get this as an Object and cast.
        Long representingDistrictID = (Long)r.getObject("representing_district_id");
        if (representingDistrictID != null) {
            representingDistrict = districtDAO.getById(representingDistrictID);
        }

        return new Voter(r.getLong("id"),
                         r.getString("name"),
                         r.getString("password_hash"),
                         r.getString("phone_number"),
                         r.getString("address"),
                         r.getString("email"),
                         // Throw a SQLException if we've stored a reference to a district that doesn't exist.
                         // This Shouldn't Happen™
                         districtDAO.getByIdOrThrow(houseDistrictID),
                         districtDAO.getByIdOrThrow(senateDistrictID),
                         representingDistrict);
    }


    public Optional<Voter> getByEmail(String email) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_voter_by_email.sql");
        stmt.setString(1, email);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return Optional.of(createFromResultSet(r));
        }
        return Optional.empty();
    }

    /**
     * Gets a list of all voters.
     * @return A list of all voters in the database.
     * @throws SQLException
     */
    public List<Voter> all() throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/all_voters.sql");
        ResultSet results = stmt.executeQuery();
        ArrayList<Voter> voters = new ArrayList<>();
        while (results.next()) {
            voters.add(createFromResultSet(results));
        }
        return voters;
    }


    public Voter create(String name, String password, Long houseDistrictID,
                        Long senateDistrictID, String phoneNumber, String address, String email,
                        Optional<Long> representedDistrictID) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());

        stmt.setString(1, name);
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        stmt.setString(2, hash);
        stmt.setLong(3, houseDistrictID);
        stmt.setLong(4, senateDistrictID);
        stmt.setString(5, phoneNumber);
        stmt.setString(6, address);
        stmt.setString(7, email);

        if (representedDistrictID.isPresent()) {
            stmt.setLong(8, representedDistrictID.get());
        } else {
            stmt.setNull(8, Types.INTEGER);
        }

        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return getByIdOrThrow(keys.getLong(1));
        }
        throw new SQLException("Voter insert did not return an ID");
    }
}
