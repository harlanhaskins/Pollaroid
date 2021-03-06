package com.pollaroid.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * Represents one of the options of a Poll.
 */
@RequiredArgsConstructor
public class PollOption {
    @NonNull
    private Long id;

    @NonNull
    @JsonIgnore
    private Long pollID;

    @NonNull
    private String text;

    @NonNull
    private Long votes;

    public Long getId() {
        return id;
    }

    public Long getPollID() {
        return pollID;
    }

    public String getText() {
        return text;
    }

    public Long getVotes() {
        return votes;
    }
}
