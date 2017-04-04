package com.bipoller.resources;
import com.bipoller.District;
import com.bipoller.Voter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jersey.params.LongParam;

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
public class SignUpResource {
    private Connection connection;

    public SignUpResource(Connection connection) {
        this.connection = connection;
    }

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
            District house = District.getByIdOrThrow(connection, voter.houseDistrictID);
            District senate = District.getByIdOrThrow(connection, voter.senateDistrictID);
            Optional<District> represented = Optional.empty();
            if (voter.representingDistrictID != null) {
                represented = District.getById(connection, voter.representingDistrictID);
            }
            return Voter.create(connection, voter.name, voter.password,
                                house, senate, voter.phoneNumber,
                                voter.address, voter.email, represented);
        } catch (SQLException e) {
            Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build();
            throw new WebApplicationException(response);
        }
    }
}
