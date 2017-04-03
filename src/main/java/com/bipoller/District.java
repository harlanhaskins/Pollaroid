package com.bipoller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import unitedstates.US;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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

    public static District getById(Connection conn, long id) throws SQLException {
        String sql = "select * from district where id = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return new District(r);
        } else {
            throw new SQLException("District with id " + id + " not found");
        }
    }

    public static void createTable(Connection connection) throws SQLException {
        PreparedStatement stmt = Utils.prepareStatementFromFile(connection, "sql/create_district_table.sql");
        stmt.execute();
    }

    public static District create(Connection conn, int number, US state, CongressionalBody body) throws SQLException {
        PreparedStatement stmt = Utils.prepareStatementFromFile(conn, "sql/insert_district.sql");
        stmt.setInt(1, number);
        stmt.setString(2, "NY" /* TODO: Fix */);
        stmt.setBoolean(3, body == CongressionalBody.SENATE);
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            long id = keys.getLong(1);
            return District.getById(conn, id);
        } else {
            throw new SQLException("District insert did not return an ID");
        }
    }
}
