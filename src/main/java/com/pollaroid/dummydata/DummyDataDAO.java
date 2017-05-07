package com.pollaroid.dummydata;

import java.sql.SQLException;
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
        generateRandomVoters();
    }

    void generateDistricts() throws SQLException {
        for (US state : US.values()) {
            System.out.println("Creating districts for " + state.getANSIAbbreviation());
            for (int i = 0; i < 3; i++) {
                District house = districtDAO.create(i, state, CongressionalBody.HOUSE);
                District senate = districtDAO.create(i, state, CongressionalBody.SENATE);
                generateRepresentatives(house, senate);
            }
        }
    }
    
    void generateRandomVoters() throws SQLException {
        for (US state : US.values()) {
            System.out.println("Creating voters for " + state.getANSIAbbreviation());
            for (int i = 0; i < random.nextInt(20); i++) {
                District house = districtDAO.getByFieldsOrThrow(random.nextInt(3), state, CongressionalBody.HOUSE);
                District senate = districtDAO.getByFieldsOrThrow(random.nextInt(3), state, CongressionalBody.SENATE);
                Person person = fairy.person();
                voterDAO.create(person.fullName(), person.password(), house, senate,
                        person.telephoneNumber(), person.getAddress().toString(), person.companyEmail(),
                        Optional.empty());
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
}
