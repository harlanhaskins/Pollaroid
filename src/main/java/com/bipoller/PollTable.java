package com.bipoller;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.bipoller.objects.Poll;
public class PollTable {
    public PollTable() {
    }


    public static void createPollTable(Connection conn) {
        try {
            String e = "CREATE TABLE IF NOT EXISTS poll(ID INT PRIMARY KEY,TITLE VARCHAR(255));";
            Statement stmt = conn.createStatement();
            stmt.execute(e);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public static void addPoll(Connection conn, int id, String title) {
        String query = String.format("INSERT INTO poll VALUES(%d,\'%s\');", new Object[]{Integer.valueOf(id), title});

        try {
            Statement e = conn.createStatement();
            e.execute(query);
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

    }


    public static String createPollInsertSQL(ArrayList<Poll> polls) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO poll (id, FIRST_NAME, LAST_NAME, MI) VALUES");

        for(int i = 0; i < polls.size(); ++i) {
            Poll p = (Poll)polls.get(i);
            sb.append(String.format("(%d,\'%s\',\'%s\',\'%s\')", new Object[]{Integer.valueOf(p.getId()), p.getTitle()}));
            if(i != polls.size() - 1) {
                sb.append(",");
            } else {
                sb.append(";");
            }
        }

        return sb.toString();
    }

    public static ResultSet queryPollTable(Connection conn, ArrayList<String> columns, ArrayList<String> whereClauses) {
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

        sb.append("FROM poll ");
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

    public static void printPollTable(Connection conn) {
        String query = "SELECT * FROM poll;";

        try {
            Statement e = conn.createStatement();
            ResultSet result = e.executeQuery(query);

            while(result.next()) {
                System.out.printf("Poll %d: %s\n", new Object[]{Integer.valueOf(result.getInt(1)), result.getString(2)});
            }
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }
}
