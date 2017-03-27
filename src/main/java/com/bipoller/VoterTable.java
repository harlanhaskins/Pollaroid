package com.bipoller;

import com.bipoller.objects.District;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by lshadler on 3/27/17.
 */
public class VoterTable {
    public VoterTable() {
    }


    public static void createVoterTable(Connection conn) {
        try {
            String e = "CREATE TABLE IF NOT EXISTS voter(ID INT PRIMARY KEY, NAME VARCHAR(255), HOUSE_DISTRICT INT, SENATE_DISTRICT INT, PHONE VARCHAR(13), ADDRESS VARCHAR(255), EMAIL VARCHAR(255), PASS_HASH VARCHAR(255), PASH_SALT VARCHAR(255), DISTRICT_REPRESENTING INT";
            Statement stmt = conn.createStatement();
            stmt.execute(e);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public static void addVoter(Connection conn, int id, String name, int h_dist, int s_dist, String phone, String addr, String email, String passH, String passS, int repping){
        String query = String.format("INSERT INTO district VALUES(%d,\'%s,\'%d,\'%d,\'%s,\'%s,\'%s,\'%s,\'%s,\'%s,\'%d);", new Object[]{Integer.valueOf(id),name,h_dist,s_dist,phone,addr,email,passH,passS,repping});

        try {
            Statement e = conn.createStatement();
            e.execute(query);
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

    }


    public static ResultSet queryVoterTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses) {
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

        sb.append("FROM voter ");
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

    public static void printVoterTable(Connection conn) {
        String query = "SELECT * FROM district;";

        try {
            Statement e = conn.createStatement();
            ResultSet result = e.executeQuery(query);

            while(result.next()) {
                System.out.printf("Voter %d: %s %s \n", new Object[]{Integer.valueOf(result.getInt(1)),result.getString(2),result.getString(5)});
            }
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }
}
