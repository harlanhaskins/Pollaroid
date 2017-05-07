package com.pollaroid.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor
public final class AccessToken {
    @NotNull
    private UUID uuid;

    @NotNull
    @JsonIgnore
    private Voter voter;

    @NotNull
    private ZonedDateTime expiration;

    public UUID getUuid() {
        return uuid;
    }

    public Voter getVoter() {
        return voter;
    }

    public ZonedDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(ZonedDateTime dateTime) {
        this.expiration = dateTime;
    }

    @JsonIgnore
    public boolean isExpired() {
        return getExpiration().isBefore(ZonedDateTime.now(ZoneId.of("UTC")));
    }
}