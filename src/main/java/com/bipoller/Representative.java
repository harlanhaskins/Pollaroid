package com.bipoller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Representative extends Voter {
    private District representedDistrict;

    public District getRepresentedDistrict() {
        return representedDistrict;
    }

    public Representative(Connection conn, ResultSet r) throws SQLException {
        super(conn, r);
        long representedDistrictID = r.getInt(9);
        this.representedDistrict = District.getById(conn, representedDistrictID);
    }
}
