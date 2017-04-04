package com.bipoller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.criterion.Distinct;
import unitedstates.US;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


enum CongressionalBody {
    HOUSE, SENATE
}

public class District {
    private long id;
    private int number;
    private US state;

    @JsonIgnore
    private CongressionalBody congressionalBody;

    public District(ResultSet r) throws SQLException {
        this.id = r.getLong("id");
        this.number = r.getInt("district_num");
        this.state = US.parse(r.getString("state"));
        this.congressionalBody = r.getBoolean("is_senate") ?
                CongressionalBody.HOUSE : CongressionalBody.SENATE;
    }

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public US getState() {
        return state;
    }

    public CongressionalBody getCongressionalBody() {
        return congressionalBody;
    }

    @JsonProperty
    public boolean isSenate() {
        return congressionalBody == CongressionalBody.SENATE;
    }

    public boolean isHouse() {
        return congressionalBody == CongressionalBody.SENATE;
    }

    public static Optional<District> getById(Connection conn, long id) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/get_district_by_id.sql");
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return Optional.of(new District(r));
        }
        return Optional.empty();
    }

    public static District getByIdOrThrow(Connection conn, long id) throws SQLException {
        Optional<District> district = getById(conn, id);
        if (district.isPresent()) {
            return district.get();
        }
        throw new SQLException("District with id " + id + " not found.");
    }

    public static void createTable(Connection connection) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(connection, "sql/create_district_table.sql");
        stmt.execute();
    }

    public static District create(Connection conn, int number, US state, CongressionalBody body) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/insert_district.sql");
        stmt.setInt(1, number);
        stmt.setString(2, "NY" /* TODO: Fix */);
        stmt.setBoolean(3, body == CongressionalBody.SENATE);
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            Optional<District> district = District.getById(conn, keys.getLong(1));
            if (district.isPresent()) {
                return district.get();
            } else {
                throw new SQLException("District insert ID was invalid");
            }
        } else {
            throw new SQLException("District insert did not return an ID");
        }
    }
}
