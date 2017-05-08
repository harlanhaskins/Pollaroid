package com.pollaroid.dummydata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.jfairy.Fairy;
import org.jfairy.producer.person.Person;

import com.pollaroid.database.DistrictDAO;
import com.pollaroid.database.PollDAO;
import com.pollaroid.database.PollOptionDAO;
import com.pollaroid.database.PollRecordDAO;
import com.pollaroid.database.VoterDAO;
import com.pollaroid.models.CongressionalBody;
import com.pollaroid.models.District;
import com.pollaroid.models.Poll;
import com.pollaroid.models.Voter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import unitedstates.US;

/**
 * Generates dummy data for insertion into the database.
 */
@RequiredArgsConstructor
public class DummyDataDAO {
    @NonNull
    private VoterDAO voterDAO;

    @NonNull
    private DistrictDAO districtDAO;

    @NonNull
    private PollDAO pollDAO;

    @NonNull
    private PollOptionDAO pollOptionDAO;

    @NonNull
    private PollRecordDAO pollRecordDAO;

    private Fairy fairy = Fairy.create();
    private Random random = new Random();

    public void generateData() throws SQLException {
        generateDistricts();
    }

    void generateDistricts() throws SQLException {
        for (US state : US.values()) {
            System.out.println("Creating districts for " + state.getANSIAbbreviation());
            for (int i = 0; i < 3; i++) {
                District house = districtDAO.create(i, state, CongressionalBody.HOUSE);
                District senate = districtDAO.create(i, state, CongressionalBody.SENATE);
                
                generateRepresentatives(house, senate);
                generateRandomPolls(house); generateRandomPolls(senate);
            }
            
            generateRandomVoters(state);
        }
    }
    
    void generateRandomVoters(US state) throws SQLException {
        for (int i = 0; i < random.nextInt(20); i++) {
            District house = districtDAO.getByFieldsOrThrow(random.nextInt(3), state, CongressionalBody.HOUSE);
            District senate = districtDAO.getByFieldsOrThrow(random.nextInt(3), state, CongressionalBody.SENATE);
            Person person = fairy.person();
            Voter voter = voterDAO.create(person.fullName(), person.password(), house, senate,
                    person.telephoneNumber(), person.getAddress().toString(), person.companyEmail(),
                    Optional.empty());
            
            List<Poll> polls = pollDAO.getPollsInDistricts(house, senate);
            for (int ii = 0; ii < random.nextInt(4); ii++) {
            	if (!polls.isEmpty()) {
	            	Poll poll = polls.remove(random.nextInt(polls.size()));
	            	pollRecordDAO.create(poll, poll.getOptions().get(random.nextInt(poll.getOptions().size())), voter, false);
            	}
            }
        }
    }
    
    void generateRepresentatives(District house, District senate) throws SQLException {
    	Person person1 = fairy.person();
    	voterDAO.create(person1.fullName(), person1.password(), house, senate,
                person1.telephoneNumber(), person1.getAddress().toString(), person1.companyEmail(),
                Optional.of(house));
        
    	Person person2 = fairy.person();
    	voterDAO.create(person2.fullName(), person2.password(), house, senate,
        		person2.telephoneNumber(), person2.getAddress().toString(), person2.companyEmail(),
                Optional.of(senate));
    }
    
    void generateRandomPolls(District district) throws SQLException {
    	ArrayList<String> legalityTopics = new ArrayList<String>() {{
			add("weed");
			add("alcohol");
			add("smoking");
			add("domestic violence");
			add("dogs");
			add("guns");
			add("everything");
		}};
		
    	ArrayList<String> helpfulnessTopics = new ArrayList<String>() {{
			add("medical workers");
			add("the millitary");
			add("dogs");
			add("service jobs");
			add("capitalism");
			add("small buisnesses");
		}};
		
    	for (int i = 0; i < random.nextInt(6); i++) {
    		Poll poll = null;
    		
    		switch (random.nextInt(2)) {
    		case 0:
        		poll = pollDAO.create(districtDAO.getRepresentative(district.getId()).orElse(null), district, "Should " + legalityTopics.get(random.nextInt(legalityTopics.size())) + " be legal?", new ArrayList<String>() {{
        			add("Yes");
        			add("No");
        		}}); break;
    		case 1:
        		poll = pollDAO.create(districtDAO.getRepresentative(district.getId()).orElse(null), district, "What is your opinion on " + helpfulnessTopics.get(random.nextInt(helpfulnessTopics.size())) + " in our country?", new ArrayList<String>() {{
        			add("They are good for this country.");
        			add("They don't do anything for us.");
        			add("They actively harm our country.");
        		}}); break;
    		}
    	}
    }
}
