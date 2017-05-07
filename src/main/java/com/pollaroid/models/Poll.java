package com.pollaroid.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class Poll {
    private long id;
    private Voter submitter;
    private District district;
    private String title;
    private List<PollOption> options;

    public long getId() {
        return id;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public Voter getSubmitter() {
        return submitter;
    }

    public District getDistrict() {
        return district;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty
    public long getNumberOfVotes() {
        long sum = 0;
        for (PollOption option : options) {
            sum += option.getVotes();
        }
        return sum;
    }
}
