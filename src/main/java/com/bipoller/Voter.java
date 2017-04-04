package com.bipoller;

import java.security.Principal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mindrot.jbcrypt.BCrypt;

public class Voter implements Principal {
    private long id;
    private String name;
    private District houseDistrict;
    private District senateDistrict;
    private String phoneNumber;
    private String address;
    private String email;

    @JsonIgnore // Don't return the password hash when serializing
    private String passwordHash;

    private Optional<District> representingDistrict;

    public long getId() {
        return id;
    }

    @Override public String getName() {
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

    public Optional<District> getRepresentingDistrict() {
        return representingDistrict;
    }

    public Voter(Connection conn, ResultSet r) throws SQLException {
        this.id = r.getLong("id");
        this.name = r.getString("name");
        this.passwordHash = r.getString("password_hash");
        long houseDistrictID = r.getLong("house_district_id");
        long senateDistrictID = r.getLong("senate_district_id");
        this.phoneNumber = r.getString("phone_number");
        this.address = r.getString("address");
        this.email = r.getString("email");

        // Throw a SQLException if we've stored a reference to a district that doesn't exist.
        // This Shouldn't Happen™

        this.houseDistrict = District.getByIdOrThrow(conn, houseDistrictID);
        this.senateDistrict = District.getByIdOrThrow(conn, senateDistrictID);

        // Have to get this as an Object and cast.

        Long representingDistrictID = (Long)r.getObject("representing_district_id");
        if (representingDistrictID != null) {
            this.representingDistrict = District.getById(conn, representingDistrictID);
        }
    }

    public static void createTable(Connection connection) throws SQLException {
        SQLUtils.prepareStatementFromFile(connection, "sql/create_voter_table.sql").execute();
    }

    public static Voter create(Connection conn, String name, String password, District houseDistrict,
                               District senateDistrict, String phoneNumber, String address, String email,
                               Optional<District> representedDistrict) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/insert_voter.sql");
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
            Optional<Voter> voter = Voter.getById(conn, keys.getLong(1));
            if (voter.isPresent()) {
                return voter.get();
            } else {
                throw new SQLException("Voter insert ID was invalid");
            }
        }
        throw new SQLException("Voter insert did not return an ID");
    }

    public static Optional<Voter> getByEmail(Connection conn, String email) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/get_voter_by_email.sql");
        stmt.setString(1, email);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return Optional.of(new Voter(conn, r));
        }
        return Optional.empty();
    }

    public static Optional<Voter> getById(Connection conn, long id) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/get_voter_by_id.sql");
        stmt.setLong(1, id);
        ResultSet r = stmt.executeQuery();
        if (r.next()) {
            return Optional.of(new Voter(conn, r));
        }
        return Optional.empty();
    }

    /**
     * Gets a list of all voters.
     * @param conn The connection to the database.
     * @return A list of all voters in the database.
     * @throws SQLException
     */
    public static List<Voter> all(Connection conn) throws SQLException {
        PreparedStatement stmt = SQLUtils.prepareStatementFromFile(conn, "sql/all_voters.sql");
        ResultSet results = stmt.executeQuery();
        ArrayList<Voter> voters = new ArrayList<>();
        while (results.next()) {
            voters.add(new Voter(conn, results));
        }
        return voters;
    }
}
