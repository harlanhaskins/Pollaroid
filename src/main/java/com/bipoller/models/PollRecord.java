package com.bipoller.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PollRecord {
    private Long id;
    private Poll poll;
    private Voter voter;
    private PollOption choice;

    public Long getId() {
        return id;
    }

    public Poll getPoll() {
        return poll;
    }

    public Voter getVoter() {
        return voter;
    }

    public PollOption getChoice() {
        return choice;
    }
}
