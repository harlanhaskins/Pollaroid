package com.bipoller.resources;

import com.bipoller.database.AccessTokenDAO;
import com.bipoller.database.VoterDAO;
import com.bipoller.models.AccessToken;
import com.bipoller.auth.AuthRoles;
import com.bipoller.auth.BiPollerAuthenticator;
import com.bipoller.models.Voter;
import io.dropwizard.auth.Auth;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Optional;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class AuthResource {
    private BiPollerAuthenticator authenticator;
    private VoterDAO voterDAO;
    private AccessTokenDAO tokenDAO;

    /**
     * Tiny class to hold parameters
     */
    public static class APICredentials {
        @NotNull
        public String email;

        @NotNull
        public String password;
    }

    /**
     * Attempts to log in the user based on the provided credentials.
     *
     * @param credentials The login credentials for the user.
     * @return An AccessToken if the user was successfully authenticated.
     */
    @POST
    public AccessToken login(@Valid APICredentials credentials) {
        try {
            Optional<Voter> v = voterDAO.getByEmail(credentials.email);
            if (!v.isPresent()) {
                throw new WebApplicationException("User with email " + credentials.email + " not found",
                                                  Response.Status.UNAUTHORIZED);
            }
            Voter voter = v.get();
            boolean isCorrectPassword = BCrypt.checkpw(credentials.password, voter.getPasswordHash());
            if (!isCorrectPassword) {
                throw new WebApplicationException("Incorrect password.", Response.Status.UNAUTHORIZED);
            }
            return authenticator.extendOrCreateToken(voter);
        } catch (SQLException e) {
            throw new BiPollerError(e.getMessage());
        }
    }

    @POST
    @Path("/logout")
    @RolesAllowed(AuthRoles.VOTER)
    public void logout(@Auth Voter voter) throws SQLException {
        Optional<AccessToken> optToken = tokenDAO.getByVoterID(voter.getId());
        if (optToken.isPresent()) {
            tokenDAO.delete(optToken.get());
        } else {
            throw new BiPollerError("You are not logged in.", Response.Status.UNAUTHORIZED);
        }
    }
}
