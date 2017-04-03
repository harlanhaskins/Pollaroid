package com.bipoller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by harlan on 4/1/17.
 */
public class BiPollerConfiguration extends Configuration {
    @Valid
    @JsonProperty
    /**
     * The allowed grant types for OAuth users.
     */
    private String[] allowedGrantTypes;

    @Valid
    @JsonProperty
    /**
     * The Bearer realm for OAuth users.
     */
    private String bearerRealm;
}
