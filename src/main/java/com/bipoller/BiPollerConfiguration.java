package com.bipoller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.bundles.assets.AssetsBundleConfiguration;
import io.dropwizard.bundles.assets.AssetsConfiguration;

import javax.validation.Valid;

/**
 * Created by harlan on 4/1/17.
 */
public class BiPollerConfiguration extends Configuration implements AssetsBundleConfiguration {
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

    @Valid
    @JsonProperty
    private final AssetsConfiguration assets = AssetsConfiguration.builder().build();

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }
}
