package com.bipoller;

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
    private Optional<Long> id;
    private int number;
    private US state;
    private CongressionalBody congressionalBody;

    public District(ResultSet r) throws SQLException {
        this.id = Optional.of(r.getLong(1));
        this.number = r.getInt(2);
    }

    public Optional<Long> getId() {
        return id;
    }

    public void setId(Optional<Long> id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public US getState() {
        return state;
    }

    public void setState(US state) {
        this.state = state;
    }

    public CongressionalBody getCongressionalBody() {
        return congressionalBody;
    }

    public void setCongressionalBody(CongressionalBody congressionalBody) {
        this.congressionalBody = congressionalBody;
    }

    boolean isSenate() {
        return congressionalBody == CongressionalBody.SENATE;
    }

    boolean isHouse() {
        return congressionalBody == CongressionalBody.SENATE;
    }

    public static District getById(Connection conn, long id) throws SQLException {
        String sql = "select * from district where id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        return new District(r);
    }

    public static District create(Connection conn, int number, CongressionalBody body) throws SQLException {
        PreparedStatement stmt = Utils.prepareStatementFromFile(conn, "sql/insert_district.sql");
        stmt.executeUpdate();
        if (stmt.getGeneratedKeys().next()) {
            return District.getById(conn, stmt.getGeneratedKeys().getLong(1));
        } else {
            throw new SQLException("District insert did not return an ID");
        }
    }
}
