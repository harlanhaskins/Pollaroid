package com.pollaroid.resources;

import com.pollaroid.auth.AuthRoles;
import com.pollaroid.database.*;
import com.pollaroid.models.*;
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
            throw new PollaroidError(e.getMessage());
        }
    }

    @POST
    @RolesAllowed(AuthRoles.REPRESENTATIVE)
    public Poll create(@Context SecurityContext context, APIPoll apiPoll) {
        try {
            if (apiPoll.options.size() < 2) {
                throw new PollaroidError("A poll must be created with at least two options.",
                                                                        Response.Status.PRECONDITION_FAILED);
            }
            Voter voter = (Voter)context.getUserPrincipal();
            if (voter.isRepresentative()) {
                return pollDAO.create(voter, voter.getRepresentingDistrict().get(),
                        apiPoll.title, apiPoll.options);
            } else {
                throw new PollaroidError("This user does not represent any districts.",
                                        Response.Status.UNAUTHORIZED);
            }
        } catch (SQLException e) {
            throw new PollaroidError(e.getMessage());
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
                throw new PollaroidError("You cannot vote on this poll; you are not in this district.",
                        Response.Status.UNAUTHORIZED);
            }

            // Delete the existing response, if any.
            Optional<PollRecord> existing = pollRecordDAO.getVoterResponse(voter, poll);
            boolean hasVotedOnPoll = false;
            if (existing.isPresent()) {
                pollRecordDAO.delete(existing.get());
                hasVotedOnPoll = true;
            }

            PollOption option = pollOptionDAO.getByIdOrThrow(record.optionID);
            pollRecordDAO.create(poll, option, voter, hasVotedOnPoll);
        } catch (SQLException e) {
            throw new PollaroidError(e.getMessage());
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
                throw new PollaroidError("You cannot get responses for a poll you did not create.",
                                        Response.Status.UNAUTHORIZED);
            }
            return pollRecordDAO.getResponses(poll);
        } catch (SQLException e) {
            throw new PollaroidError(e.getMessage());
        }
    }


    @GET
    @Path("/{id}/results")
    @RolesAllowed(AuthRoles.VOTER)
    public List<PollRecord> allResponses(@PathParam("id") long pollID,@Context SecurityContext context) {
        try {
            Poll poll = pollDAO.getByIdOrThrow(pollID);
            return pollRecordDAO.getResponses(poll);
        } catch (SQLException e) {
            throw new PollaroidError(e.getMessage());
        }
    }

    @GET
    @Path("/top")
    @RolesAllowed(AuthRoles.VOTER)
    public List<Poll> getTopPolls(@DefaultValue("3") @QueryParam("count") int numberOfPolls,
                                  @Context SecurityContext context) {
        try {
        	Voter voter = (Voter)context.getUserPrincipal();
            return pollDAO.getTopPolls(numberOfPolls, voter);
        } catch (SQLException e){
            throw new PollaroidError(e.getMessage());
        }
    }

}
