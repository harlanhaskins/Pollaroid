package com.bipoller.resources;
import com.bipoller.database.VoterDAO;
import com.bipoller.models.Voter;
import com.bipoller.auth.AuthRoles;
import lombok.AllArgsConstructor;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.List;

@Path("/voters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class VoterResource {
    private VoterDAO voterDAO;

    @GET
    @RolesAllowed(AuthRoles.VOTER)
    public List<Voter> voters() {
        try {
            return voterDAO.all();
        } catch (SQLException e) {
            Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build();
            throw new WebApplicationException(response);
        }
    }
}
