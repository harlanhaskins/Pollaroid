package com.bipoller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class Voter {
    private Optional<Long> id;
    private String name;
    private District houseDistrict;
    private District senateDistrict;
    private String phoneNumber;
    private String address;
    private String email;
    private String passwordHash;

    public Optional<Long> getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public District getHouseDistrict() {
        return houseDistrict;
    }

    public District getSenateDistrict() {
        return senateDistrict;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Voter(Connection conn, ResultSet r) throws SQLException {
        this.id = Optional.of(r.getLong(1));
        this.name = r.getString(2);
        this.passwordHash = r.getString(3);
        long houseDistrictID = r.getLong(4);
        this.houseDistrict = District.getById(conn, houseDistrictID);
        long senateDistrictID = r.getLong(5);
        this.senateDistrict = District.getById(conn, senateDistrictID);
        this.phoneNumber = r.getString(6);
        this.address = r.getString(7);
        this.email = r.getString(8);
    }

    public static void createTable(Connection connection) throws SQLException {
        PreparedStatement stmt = Utils.prepareStatementFromFile(connection, "sql/create_voter.sql");
        stmt.execute();
    }

    public static Voter create(Connection conn, String name, String password, District houseDistrict,
                               District senateDistrict, String phoneNumber, String address, String email) throws SQLException {
        PreparedStatement stmt = Utils.prepareStatementFromFile(conn, "sql/insert_voter.sql");
        stmt.setString(1, name);
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        stmt.setString(2, hash);
        if (houseDistrict.getId().isPresent()) {
            stmt.setLong(3, houseDistrict.getId().get());
        }
        if (senateDistrict.getId().isPresent()) {
            stmt.setLong(4, senateDistrict.getId().get());
        }
        stmt.setString(5, phoneNumber);
        stmt.setString(6, address);
        stmt.setString(7, email);
        stmt.executeUpdate();
        if (stmt.getGeneratedKeys().next()) {
            return Voter.getById(conn, stmt.getGeneratedKeys().getLong(1));
        } else {
            throw new SQLException("Voter insert did not return an ID");
        }
    }

    public static Voter getById(Connection conn, long id) throws SQLException {
        String sql = "select * from voter where id = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        return new Voter(conn, r);
    }
}
