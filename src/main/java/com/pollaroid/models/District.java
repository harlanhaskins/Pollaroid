package com.pollaroid.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import unitedstates.US;

@AllArgsConstructor
public class District {
    private long id;
    private int number;

    @JsonIgnore
    private US state;

    @JsonIgnore
    private CongressionalBody congressionalBody;

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    @JsonIgnore
    public US getState() {
        return state;
    }

    public String getStateName() { return state.getUnabbreviated(); }
    public String getStateCode() { return state.getANSIAbbreviation(); }

    public CongressionalBody getCongressionalBody() {
        return congressionalBody;
    }

    @JsonProperty
    public boolean isSenate() {
        return congressionalBody == CongressionalBody.SENATE;
    }

    public boolean isHouse() {
        return congressionalBody == CongressionalBody.HOUSE;
    }
}
