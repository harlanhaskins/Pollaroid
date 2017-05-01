package com.bipoller.resources;
import com.bipoller.database.AccessTokenDAO;
import com.bipoller.database.DistrictDAO;
import com.bipoller.database.VoterDAO;
import com.bipoller.models.AccessToken;
import com.bipoller.models.District;
import com.bipoller.models.Voter;
import lombok.AllArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
            District house = districtDAO.getByIdOrThrow(voter.houseDistrictID);
            District senate = districtDAO.getByIdOrThrow(voter.senateDistrictID);
            Optional<District> representing = districtDAO.getById(voter.representingDistrictID);
            Voter v = voterDAO.create(voter.name, voter.password,
                                      house, senate, voter.phoneNumber,
                                      voter.address, voter.email, representing);
            return accessTokenDAO.create(v);
        } catch (SQLException e) {
            throw new BiPollerError(e.getMessage());
        }
    }
}
