package com.bipoller.resources;
import com.bipoller.database.DistrictDAO;
import com.bipoller.database.VoterDAO;
import com.bipoller.models.District;
import com.bipoller.models.Voter;
import lombok.AllArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Path("/signup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class SignUpResource {
    private VoterDAO voterDAO;
    private DistrictDAO districtDAO;

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
    public Voter signUp(@Valid APIVoter voter) {
        try {
            return voterDAO.create(voter.name, voter.password,
                                   voter.houseDistrictID, voter.senateDistrictID, voter.phoneNumber,
                                   voter.address, voter.email,
                                   Optional.ofNullable(voter.representingDistrictID));
        } catch (SQLException e) {
            Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build();
            throw new WebApplicationException(response);
        }
    }
}
