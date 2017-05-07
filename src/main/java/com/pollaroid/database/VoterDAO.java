package com.pollaroid.database;

import com.pollaroid.models.District;
import com.pollaroid.models.Voter;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A DAO for working with Voters.
 */
public class VoterDAO extends PollaroidDAO<Voter, Long> {

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
    public String[] getIndexPaths() {
        return new String[] { "sql/create_voter_index.sql" };
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

    /**
     * Gets a specific voter by the provided email, if one exists.
     * @param email The email you're looking up.
     * @return A Voter from the Database, if one exists with that email.
     * @throws SQLException If something went wrong connecting to the database.
     */
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
     * @throws SQLException If anything went wrong while executing the query.
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

    /**
     * Creates a new Voter in the database with the provided fields.
     * @param name The voter's full name.
     * @param password The voter's new password. (Will not be stored directly.)
     * @param houseDistrict The House district this voter will be created in.
     * @param senateDistrict The Senate district this voter will be created in.
     * @param phoneNumber The voter's phone number.
     * @param address The voter's address.
     * @param email The voter's email.
     * @param representingDistrict The district this voter represents, or .empty() if this user is not a representative.
     * @return A fully-formed Voter that's now in the database.
     * @throws SQLException If anything went wrong talking to the database.
     */
    public Voter create(String name, String password, District houseDistrict,
                        District senateDistrict, String phoneNumber, String address, String email,
                        Optional<District> representingDistrict) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());

        stmt.setString(1, name);
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        stmt.setString(2, hash);
        stmt.setLong(3, houseDistrict.getId());
        stmt.setLong(4, senateDistrict.getId());
        stmt.setString(5, phoneNumber);
        stmt.setString(6, address);
        stmt.setString(7, email);

        if (representingDistrict.isPresent()) {
            stmt.setLong(8, representingDistrict.get().getId());
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
