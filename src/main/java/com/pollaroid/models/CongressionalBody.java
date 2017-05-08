package com.pollaroid.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines a body of Congress.
 */
public enum CongressionalBody {
    HOUSE, SENATE;

    @Override
    @JsonValue
    public String toString() {
        switch (this) {
            case HOUSE:
                return "House";
            case SENATE:
                return "Senate";
        }
        return null;
    }

    /**
     * Whether this body is the SENATE.
     */
    public boolean isSenate() {
        return this == SENATE;
    }

    /**
     * Whether this body is the HOUSE.
     */
    public boolean isHouse() {
        return this == HOUSE;
    }
}
