package com.bipoller.auth;

import com.bipoller.database.AccessTokenDAO;
import com.bipoller.database.VoterDAO;
import com.bipoller.models.AccessToken;
import com.bipoller.models.Voter;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

/**
 * A manager that keeps an in-memory hash map of valid access tokens and provides
 * methods for accessing
 */
@AllArgsConstructor
public class BiPollerAuthenticator implements Authenticator<UUID, Voter> {
    private VoterDAO voterDAO;
    private AccessTokenDAO accessTokenDAO;

    /**
     * Gets the access token registered for the provided voter, or creates a new one if it doesn't exist.
     *
     * @param voter The voter who has been successfully authenticated.
     * @return A new token that's valid for the provided Voter.
     */
    public AccessToken extendOrCreateToken(Voter voter) throws SQLException {
        Optional<AccessToken> optToken = accessTokenDAO.getByVoterID(voter.getId());
        if (optToken.isPresent()) {
            // If we have an existing token, then extend its lifetime another 2 days.
            accessTokenDAO.extendLifetime(optToken.get());
        } else {
            optToken = Optional.of(accessTokenDAO.create(voter));
        }
        return optToken.get();
    }

    /**
     * Gets the voter associated with the access token with the provided UUID.
     * This voter can be considered "authenticated", and has the permissions to
     * make responses to polls on this voter's behalf.
     *
     * @param uuid The UUID of the access token
     * @return A Voter if the UUID is a valid AccessToken.
     */
    @Override
    public Optional<Voter> authenticate(UUID uuid) throws AuthenticationException {
        try {
            Optional<AccessToken> optToken = accessTokenDAO.getById(uuid);

            // Bail if we didn't get a token.
            if (!optToken.isPresent()) {
                return Optional.empty();
            }
            AccessToken token = optToken.get();

            // Bail if the token is expired.
            if (token.isExpired()) {
                accessTokenDAO.delete(token);
                return Optional.empty();
            }

            // Otherwise extend the token's life and return the voter.
            accessTokenDAO.extendLifetime(token);
            return voterDAO.getById(token.getVoter().getId());
        } catch (SQLException e) {
            //Log error
            return Optional.empty();
        }
    }
}
