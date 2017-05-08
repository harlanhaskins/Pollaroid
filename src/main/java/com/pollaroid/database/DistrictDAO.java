package com.pollaroid.database;

import com.pollaroid.models.CongressionalBody;
import com.pollaroid.models.District;
import com.pollaroid.models.Voter;
import lombok.Setter;
import unitedstates.US;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A DAO for working with Districts.
 */
public class DistrictDAO extends PollaroidDAO<District, Long> {
    @Setter
    private VoterDAO voterDAO;

    public DistrictDAO(Connection connection) {
        super(connection);
    }

    @Override
    public String getSQLInsertPath() {
        return "sql/insert_district.sql";
    }

    @Override
    public String getSQLGetByIdPath() {
        return "sql/get_district_by_id.sql";
    }

    @Override
    public String getTableName() {
        return "district";
    }

    @Override
    public String getSQLCreateTablePath() {
        return "sql/create_district_table.sql";
    }

    @Override
    public String[] getIndexPaths() {
        return new String[] { "sql/create_district_index.sql" };
    }

    @Override
    public District createFromResultSet(ResultSet r) throws SQLException {
        return new District(r.getLong("id"),
                            r.getInt("district_num"),
                            US.parse(r.getString("state")),
                            r.getBoolean("is_senate") ?
                                CongressionalBody.HOUSE : CongressionalBody.SENATE);
    }

    public Optional<District> getByFields(int number, US state, CongressionalBody body) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_district_by_num_state_is_senate.sql");
        stmt.setInt(1, number);
        stmt.setString(2, state.getANSIAbbreviation());
        stmt.setBoolean(3, body.isSenate());
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return Optional.of(createFromResultSet(r));
        }
        return Optional.empty();
    }

    public District getByFieldsOrThrow(int number, US state, CongressionalBody body) throws SQLException {
        Optional<District> d = getByFields(number, state, body);
        if (d.isPresent()) {
            return d.get();
        }
        throw new SQLException("District not found with fields (number: " + number
                             + ", state: " + state.getANSIAbbreviation() + ", body: " + body + ")");
    }

    /**
     * Gets the representative of this district, if it has one registered.
     * @param districtID The district whose representative you're looking up.
     * @return The voter who is registered as a representative for this district, if there is one.
     * @throws SQLException If there was a SQL error during the query.
     */
    public Optional<Voter> getRepresentative(long districtID) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/get_rep_for_district.sql");
        stmt.setLong(1, districtID);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            Voter v = voterDAO.createFromResultSet(r);

            return Optional.ofNullable(v);
        }
        return Optional.empty();
    }

    /**
     * Gets all districts registered in the Pollaroid database.
     * @return A list of all districts, house and Senate.
     * @throws SQLException if a SQL error occurred.
     */
    public List<District> all() throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile("sql/all_districts.sql");
        ResultSet r = stmt.executeQuery();
        ArrayList<District> districts = new ArrayList<>();
        while (r.next()) {
            districts.add(createFromResultSet(r));
        }
        return districts;
    }

    /**
     * Creates a new District in the database from the constituent parts.
     * @param number The district's number.
     * @param state The state the district is located in.
     * @param body The congressional body of this district.
     * @return A fully-formed District object, if it was created successfully.
     * @throws SQLException If the database did not successfully create the district.
     */
    public District create(int number, US state, CongressionalBody body) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());
        stmt.setInt(1, number);
        stmt.setString(2, state.getANSIAbbreviation());
        stmt.setBoolean(3, body == CongressionalBody.SENATE);
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return getByIdOrThrow(keys.getLong(1));
        }
        throw new SQLException("District insert did not return an ID");
    }
}
