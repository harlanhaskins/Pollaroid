import com.bipoller.BiPollerApplication;
import com.bipoller.BiPollerConfiguration;
import com.bipoller.database.*;
import com.bipoller.models.*;

import java.sql.SQLException;
import java.util.Optional;
import io.dropwizard.setup.Environment;
import junit.framework.TestCase;
import org.h2.jdbc.JdbcSQLException;
import org.junit.Assert;
import org.junit.Test;
import unitedstates.US;

import java.util.ArrayList;
import java.util.List;

public class BiPollerApplicationTest extends TestCase {

    protected BiPollerApplication   server;
    protected BiPollerConfiguration config;
    protected Environment           env;

    protected Poll       samplePoll;
    protected PollOption sampleOption1,sampleOption2;

    protected Voter         sampleVoter;
    protected Voter         sampleRepresentative;
    protected District      sampleHouseDistrict;
    protected District      sampleSenateDistrict;
    protected PollRecord    samplePollRecord;

    protected DistrictDAO       districtDAO;
    protected VoterDAO          voterDAO;
    protected PollRecordDAO     pollRecordDAO;
    protected PollOptionDAO     pollOptionDAO;
    protected PollDAO           pollDAO;
    protected AccessTokenDAO    tokenDAO;

    @Override
    /**
     * Creates sample data for Testing
     */
    public void setUp() throws Exception {

        // Create instance of BiPoller Application
        server = new BiPollerApplication();

        // Create configuration class
        config = new BiPollerConfiguration();



        // Attempt to make a connection to database
        try {
            server.createConnectionFromConfig();
            Assert.assertNotNull(server.getConnection());
        }
        catch(Exception e) {
            e.printStackTrace();
        }


        // Create Sample Tuples
        sampleOption1 = new PollOption((long)1,(long)1,"Cats");
        sampleOption2 = new PollOption((long)2,(long)1,"Dogs");

        List<PollOption> opts = new ArrayList<>();
        opts.add(sampleOption1);
        opts.add(sampleOption2);

        samplePoll = new Poll((long)1,sampleRepresentative,
                              sampleSenateDistrict,"Cats or Dogs?",opts);


        SQLUtils.dropEverything(server.getConnection());
        districtDAO   = new DistrictDAO(server.getConnection());
        voterDAO      = new VoterDAO(server.getConnection(),districtDAO);
        pollOptionDAO = new PollOptionDAO(server.getConnection());
        pollDAO       = new PollDAO(server.getConnection(),pollOptionDAO,voterDAO,districtDAO);
        pollRecordDAO = new PollRecordDAO(server.getConnection(),pollDAO,pollOptionDAO,voterDAO);
        tokenDAO      = new AccessTokenDAO(server.getConnection(),voterDAO);

        districtDAO.createTable();
        voterDAO.createTable();
        pollDAO.createTable();
        pollOptionDAO.createTable();
        pollRecordDAO.createTable();
        tokenDAO.createTable();


    }

    @Override
    /**
     * Configured to close the connection with the BiPoller Server
     */
    public void tearDown() {

        // Attempt to close connection
        server.closeConnection();
    }

    @Test
    /**
     *  Attempts to add new tuples to tables
     */
    public void testInsertAndAccessVoters() {
        try {

            districtDAO.create(1,US.NEW_YORK,CongressionalBody.HOUSE);
            districtDAO.create(2,US.NEW_YORK,CongressionalBody.SENATE);

            voterDAO.create("Luke Shadler",
                    "pass1234",
                    districtDAO.getById((long)1).get(),
                    districtDAO.getById((long)2).get(),
                    "5851234567",
                    "1 Lomb Memorial Drive",
                    "test123@gmail.com",
                    Optional.of(districtDAO.getById((long)2).get()));

            voterDAO.create("Harlan Haskins",
                            "pass4321",
                            districtDAO.getById((long)1).get(),
                            districtDAO.getById((long)2).get(),
                            "5857654321",
                            "1 Lomb Memorial Drive",
                            "test321@gmail.com",
                            Optional.empty());

            List<Voter> voters = voterDAO.all();
            String namesSpaceSeparated = "";
            assertEquals("Luke Shadler",voters.get(0).getName());
            assertEquals("Harlan Haskins",voters.get(1).getName());
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
