package com.bipoller.models;

/**
 * Defines a body of Congress.
 */
public enum CongressionalBody {
    HOUSE, SENATE;

    @Override
    public String toString() {
        switch (this) {
            case HOUSE:
                return "House";
            case SENATE:
                return "Senate";
        }
        return null;
    }
}
