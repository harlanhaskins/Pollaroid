package com.bipoller.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PollRecord {
    private Poll poll;
    private Voter voter;
    private PollOption choice;

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
