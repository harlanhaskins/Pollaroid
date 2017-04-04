package com.bipoller.auth;

/**
 * The different roles that a user can have.
 */
public class AuthRoles {
    /**
     * The user is a Voter, and may or may not be a Representative.
     */
    public final static String VOTER = "voter";

    /**
     * The user is a Representative.
     */
    public final static String REPRESENTATIVE = "representative";
}
