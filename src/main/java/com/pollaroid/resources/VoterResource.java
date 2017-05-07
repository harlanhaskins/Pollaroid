package com.pollaroid.resources;
import com.pollaroid.database.VoterDAO;
import com.pollaroid.models.Voter;
import com.pollaroid.auth.AuthRoles;
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
            throw new PollaroidError(e.getMessage());
        }
    }
}
