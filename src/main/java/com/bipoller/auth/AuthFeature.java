package com.bipoller.auth;

import org.glassfish.jersey.server.model.AnnotatedMethod;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

/**
 * A Dropwizard feature for authorizing user requests.
 */
public class AuthFeature implements DynamicFeature {

    private BiPollerAuthFilter authFilter;

    public AuthFeature(BiPollerAuthFilter filter) {
        this.authFilter = filter;
    }

    /**
     * Configures an annotated API method to requre authorization for a given set of roles.
     * @param resourceInfo An object containing metadata about the resource method.
     * @param context A context provided by Dropwizard for us to register into.
     */
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        AnnotatedMethod method = new AnnotatedMethod(resourceInfo.getResourceMethod());
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            context.register(authFilter);
        }
    }
}
