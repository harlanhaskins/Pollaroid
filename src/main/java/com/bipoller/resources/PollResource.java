package com.bipoller.resources;

import com.bipoller.auth.AuthRoles;
import com.bipoller.database.*;
import com.bipoller.models.*;
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
import java.util.List;
import java.util.Optional;

/**
 * A resource for creating and responding to polls
 */
@Path("/polls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class PollResource {
    private PollDAO pollDAO;
    private PollOptionDAO pollOptionDAO;
    private PollRecordDAO pollRecordDAO;

    public static class APIPoll {
        @NotNull
        public String title;

        @NotEmpty
        public List<String> options;
    }

    public static class APIPollRecord {
        @NotNull
        public Long optionID;
    }

    @GET
    @RolesAllowed(AuthRoles.VOTER)
    public List<Poll> all(@Context SecurityContext context) {
        try {
            Voter voter = (Voter)context.getUserPrincipal();
            return pollDAO.getPollsInDistricts(voter.getHouseDistrict(), voter.getSenateDistrict());
        } catch (SQLException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
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
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/{id}/responses")
    @RolesAllowed(AuthRoles.VOTER)
    public void vote(@PathParam("id") long pollID, @Context SecurityContext context, APIPollRecord record) {
        try {
            Voter voter = (Voter)context.getUserPrincipal();
            Poll poll = pollDAO.getByIdOrThrow(pollID);

            if (!voter.isInDistrict(poll.getDistrict())) {
                throw new WebApplicationException("You cannot vote on this poll; you are not in this district.",
                                                  Response.Status.UNAUTHORIZED);
            }

            // Delete the existing response, if any.
            Optional<PollRecord> existing = pollRecordDAO.getVoterResponse(voter, poll);
            if (existing.isPresent()) {
                pollRecordDAO.delete(existing.get());
            }

            PollOption option = pollOptionDAO.getByIdOrThrow(record.optionID);
            pollRecordDAO.create(poll, option, voter);
        } catch (SQLException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("/{id}/responses")
    @RolesAllowed(AuthRoles.REPRESENTATIVE)
    public List<PollRecord> responses(@PathParam("id") long pollID, @Context SecurityContext context) {
        try {
            Voter voter = (Voter)context.getUserPrincipal();
            Poll poll = pollDAO.getByIdOrThrow(pollID);
            if (poll.getSubmitter().getId() != voter.getId()) {
                Response response =
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity("You cannot get responses for a poll you did not create.")
                                .build();
                throw new WebApplicationException(response);
            }
            return pollRecordDAO.getResponses(poll);
        } catch (SQLException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
