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

    private BiPollerApplication   server;
    private District              sampleHouseDistrict;
    private District              sampleSenateDistrict;

    private DistrictDAO       districtDAO;
    private VoterDAO          voterDAO;
    private PollRecordDAO     pollRecordDAO;
    private PollOptionDAO     pollOptionDAO;
    private PollDAO           pollDAO;
    private AccessTokenDAO    tokenDAO;


    /**
     * Sets up the database in an initial configuration.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Create instance of BiPoller Application
        server = new BiPollerApplication();

        // Attempt to make a connection to database
        try {
            server.createConnectionFromConfig();
            assertNotNull(server.getConnection());

            districtDAO   = new DistrictDAO(server.getConnection());
            voterDAO      = new VoterDAO(server.getConnection(),districtDAO);
            pollOptionDAO = new PollOptionDAO(server.getConnection());
            pollDAO       = new PollDAO(server.getConnection(),pollOptionDAO,voterDAO,districtDAO);
            pollRecordDAO = new PollRecordDAO(server.getConnection(),pollDAO,pollOptionDAO,voterDAO);
            tokenDAO      = new AccessTokenDAO(server.getConnection(),voterDAO);

            SQLUtils.dropEverything(server.getConnection());

            districtDAO.createTable();
            voterDAO.createTable();
            pollDAO.createTable();
            pollOptionDAO.createTable();
            pollRecordDAO.createTable();
            tokenDAO.createTable();


            sampleHouseDistrict = districtDAO.create(1, US.NEW_YORK,
                    CongressionalBody.HOUSE);
            sampleSenateDistrict = districtDAO.create(2, US.NEW_YORK,
                    CongressionalBody.SENATE);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Could not connect to database: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     *  Attempts to add new tuples to tables
     */
    public void testInsertAndAccessVoters() {
        try {
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

            Optional<Voter> harlan = voterDAO.getByEmail("test321@gmail.com");
            List<Voter> voters = voterDAO.all();
            assertEquals("Luke Shadler", voters.get(0).getName());
            assertEquals("Harlan Haskins", voters.get(1).getName());
            assertTrue(harlan.isPresent());
            harlan.ifPresent((value) -> assertEquals("Harlan Haskins", value.getName()));
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testCreatePoll() {
        try {
            districtDAO.create(1, US.NEW_YORK, CongressionalBody.HOUSE);
            districtDAO.create(2, US.NEW_YORK, CongressionalBody.SENATE);

            voterDAO.create("Luke Shadler",
                    "pass1234",
                    districtDAO.getById((long) 1).get(),
                    districtDAO.getById((long) 2).get(),
                    "5851234567",
                    "1 Lomb Memorial Drive",
                    "test123@gmail.com",
                    Optional.of(districtDAO.getById((long) 2).get()));


            List<String> options = new ArrayList<>();
            options.add("Cats");
            options.add("Dogs");

            pollDAO.create(voterDAO.getById((long) 1).get(), districtDAO.getById((long)1).get(),
                    "Cats or dogs?", options);

            List<Poll> polls = pollDAO.getPollsInDistricts(districtDAO.getById((long)1).get(),
                    districtDAO.getById((long)2).get());
            assertEquals(polls.size(),1);

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreatePollRecord() {
        try {
            districtDAO.create(1, US.NEW_YORK, CongressionalBody.HOUSE);
            districtDAO.create(2, US.NEW_YORK, CongressionalBody.SENATE);

            voterDAO.create("Luke Shadler",
                    "pass1234",
                    districtDAO.getById((long) 1).get(),
                    districtDAO.getById((long) 2).get(),
                    "5851234567",
                    "1 Lomb Memorial Drive",
                    "test123@gmail.com",
                    Optional.of(districtDAO.getById((long) 2).get()));

            voterDAO.create("Harlan Haskins",
                    "pass4321",
                    districtDAO.getById((long) 1).get(),
                    districtDAO.getById((long) 2).get(),
                    "5857654321",
                    "1 Lomb Memorial Drive",
                    "test321@gmail.com",
                    Optional.empty());

            List<String> options = new ArrayList<>();
            options.add("Cats");
            options.add("Dogs");

            pollDAO.create(voterDAO.getById((long) 1).get(), districtDAO.getById((long) 1).get(),
                    "Cats or dogs?", options);

            pollRecordDAO.create(pollDAO.getById((long) 1).get(), pollOptionDAO.getById((long) 1).get(),
                    voterDAO.getById((long) 2).get());

            PollRecord sampleRecord = pollRecordDAO.getVoterResponse(voterDAO.getById((long) 2).get(),
                    pollDAO.getById((long) 1).get()).get();

            assertEquals(sampleRecord.getChoice().getText(), "Cats");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
