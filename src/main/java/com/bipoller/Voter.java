package com.bipoller;

import java.sql.*;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mindrot.jbcrypt.BCrypt;

public class Voter {
    private long id;
    private String name;
    private District houseDistrict;
    private District senateDistrict;
    private String phoneNumber;
    private String address;
    private String email;

    @JsonIgnore // Don't return the password hash when serializing
    private String passwordHash;

    private Optional<District> representedDistrict;

    public long getId() {
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

    public Optional<District> getRepresentedDistrict() {
        return representedDistrict;
    }

    public Voter(Connection conn, ResultSet r) throws SQLException {
        this.id = r.getLong("id");
        this.name = r.getString("name");
        this.passwordHash = r.getString("password_hash");
        long houseDistrictID = r.getLong("house_district_id");
        this.houseDistrict = District.getById(conn, houseDistrictID);
        long senateDistrictID = r.getLong("senate_district_id");
        this.senateDistrict = District.getById(conn, senateDistrictID);
        this.phoneNumber = r.getString("phone_number");
        this.address = r.getString("address");
        this.email = r.getString("email");
    }

    public static void createTable(Connection connection) throws SQLException {
        PreparedStatement stmt = Utils.prepareStatementFromFile(connection, "sql/create_voter_table.sql");
        stmt.execute();
    }

    public static Voter create(Connection conn, String name, String password, District houseDistrict,
                               District senateDistrict, String phoneNumber, String address, String email,
                               Optional<District> representedDistrict) throws SQLException {
        PreparedStatement stmt = Utils.prepareStatementFromFile(conn, "sql/insert_voter.sql");
        stmt.setString(1, name);
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        stmt.setString(2, hash);
        stmt.setLong(3, houseDistrict.getId());
        stmt.setLong(4, senateDistrict.getId());
        stmt.setString(5, phoneNumber);
        stmt.setString(6, address);
        stmt.setString(7, email);
        if (representedDistrict.isPresent()) {
            stmt.setLong(8, representedDistrict.get().getId());
        } else {
            stmt.setNull(8, Types.INTEGER);
        }
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return Voter.getById(conn, keys.getLong(1));
        } else {
            throw new SQLException("Voter insert did not return an ID");
        }
    }

    public static Voter getById(Connection conn, long id) throws SQLException {
        String sql = "select * from voter where id = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return new Voter(conn, r);
        } else {
            throw new SQLException("Voter with id " + id + " not found");
        }
    }
}
