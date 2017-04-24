package com.pollaroid.dummydata;

import com.pollaroid.database.*;
import com.pollaroid.models.CongressionalBody;
import com.pollaroid.models.District;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jfairy.producer.person.Person;
import unitedstates.US;
import org.jfairy.Fairy;

import java.sql.SQLException;
import java.util.Optional;

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

    public void generateData() throws SQLException {
        generateDistricts();
        generateRandomVoters();
    }

    void generateDistricts() throws SQLException {
        for (US state : US.values()) {
            System.out.println("Creating districts for " + state.getANSIAbbreviation());
            for (int i = 0; i < 3; i++) {
                districtDAO.create(i, state, CongressionalBody.HOUSE);
                districtDAO.create(i, state, CongressionalBody.SENATE);
            }
        }
    }

    void generateRandomVoters() throws SQLException {
        for (US state : US.values()) {
            System.out.println("Creating voters for " + state.getANSIAbbreviation());
            for (int districtNo = 0; districtNo < 3; districtNo++) {
                District house = districtDAO.getByFieldsOrThrow(districtNo, state, CongressionalBody.HOUSE);
                District senate = districtDAO.getByFieldsOrThrow(districtNo, state, CongressionalBody.SENATE);
                for (int i = 0; i < 5; i++) {
                    Person person = fairy.person();
                    try {
                        voterDAO.create(person.fullName(), person.password(), house, senate,
                                person.telephoneNumber(), person.getAddress().toString(), person.companyEmail(),
                                Optional.empty());
                    } catch (SQLException e) {
                        System.out.println("could not create entry for " + person.fullName() + "("
                                         + person.email() + "):");
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }
}
