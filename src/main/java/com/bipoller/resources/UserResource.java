package com.bipoller.resources;
import com.bipoller.Voter;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private Connection connection;

    public UserResource(Connection connection) {
        this.connection = connection;
    }

    @GET
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
