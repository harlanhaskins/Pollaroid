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
     *
     * Will Eventually open a database connection.
     */
    public void setUp() throws Exception {

        // Create instance of BiPoller Application
        server = new BiPollerApplication();

        // Create configuration class
        config = new BiPollerConfiguration();



        // Attempt to make a connection to database
        try {
            server.createConnection("/home/lshadler/bipoller","lshadler","1234");
            Assert.assertNotNull(server.getConnection());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
//
//        // Run Database
//        try {
//            server.run(config, env);
//        }
//        catch(Exception e) {
//            e.printStackTrace(); // I should handle this better
//        }



        // Create Sample Tuples
        sampleHouseDistrict = new District( (long)1     ,
                                            1           ,
                                            US.NEW_YORK ,
                                            CongressionalBody.HOUSE);

        sampleSenateDistrict = new District(    (long)2                  ,
                                                2                        ,
                                                US.NEW_YORK              ,
                                                CongressionalBody.SENATE );

        sampleVoter = new Voter(    0001                    ,
                                    "Luke Shadler"          ,
                                    "pass1234"              ,
                                    "5851234567"            ,
                                    "1 Lomb Memorial Drive" ,
                                    "email-me@rit.edu"      ,
                                    sampleHouseDistrict     ,
                                    sampleSenateDistrict    ,
                                    Optional.empty()        );

        sampleRepresentative = new Voter(   0001                                ,
                                            "Harlan Haskins"                    ,
                                            "pass4321"                          ,
                                            "5857654321"                        ,
                                            "1 Lomb Memorial Drive"             ,
                                            "email-me@rit.edu"                  ,
                                            sampleHouseDistrict                 ,
                                            sampleSenateDistrict                ,
                                            Optional.of(sampleSenateDistrict)   );

        sampleOption1 = new PollOption( (long) 1,
                                        (long) 1,
                                        "Cats"  );
        sampleOption2 = new PollOption( (long) 2,
                                        (long) 1,
                                        "Dogs"  );
        List<PollOption> opts = new ArrayList<>();
        opts.add(sampleOption1);
        opts.add(sampleOption2);

        samplePoll = new Poll(  (long) 1                ,
                                sampleRepresentative    ,
                                sampleSenateDistrict    ,
                                "Cats or Dogs?"         ,
                                opts                    );

        assertNotNull(sampleVoter);
        assertNotNull(sampleRepresentative);
        assertNotNull(sampleHouseDistrict);
        assertNotNull(sampleSenateDistrict);
        assertNotNull(samplePoll);

        SQLUtils.dropEverything(server.getConnection());
        districtDAO   = new DistrictDAO(server.getConnection());
        voterDAO      = new VoterDAO(server.getConnection(),districtDAO);
        pollOptionDAO = new PollOptionDAO(server.getConnection());
        pollDAO       = new PollDAO(server.getConnection(),pollOptionDAO,voterDAO,districtDAO);
        pollRecordDAO = new PollRecordDAO(server.getConnection(),pollDAO,pollOptionDAO,voterDAO);
        tokenDAO      = new AccessTokenDAO(server.getConnection(),voterDAO);









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

            SQLUtils.dropEverything(server.getConnection());                  
            districtDAO.createTable();                                        
            voterDAO.createTable();                                           
            pollDAO.createTable();                                            
            pollOptionDAO.createTable();                                      
            pollRecordDAO.createTable();                                      
            tokenDAO.createTable();                                           



            voterDAO.create("Luke Shadler",
                    "pass1234",
                    sampleHouseDistrict,
                    sampleSenateDistrict,
                    "5851234567",
                    "1 Lomb Memorial Drive",
                    "test123@gmail.com",
                    Optional.of(sampleSenateDistrict));

            voterDAO.create("Harlan Haskins",
                            "pass4321",
                            sampleHouseDistrict,
                            sampleSenateDistrict,
                            "5857654321",
                            "1 Lomb Memorial Drive",
                            "test321@gmail.com",
                            Optional.empty());

            List<Voter> voters = voterDAO.all();
            for(Voter v : voters) {
                System.out.printf("%s: %s\n",v.getName(),v.getEmail());
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }



}
