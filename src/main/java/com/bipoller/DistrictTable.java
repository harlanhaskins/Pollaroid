package com.bipoller;

import com.bipoller.objects.District;
import com.bipoller.objects.Poll;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by lshadler on 3/27/17.
 */
public class DistrictTable {
    public DistrictTable() {
    }


    public static void createDistrictTable(Connection conn) {
        try {
            String e = "CREATE TABLE IF NOT EXISTS district(ID INT PRIMARY KEY,DISTRICTNO INT, STATE VARCHAR (255), BOOLEAN SENATE);";
            Statement stmt = conn.createStatement();
            stmt.execute(e);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public static void addDistrict(Connection conn, int id, int dNo, String state, boolean isSenate) {
        String query = String.format("INSERT INTO district VALUES(%d,\'%d,\'%s,\'%s);", new Object[]{Integer.valueOf(id), Integer.valueOf(dNo),state, Boolean.valueOf(isSenate)});

        try {
            Statement e = conn.createStatement();
            e.execute(query);
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

    }


    public static String createDistrictInsertSQL(ArrayList<District> districts) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO district (id, FIRST_NAME, LAST_NAME, MI) VALUES");

        for(int i = 0; i < districts.size(); ++i) {
            District p = (District)districts.get(i);
            sb.append(String.format("(%d,\'%d\',\'%s\',\'%s\')", new Object[]{Integer.valueOf(p.getId()), p.getDistrictNo(),p.getState(),p.isSenate()}));
            if(i != districts.size() - 1) {
                sb.append(",");
            } else {
                sb.append(";");
            }
        }

        return sb.toString();
    }

    public static ResultSet queryDistrictTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        int e;
        if(columns.isEmpty()) {
            sb.append("* ");
        } else {
            for(e = 0; e < columns.size(); ++e) {
                if(e != columns.size() - 1) {
                    sb.append((String)columns.get(e) + ", ");
                } else {
                    sb.append((String)columns.get(e) + " ");
                }
            }
        }

        sb.append("FROM district ");
        if(!whereClauses.isEmpty()) {
            sb.append("WHERE ");

            for(e = 0; e < whereClauses.size(); ++e) {
                if(e != whereClauses.size() - 1) {
                    sb.append((String)whereClauses.get(e) + " AND ");
                } else {
                    sb.append((String)whereClauses.get(e));
                }
            }
        }

        sb.append(";");
        System.out.println("Query: " + sb.toString());

        try {
            Statement var6 = conn.createStatement();
            return var6.executeQuery(sb.toString());
        } catch (SQLException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static void printDistrictTable(Connection conn) {
        String query = "SELECT * FROM district;";

        try {
            Statement e = conn.createStatement();
            ResultSet result = e.executeQuery(query);

            while(result.next()) {
                System.out.printf("District %d: %d %s %s \n", new Object[]{Integer.valueOf(result.getInt(1)),
                                                                           Integer.valueOf(result.getInt(2)),
                                                                                           result.getString(3),
                                                                                           result.getBoolean(4)});
            }
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }
}
