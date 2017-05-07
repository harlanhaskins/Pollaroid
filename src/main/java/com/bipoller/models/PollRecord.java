package com.bipoller.models;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class PollRecord {
    private Long id;
    private Poll poll;
    private Voter voter;
    private Optional<PollOption> choice;

    public Long getId() {
        return id;
    }

    public Poll getPoll() {
        return poll;
    }

    public Voter getVoter() {
        return voter;
    }

    public Optional<PollOption> getChoice() {
        return choice;
    }
}
