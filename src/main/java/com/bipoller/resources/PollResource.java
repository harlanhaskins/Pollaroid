package com.bipoller.resources;

import com.bipoller.auth.AuthRoles;
import com.bipoller.database.*;
import com.bipoller.models.Poll;
import com.bipoller.models.Voter;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A resource for creating and responding to polls
 */
@Path("/polls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class PollResource {
    private PollDAO pollDAO;

    public static class APIPoll {
        @NotNull
        public String title;

        @NotEmpty
        public List<String> options;
    }

    @GET
    @RolesAllowed(AuthRoles.VOTER)
    public List<Poll> all(@Context SecurityContext context) {
        try {
            Voter voter = (Voter)context.getUserPrincipal();
            return pollDAO.getPollsInDistricts(voter.getHouseDistrict(), voter.getSenateDistrict());
        } catch (SQLException e) {
            Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build();
            throw new WebApplicationException(response);
        }
    }

    @POST
    @RolesAllowed(AuthRoles.REPRESENTATIVE)
    public Poll create(@Context SecurityContext context, APIPoll apiPoll) {
        try {
            Voter voter = (Voter)context.getUserPrincipal();
            return pollDAO.create(voter, voter.getRepresentingDistrict().get(),
                                  apiPoll.title, apiPoll.options);
        } catch (SQLException e) {
            Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build();
            throw new WebApplicationException(response);
        }
    }
}
