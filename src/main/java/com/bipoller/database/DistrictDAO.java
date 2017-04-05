package com.bipoller.database;

import com.bipoller.models.CongressionalBody;
import com.bipoller.models.District;
import unitedstates.US;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A DAO that traffics in Districts.
 */
public class DistrictDAO extends BiPollerDAO<District, Long> {
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
    public District createFromResultSet(ResultSet r) throws SQLException {
        return new District(r.getLong("id"),
                            r.getInt("district_num"),
                            US.parse(r.getString("state")),
                            r.getBoolean("is_senate") ?
                                CongressionalBody.HOUSE : CongressionalBody.SENATE);
    }

    public District create(int number, US state, CongressionalBody body) throws SQLException {
        PreparedStatement stmt = prepareStatementFromFile(getSQLInsertPath());
        stmt.setInt(1, number);
        stmt.setString(2, "NY" /* TODO: Fix */);
        stmt.setBoolean(3, body == CongressionalBody.SENATE);
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return getByIdOrThrow(keys.getLong(1));
        }
        throw new SQLException("District insert did not return an ID");
    }
}
