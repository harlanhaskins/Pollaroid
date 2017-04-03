package com.bipoller.resources;
import com.bipoller.District;
import com.bipoller.Voter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jersey.params.LongParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Path("/signup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SignUpResource {
    private Connection connection;

    public SignUpResource(Connection connection) {
        this.connection = connection;
    }

    public static class APIVoter {
        public String name;
        public String password;
        public String email;
        public String phoneNumber;
        public String address;
        public Long houseDistrictID;
        public Long senateDistrictID;
        public Long representedDistrictID;
    }

    @POST
    public Voter signUp(APIVoter voter) {
        try {
            District house = District.getById(connection, voter.houseDistrictID);
            District senate = District.getById(connection, voter.senateDistrictID);
            District represented = null;
            if (voter.representedDistrictID != null) {
                represented = District.getById(connection, voter.representedDistrictID);
            }
            Voter v = Voter.create(connection, voter.name, voter.password,
                    house, senate, voter.phoneNumber,
                    voter.address, voter.email, Optional.ofNullable(represented));
            return v;
        } catch (SQLException e) {
            Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build();
            throw new WebApplicationException(response);
        }
    }
}
