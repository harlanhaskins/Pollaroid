package com.bipoller.resources;

import com.bipoller.AccessToken;
import com.bipoller.auth.AuthRoles;
import com.bipoller.auth.BiPollerAuthenticator;
import com.bipoller.Voter;
import io.dropwizard.auth.Auth;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private BiPollerAuthenticator authenticator;
    private Connection connection;

    public AuthResource(BiPollerAuthenticator authenticator, Connection connection) {
        this.connection = connection;
        this.authenticator = authenticator;
    }

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
     * @param credentials
     * @return
     */
    @POST
    public AccessToken login(@Valid APICredentials credentials) {
        try {
            Optional<Voter> v = Voter.getByEmail(connection, credentials.email);
            if (!v.isPresent()) {
                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("User with email " + credentials.email + " not found")
                        .build());
            }
            Voter voter = v.get();
            boolean isCorrectPassword = BCrypt.checkpw(credentials.password, voter.getPasswordHash());
            if (!isCorrectPassword) {
                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Incorrect password.")
                        .build());
            }
            return authenticator.extendOrCreateToken(voter);
        } catch (SQLException e) {
            Response response =
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage())
                            .build();
            throw new WebApplicationException(response);
        }
    }

    @POST
    @Path("/logout")
    @RolesAllowed(AuthRoles.VOTER)
    public void logout(@Auth Voter voter) throws SQLException {
        Optional<AccessToken> optToken = AccessToken.getByVoterID(connection, voter.getId());
        if (optToken.isPresent()) {
            optToken.get().delete(connection);
        } else {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("You are not logged in.")
                    .build());
        }
    }
}
