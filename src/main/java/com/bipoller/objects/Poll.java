package com.bipoller.objects;

/**
 * Created by lshadler on 3/27/17.
 */
public class Poll {
    int id;
    String title;

    public Poll(int new_id, String new_title){
        this.id = new_id;
        this.title = new_title;
    }

    public Poll(String [] data){
        this.id = Integer.parseInt(data[0]);
        this.title = data[1];
    }

    public int getId() { return this.id; }

    public String getTitle() { return this.title; }
}
