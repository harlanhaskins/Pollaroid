package com.bipoller;

import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public final class AccessToken {
    @NotNull
    private UUID uuid;

    @NotNull
    private long voterID;

    @NotNull
    private DateTime expiration;

    /**
     * Creates a new AccessToken with the provided values.
     * @param uuid The UUID of the token to create.
     * @param voterID The ID of the voter who owns this token.
     * @param expiration The date time this access token will expire.
     */
    private AccessToken(UUID uuid, long voterID, DateTime expiration) {
        this.uuid = uuid;
        this.voterID = voterID;
        this.expiration = expiration;
    }

    /**
     * Creates an AccessToken for the provided Voter, with the default expiration date of 2 days.
     * @param voterID The voter to create.
     */
    public AccessToken(long voterID) {
        this.uuid = UUID.randomUUID();
        this.voterID = voterID;
        this.expiration = DateTime.now().withDurationAdded(Days.TWO.toStandardDuration(), 1);
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getVoterID() {
        return voterID;
    }

    public DateTime getExpiration() {
        return expiration;
    }

    public boolean isExpired() {
        return getExpiration().isBeforeNow();
    }

    /**
     * Creates a new AccessToken with the provided UUID, but set to expire two days later.
     * @return A new AccessToken with an extended lifetime.
     */
    public AccessToken withExtendedLifetime() {
        DateTime newExpiration = getExpiration().withDurationAdded(Days.TWO.toStandardDuration(), 1);
        return new AccessToken(getUuid(), getVoterID(), newExpiration);
    }
}