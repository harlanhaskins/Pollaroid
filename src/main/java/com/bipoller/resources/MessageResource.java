package com.bipoller.resources;

import com.bipoller.auth.AuthRoles;
import com.bipoller.database.*;
import com.bipoller.models.*;
import lombok.AllArgsConstructor;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.List;

/**
 * A resource for creating and responding to polls
 */
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class MessageResource {
    private MessageDAO messageDAO;
    private VoterDAO voterDAO;

    public static class APIMessage {
        @NotNull
        public String fromEmail;

        @NotNull
        public String toEmail;

        @NotNull
        public String message;
    }



    @GET
    @RolesAllowed(AuthRoles.VOTER)
    public List<Message> all(@Context SecurityContext context) {
        try {
            return messageDAO.all();
        } catch (SQLException e) {
            throw new BiPollerError(e.getMessage());
        }
    }

    @POST
    @RolesAllowed(AuthRoles.REPRESENTATIVE)
    public Message create(@Context SecurityContext context, APIMessage apiMessage) {
        try {
            return messageDAO.create(voterDAO.getByEmail(apiMessage.fromEmail).get(),
                                            voterDAO.getByEmail(apiMessage.toEmail).get(),apiMessage.message);
        } catch (SQLException e) {
            throw new BiPollerError(e.getMessage());
        }
    }



    @GET
    @Path("/{id}/inbox")
    @RolesAllowed(AuthRoles.REPRESENTATIVE)
    public List<Message> userInbox(@Context SecurityContext context) {
        try {
            Voter voter = (Voter)context.getUserPrincipal();
            return messageDAO.getRepMessagesById(voter.getId());
        } catch (SQLException e) {
            throw new BiPollerError(e.getMessage());
        }
    }
}
