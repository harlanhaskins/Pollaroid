package com.pollaroid.dummydata;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.pollaroid.database.AccessTokenDAO;
import com.pollaroid.database.DistrictDAO;
import com.pollaroid.database.PollDAO;
import com.pollaroid.database.PollOptionDAO;
import com.pollaroid.database.PollRecordDAO;
import com.pollaroid.database.SQLUtils;
import com.pollaroid.database.VoterDAO;

public class DummyDataPopulator {
	public static void main(String[] args) {
        Properties dbProperties = new Properties();
        try {
            InputStream fileStream = new FileInputStream("./database.cfg");
            dbProperties.load(fileStream);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        try {
            String url = "jdbc:h2:" + dbProperties.getProperty("location");
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(url, dbProperties.getProperty("user"), dbProperties.getProperty("password"));
            
            SQLUtils.dropEverything(conn);
            
            DistrictDAO districtDAO = new DistrictDAO(conn);
            VoterDAO voterDAO = new VoterDAO(conn, districtDAO);
            districtDAO.setVoterDAO(voterDAO);

            PollOptionDAO pollOptionDAO = new PollOptionDAO(conn);
            PollDAO pollDAO = new PollDAO(conn, pollOptionDAO, voterDAO, districtDAO);
            pollOptionDAO.setPollDAO(pollDAO);
            PollRecordDAO pollRecordDAO = new PollRecordDAO(conn, pollDAO, pollOptionDAO, voterDAO);

            AccessTokenDAO tokenDAO = new AccessTokenDAO(conn, voterDAO);

            districtDAO.createTable();
            voterDAO.createTable();
            pollDAO.createTable();
            pollOptionDAO.createTable();
            pollRecordDAO.createTable();
            tokenDAO.createTable();

            DummyDataDAO dummyDataDAO = new DummyDataDAO(voterDAO, districtDAO, pollDAO, pollOptionDAO, pollRecordDAO);
            dummyDataDAO.generateData();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
	}
}
