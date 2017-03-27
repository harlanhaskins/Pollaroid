package com.bipoller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by lshadler on 3/27/17.
 *
 * A set of query tools for
 */
public class QueryTools {

    /**
     *  queryVoterTable
     *
     *  Given arguments to an SQL query, execute this query on a given table
     *
     *  @Param conn:            Connection to a database
     *  @Param tableName:       Name of the table to query from
     *  @Param columns:         Projected columns of table desired
     *  @Param whereClauses:    Set of conditions for query
     *
     *  @Returns ResultSet of the generated query.
     */
    public static ResultSet queryVoterTable(Connection conn, String tableName, ArrayList<String> columns, ArrayList<String> whereClauses) {
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

        sb.append("FROM " + tableName + " ");
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
}
