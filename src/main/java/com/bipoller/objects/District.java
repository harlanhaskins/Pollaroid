package com.bipoller.objects;

/**
 * Created by lshadler on 3/27/17.
 */
public class District {
    int id;
    int districtNo;
    String state;
    boolean isSenate;

    public District(int newID, int newdistrictNo, String stateIdent, boolean senateVal){
        this.id = newID;
        this.districtNo = newdistrictNo;
        this.state = stateIdent;
    }

    public District(String [] data){
        this.id         = Integer.parseInt(data[0]);
        this.districtNo = Integer.parseInt(data[1]);
        this.state      = data[2];
        this.isSenate   = Boolean.parseBoolean(data[3]);
    }

    public int getDistrictNo() {
        return districtNo;
    }

    public String getState() {
        return state;
    }

    public boolean isSenate() {
        return isSenate;
    }

    public int getId() {
        return id;
    }
}
