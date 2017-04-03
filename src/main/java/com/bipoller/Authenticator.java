package com.bipoller;

import javax.persistence.Access;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * A manager that keeps an in-memory hash map of valid access tokens.
 */
public class Authenticator {
    /**
     * A map of Voter IDs to their token.
     */
    private HashMap<Long, AccessToken> tokenVoterMap = new HashMap<>();

    /**
     * A map of AccessToken UUIDs to their token.
     */
    private HashMap<UUID, AccessToken> tokenUUIDMap = new HashMap<>();

    /**
     * Gets the access token registered for the provided voter, or creates a new one if it doesn't exist.
     * @param voter The voter who has been successfully authenticated.
     * @return
     */
    public AccessToken extendOrCreateToken(Voter voter) {
        AccessToken token;
        if (tokenVoterMap.containsKey(voter.getId())) {
            token = tokenVoterMap.get(voter.getId()).withExtendedLifetime();
        } else {
            token = new AccessToken(voter.getId());
        }
        addToken(token);
        return token;
    }

    /**
     * Adds the provided token to the token map
     * @param token
     */
    private void addToken(AccessToken token) {
        tokenVoterMap.put(token.getVoterID(), token);
        tokenUUIDMap.put(token.getUuid(), token);
    }

    /**
     * Gets the access token with the provided UUID.
     * @param uuid The UUID of the token you're looking for.
     * @return A optional value that will be present if there is an unexpired token with the provided UUID.
     */
    public Optional<AccessToken> getTokenByUUID(UUID uuid) {
        if (tokenUUIDMap.containsKey(uuid)) {
            AccessToken token = tokenUUIDMap.get(uuid);
            if (token.isExpired()) {
                return Optional.empty();
            }
            AccessToken extendedToken = token.withExtendedLifetime();
            addToken(extendedToken);
            return Optional.of(extendedToken);
        }
        return Optional.empty();
    }
}
