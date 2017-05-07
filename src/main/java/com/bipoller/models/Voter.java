package com.bipoller.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;

import java.security.Principal;
import java.util.Optional;

@AllArgsConstructor
public class Voter implements Principal {
    private long id;
    private String name;

    @JsonIgnore // Don't return the password hash when serializing
    private String passwordHash;

    private String phoneNumber;
    private String address;
    private String email;

    private District houseDistrict;
    private District senateDistrict;
    private Optional<District> representingDistrict;

    public long getId() {
        return id;
    }

    @Override public String getName() {
        return name;
    }

    public District getHouseDistrict() {
        return houseDistrict;
    }

    public District getSenateDistrict() {
        return senateDistrict;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public boolean isInDistrict(District district) {
        return district.getId() == getHouseDistrict().getId() || district.getId() == getSenateDistrict().getId();
    }

    public boolean isRepresentative() {
        return getRepresentingDistrict().isPresent();
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public Optional<District> getRepresentingDistrict() {
        return representingDistrict;
    }
}
