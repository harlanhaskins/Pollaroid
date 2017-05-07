package com.pollaroid.auth;

import com.pollaroid.models.Voter;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

/**
 * A filter that filters resources based on their requirements for authentication.
 */
@Priority(Priorities.AUTHENTICATION)
public class PollaroidAuthFilter extends AuthFilter<UUID, Voter> {

    public PollaroidAuthFilter(PollaroidAuthenticator authenticator) {
        this.authorizer = new PollaroidAuthorizer();
        this.authenticator = authenticator;
    }

    /**
     * Gets the current authenticated user or throws an UNAUTHORIZED exception if the user is not correctly
     * authenticated.
     * @param uuidString The UUID string from the headers.
     * @return The voter who has an access token registered with that UUID.
     * @throws WebApplicationException If the authorization failed or the credentials were invalid.
     */
    private Voter getAuthenticatedUser(String uuidString) throws WebApplicationException {
        WebApplicationException exc = new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        Optional<Voter> v;
        try {
            UUID uuid = UUID.fromString(uuidString);
            v = authenticator.authenticate(uuid);
        } catch (AuthenticationException | IllegalArgumentException | NullPointerException e) {
            throw exc;
        }
        if (!v.isPresent()) {
            throw exc;
        }
        return v.get();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String key = requestContext.getHeaderString("X-API-KEY");
        Voter v = getAuthenticatedUser(key);
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return v;
            }

            @Override
            public boolean isUserInRole(String role) {
                return authorizer.authorize(v, role);
            }

            @Override
            public boolean isSecure() {
                return requestContext.getSecurityContext().isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Pollaroid Authorization";
            }
        });
    }
}
