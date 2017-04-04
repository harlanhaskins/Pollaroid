package com.bipoller.resources;
import com.bipoller.Poll;
import com.bipoller.Voter;
import com.bipoller.auth.AuthRoles;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.List;

@Path("/voters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VoterResource {
    private Connection connection;

    public VoterResource(Connection connection) {
        this.connection = connection;
    }

    @GET
    @RolesAllowed(AuthRoles.VOTER)
    public List<Voter> voters() {
        try {
            return Voter.all(connection);
        } catch (SQLException e) {
            Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build();
            throw new WebApplicationException(response);
        }
    }
}
