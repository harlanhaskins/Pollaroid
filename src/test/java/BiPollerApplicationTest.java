import com.bipoller.BiPollerApplication;
import com.bipoller.BiPollerConfiguration;
import com.bipoller.database.*;
import com.bipoller.models.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

            assertEquals(districtDAO.getTableName(),"district");
            assertEquals(voterDAO.getTableName(),"voter");
            assertEquals(pollDAO.getTableName(),"poll");
            assertEquals(pollOptionDAO.getTableName(),"poll_option");
            assertEquals(pollRecordDAO.getTableName(),"poll_record");

            assertEquals(tokenDAO.getTableName(),"token");
            assertEquals(tokenDAO.getSQLGetByIdPath(),"sql/get_token_by_uuid.sql");
            assertEquals(tokenDAO.getSQLInsertPath(),"sql/insert_token.sql");


            SQLUtils.dropEverything(server.getConnection());

            districtDAO.createTable();
            voterDAO.createTable();
            pollDAO.createTable();
            pollOptionDAO.createTable();
            pollOptionDAO.setPollDAO(pollDAO);
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
    public void testInsertAndAccessVoters() throws SQLException {
        voterDAO.create("Luke Shadler",
                        "pass1234",
                        sampleHouseDistrict,
                        sampleSenateDistrict,
                        "5851234567",
                        "1 Lomb Memorial Drive",
                        "test123@gmail.com",
                        Optional.empty());

        voterDAO.create("Harlan Haskins",
                        "pass4321",
                        sampleHouseDistrict,
                        sampleSenateDistrict,
                        "5857654321",
                        "1 Lomb Memorial Drive",
                        "test321@gmail.com",
                        Optional.of(sampleSenateDistrict));

        Optional<Voter> harlan = voterDAO.getByEmail("test321@gmail.com");
        List<Voter> voters = voterDAO.all();
        assertEquals("Luke Shadler", voters.get(0).getName());
        assertEquals("Harlan Haskins", voters.get(1).getName());
        assertTrue(harlan.isPresent());
        harlan.ifPresent((value) -> {
            assertEquals("Harlan Haskins", value.getName());
            assertEquals(2, value.getId());
            assertNotNull(value.getPasswordHash());
            assertEquals("test321@gmail.com", value.getEmail());
            assertEquals("1 Lomb Memorial Drive", value.getAddress());
            assertEquals("5857654321", value.getPhoneNumber());
            assertEquals(1, value.getHouseDistrict().getId());
            assertEquals(2, value.getSenateDistrict().getId());
            assertEquals(2, value.getRepresentingDistrict().get().getId());
            assertTrue(value.isInDistrict(sampleHouseDistrict));
        });
    }


    @Test
    public void testCreatePoll() throws SQLException {
        districtDAO.create(1, US.NEW_YORK, CongressionalBody.HOUSE);
        districtDAO.create(2, US.NEW_YORK, CongressionalBody.SENATE);

        Optional<District> districtOpt = districtDAO.getById((long)1);

        districtOpt.ifPresent((district) ->
        {
            assertEquals(district.getId(),1);
            assertEquals(district.getCongressionalBody(),CongressionalBody.SENATE);
            assertEquals(district.getCongressionalBody().toString(),"Senate");
            assertEquals(district.getState(),US.NEW_YORK);
            assertEquals((int)district.getNumber(),(int)1);
            assertTrue(district.isSenate());
            assertFalse(district.isHouse());
            assertEquals(district.getStateCode(),"NY");
            assertEquals(district.getStateName(),"New York");
        });

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

        Poll sample = polls.get(0);
        assertEquals(sample.getDistrict().getId(), (long)1);
        assertEquals(sample.getTitle(),"Cats or dogs?");
        assertEquals(sample.getOptions().size(),2);
        assertEquals(sample.getSubmitter().getName(),"Luke Shadler");
        ;
    }

    @Test
    public void testCreatePollRecord() throws SQLException {
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
                "Cats or Dogs?", options);

        pollRecordDAO.create(pollDAO.getById((long) 1).get(), pollOptionDAO.getById((long) 1).get(),
                voterDAO.getById((long) 2).get());

        PollRecord sampleRecord = pollRecordDAO.getVoterResponse(voterDAO.getById((long) 2).get(),
                pollDAO.getById((long) 1).get()).get();

        List<PollRecord> records = pollRecordDAO.getResponses(pollDAO.getById((long) 1).get());

        assertEquals(records.get(0).getChoice().getId(),sampleRecord.getId());
        assertEquals(sampleRecord.getChoice().getText(), "Cats");
        assertEquals((long)sampleRecord.getId(),(long)1);
        assertEquals(sampleRecord.getVoter().getName(), "Harlan Haskins");
        assertEquals(sampleRecord.getPoll().getTitle(), "Cats or Dogs?");

        PollOption cats = sampleRecord.getChoice();

        assertEquals(cats.getText(), "Cats");
        assertEquals((long)cats.getId(),(long)1);

        assertEquals((long)pollOptionDAO.getPoll(cats).getId(),(long)cats.getPollID());
        pollRecordDAO.delete(pollRecordDAO.getById((long)1).get());
        assertEquals(pollRecordDAO.getResponses(pollDAO.getById((long) 1).get()).size(),0);


    }

    @Test
    public void testLogonTokens() throws SQLException {
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

        tokenDAO.create(voterDAO.getByEmail("test321@gmail.com").get());
        Optional<AccessToken> tokenLuke = tokenDAO.getByVoterID((long)2);

        tokenLuke.ifPresent((value) -> {
            assertNotNull(value.getExpiration());
            assertNotNull(value.getUuid());
            assertEquals(value.getVoter().getName(),"Harlan Haskins");
            ZonedDateTime time = ZonedDateTime.now(ZoneId.of("UTC"));
            assertFalse(value.isExpired());
            value.setExpiration(time);
        });

        tokenLuke.ifPresent((value) -> {
            if(value.isExpired()) {
                try {
                    tokenDAO.extendLifetime(value);
                }
                catch(SQLException e){
                    e.printStackTrace();
                }

            }
        });

        tokenDAO.delete(tokenDAO.getByVoterID((long)2).get());
    }
}
