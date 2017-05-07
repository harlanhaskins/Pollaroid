package com.pollaroid.resources;
import com.pollaroid.database.AccessTokenDAO;
import com.pollaroid.database.DistrictDAO;
import com.pollaroid.database.VoterDAO;
import com.pollaroid.models.AccessToken;
import com.pollaroid.models.District;
import com.pollaroid.models.Voter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.AllArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.Optional;

@Path("/signup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class SignUpResource {
    private VoterDAO voterDAO;
    private DistrictDAO districtDAO;
    private AccessTokenDAO accessTokenDAO;

    public static class APIVoter {
        @NotNull
        public String name;

        @NotNull
        public String password;

        @NotNull
        public String email;

        @NotNull
        public String phoneNumber;

        @NotNull
        public String address;

        @NotNull
        public Long houseDistrictID;

        @NotNull
        public Long senateDistrictID;

        public Long representingDistrictID;
    }

    @POST
    public AccessToken signUp(@Valid APIVoter voter) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number;

            try {
                number = phoneUtil.parse(voter.phoneNumber, "US");
            } catch (NumberParseException e) {
                throw new PollaroidError("You must enter a valid US phone number.");
            }

            District house = districtDAO.getByIdOrThrow(voter.houseDistrictID);
            District senate = districtDAO.getByIdOrThrow(voter.senateDistrictID);
            Optional<District> representing = districtDAO.getById(voter.representingDistrictID);

            String numberStr = phoneUtil.format(number,
                    PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            Voter v = voterDAO.create(voter.name, voter.password,
                                      house, senate, numberStr,
                                      voter.address, voter.email, representing);
            return accessTokenDAO.create(v);
        } catch (SQLException e) {
            throw new PollaroidError(e.getMessage());
        }
    }
}
