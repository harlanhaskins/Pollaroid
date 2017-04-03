package com.bipoller.resources;

import com.bipoller.AccessToken;
import com.bipoller.Authenticator;
import com.bipoller.Voter;
import org.mindrot.jbcrypt.BCrypt;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private Authenticator authenticator = new Authenticator();
    private Connection connection;

    public AuthResource(Connection connection) {
        this.connection = connection;
    }

    public static class APICredentials {
        public String email;
        public String password;
    }

    @POST
    public AccessToken login(APICredentials credentials) {
        try {
            Optional<Voter> v = Voter.getByEmail(connection, credentials.email);
            if (!v.isPresent()) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
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
}
