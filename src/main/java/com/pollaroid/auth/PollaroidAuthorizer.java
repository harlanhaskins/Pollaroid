package com.pollaroid.auth;

import com.pollaroid.models.Voter;
import io.dropwizard.auth.Authorizer;

/**
 * Created by harlan on 4/3/17.
 */
public class PollaroidAuthorizer implements Authorizer<Voter> {

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
