package com.bipoller.objects;

/**
 * Created by lshadler on 3/27/17.
 */
public class Voter {
    int id;
    String name;
    int house_district;
    int senate_district;
    String phoneNo;
    String address;
    String email;
    String pass_hash;
    String pass_salt;
    int district_representing;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHouse_district() {
        return house_district;
    }

    public int getSenate_district() {
        return senate_district;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPass_hash() {
        return pass_hash;
    }

    public String getPass_salt() {
        return pass_salt;
    }

    public int getDistrict_representing() {
        return district_representing;
    }

    public Voter(int id, String name, int house_district, int senate_district, String phoneNo, String address, String email, String pass_hash, String pass_salt, int district_representing) {

        this.id = id;
        this.name = name;
        this.house_district = house_district;
        this.senate_district = senate_district;
        this.phoneNo = phoneNo;
        this.address = address;
        this.email = email;
        this.pass_hash = pass_hash;
        this.pass_salt = pass_salt;
        this.district_representing = district_representing;
    }
}
