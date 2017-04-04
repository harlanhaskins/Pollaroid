package com.bipoller.auth;

import com.bipoller.Voter;
import io.dropwizard.auth.Authorizer;

/**
 * Created by harlan on 4/3/17.
 */
public class BiPollerAuthorizer implements Authorizer<Voter> {

    @Override
    public boolean authorize(Voter principal, String role) {
        switch (role) {
            case AuthRoles.VOTER:
                return true;
            case AuthRoles.REPRESENTATIVE:
                return !principal.getRepresentingDistrict().isPresent();
            default:
                return false;
        }
    }

}
