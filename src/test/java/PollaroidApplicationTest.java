import com.pollaroid.PollaroidApplication;
import com.pollaroid.database.*;
import com.pollaroid.models.*;

import java.sql.SQLException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import junit.framework.TestCase;
import org.junit.Test;
import unitedstates.US;

import java.util.ArrayList;
import java.util.List;

public class PollaroidApplicationTest extends TestCase {

    private PollaroidApplication server;
    private District              sampleHouseDistrict;
    private District              sampleSenateDistrict;

    private DistrictDAO       districtDAO;
    private VoterDAO          voterDAO;
    private PollRecordDAO     pollRecordDAO;
    private PollOptionDAO     pollOptionDAO;
    private PollDAO           pollDAO;
    private AccessTokenDAO    tokenDAO;
    private MessageDAO        messageDAO;


    /**
     * Sets up the database in an initial configuration.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Create instance of Pollaroid Application
        server = new PollaroidApplication();

        // Attempt to make a connection to database
        try {
            server.createConnectionFromConfig();
            assertNotNull(server.getConnection());

            districtDAO   = new DistrictDAO(server.getConnection());
            voterDAO      = new VoterDAO(server.getConnection(),districtDAO);
            districtDAO.setVoterDAO(voterDAO);
            pollOptionDAO = new PollOptionDAO(server.getConnection());
            pollDAO       = new PollDAO(server.getConnection(),pollOptionDAO,voterDAO,districtDAO);
            pollRecordDAO = new PollRecordDAO(server.getConnection(),pollDAO,pollOptionDAO,voterDAO);
            tokenDAO      = new AccessTokenDAO(server.getConnection(),voterDAO);
            messageDAO    = new MessageDAO(server.getConnection(),voterDAO);

            assertEquals(districtDAO.getTableName(),"district");
            assertEquals(voterDAO.getTableName(),"voter");
            assertEquals(pollDAO.getTableName(),"poll");
            assertEquals(pollOptionDAO.getTableName(),"poll_option");
            assertEquals(pollRecordDAO.getTableName(),"poll_record");
            assertEquals(messageDAO.getTableName(),"message");

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
            messageDAO.createTable();


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
        List<Voter> voters = voterDAO.allInDistrict(sampleHouseDistrict.getId());
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

        pollRecordDAO.create(pollDAO.getById((long) 1).get(),
                pollOptionDAO.getById((long) 1).get(),
                voterDAO.getById((long) 2).get(), false);

        PollRecord sampleRecord = pollRecordDAO.getVoterResponse(voterDAO.getById((long) 2).get(),
                pollDAO.getById((long) 1).get()).get();

        List<PollRecord> records = pollRecordDAO.getResponses(pollDAO.getById((long) 1).get());

        // We shouldn't have a choice recorded for the first record, it's anonymous.
        assertFalse(records.get(0).getChoice().isPresent());

        // We shouldn't have a choice recorded for the second record, it's anonymous.
        assertFalse(sampleRecord.getChoice().isPresent());
        assertEquals((long)sampleRecord.getId(),(long)1);
        assertEquals(sampleRecord.getVoter().getName(), "Harlan Haskins");
        assertEquals(sampleRecord.getPoll().getTitle(), "Cats or Dogs?");

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
            ZonedDateTime time = ZonedDateTime.now(ZoneId.of("UTC")).minus(Duration.ofDays(2));
            assertFalse(value.isExpired());
            try {
                tokenDAO.setExpiration(value, time);
            } catch (SQLException e) {
                fail("Could not set expiration: " + e.getMessage());
            }
        });

        Optional<AccessToken> tokenLukeUpdated = tokenDAO.getByVoterID((long)2);

        tokenLukeUpdated.ifPresent((value) -> {
            assertTrue(value.isExpired());
            try {
                tokenDAO.extendLifetime(value);
            } catch (SQLException e) {
                fail("Could not extend token lifetime: " + e.getMessage());
            }
            assertFalse(value.isExpired());
        });

        tokenDAO.delete(tokenLuke.get());
    }

    @Test
    public void testDistricts() throws SQLException {
        District d = districtDAO.create(4, US.NEW_YORK,
                                        CongressionalBody.HOUSE);
        Voter v = voterDAO.create("Harlan Haskins",
                "pass4321",
                districtDAO.getById((long) 1).get(),
                districtDAO.getById((long) 2).get(),
                "5857654321",
                "1 Lomb Memorial Drive",
                "test321@gmail.com",
                Optional.of(d));

        Optional<Voter> rep = districtDAO.getRepresentative(d.getId());
        if (rep.isPresent()) {
            assertEquals(rep.get().getName(), "Harlan Haskins");
            assertEquals(rep.get().getEmail(), "test321@gmail.com");
        } else {
            fail("representative not returned for getRepresentative");
        }

        List<District> districts = districtDAO.all();
        assertEquals(districts.size(), 3);
    }

    @Test
    public void testMessages() throws SQLException {

        voterDAO.create("Luke Shadler",
                "pass1234",
                districtDAO.getById((long) 1).get(),
                districtDAO.getById((long) 2).get(),
                "5851234567",
                "1 Lomb Memorial Drive",
                "rep@gmail.com",
                Optional.of(districtDAO.getById((long) 2).get()));

        voterDAO.create("Harlan Haskins",
                "pass4321",
                districtDAO.getById((long) 1).get(),
                districtDAO.getById((long) 2).get(),
                "5857654321",
                "1 Lomb Memorial Drive",
                "test321@gmail.com",
                Optional.empty());

        voterDAO.create("Stuart Olivera",
                "pass1234",
                districtDAO.getById((long) 1).get(),
                districtDAO.getById((long) 2).get(),
                "5851234567",
                "1 Lomb Memorial Drive",
                "test456@gmail.com",
                Optional.empty());

        voterDAO.create("Joshua Robbins",
                "pass4321",
                districtDAO.getById((long) 1).get(),
                districtDAO.getById((long) 2).get(),
                "5857654321",
                "1 Lomb Memorial Drive",
                "test654@gmail.com",
                Optional.empty());

        messageDAO.create(voterDAO.getByEmail("test321@gmail.com").get(),
                          voterDAO.getByEmail("rep@gmail.com").get(),
                            "Hello World!");

        messageDAO.create(voterDAO.getByEmail("rep@gmail.com").get(),
                voterDAO.getByEmail("test321@gmail.com").get(),
                "New phone who dis?");

        messageDAO.create(voterDAO.getByEmail("test321@gmail.com").get(),
                voterDAO.getByEmail("rep@gmail.com").get(),
                "Oops, sorry!");

        messageDAO.create(voterDAO.getByEmail("rep@gmail.com").get(),
                voterDAO.getByEmail("test321@gmail.com").get(),
                "I'm not world, I am Luke!");

        Message firstMessage = messageDAO.getById((long)1).get();
        List<Message> messagesRep = messageDAO.getRepMessagesById(
                                            voterDAO.getByEmail("rep@gmail.com").get().getId());
        List<Message> messagesSentRep = messageDAO.getSentMessagesById(
                                            voterDAO.getByEmail("rep@gmail.com").get().getId());
        assertEquals(messagesRep.get(0).getText(),firstMessage.getText());
        assertEquals(messagesSentRep.get(0).getText(),"New phone who dis?");

        List<Message> allMessages = messageDAO.all();
        assertEquals(allMessages.size(),4);
        assertEquals(messagesRep.get(0).getId(),1);
        assertEquals(messagesRep.get(0).getFromId(),2);
        assertEquals(messagesRep.get(0).getToId(),1);
        assertTrue(messagesRep.get(0).getTimeSent().isBefore(messagesRep.get(1).getTimeSent()));


    }

    @Test
    public void testTopPolls() throws SQLException {
        districtDAO.create(1, US.NEW_YORK, CongressionalBody.HOUSE);
        districtDAO.create(2, US.NEW_YORK, CongressionalBody.SENATE);

        Voter luke = voterDAO.create("Luke Shadler",
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

        List<String> options2 = new ArrayList<>();
        options2.add("Blue");
        options2.add("Red");

        pollDAO.create(voterDAO.getById((long) 1).get(), districtDAO.getById((long) 1).get(),
                "Cats or Dogs?", options);
        pollDAO.create(voterDAO.getById((long) 1).get(), districtDAO.getById((long) 1).get(),
                "Blue or Red?", options2);

        pollRecordDAO.create(pollDAO.getById((long) 1).get(), pollOptionDAO.getById((long) 1).get(),
                voterDAO.getById((long) 1).get(), false);
        pollRecordDAO.create(pollDAO.getById((long) 1).get(), pollOptionDAO.getById((long) 2).get(),
                voterDAO.getById((long) 2).get(), false);
        pollRecordDAO.create(pollDAO.getById((long) 2).get(), pollOptionDAO.getById((long) 3).get(),
                voterDAO.getById((long) 1).get(), false);

        List<PollDAO.TopPollListing> topPoll = pollDAO.getTopPolls(1, luke);
        assertEquals(topPoll.size(), 1);
        assertEquals(topPoll.get(0).poll.getTitle(),"Cats or Dogs?");
    }
}
