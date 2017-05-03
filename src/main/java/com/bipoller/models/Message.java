package com.bipoller.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import java.time.ZonedDateTime;

@AllArgsConstructor
public class Message {
    private long id;
    private long fromId;
    private long toId;
    private String text;
    private ZonedDateTime timeSent;

    public long getId(){ return this.id; }
    public long getFromId(){ return this.fromId; }
    public long getToId(){ return this.toId; }
    public String getText(){ return this.text; }
    public ZonedDateTime getTimeSent(){ return this.timeSent; }

}
