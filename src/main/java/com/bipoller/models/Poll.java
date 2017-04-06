package com.bipoller.models;

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
}
