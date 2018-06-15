package com.savant.savantandroidteam.meetings;

import com.savant.savantandroidteam.poker.Tuple;

import java.util.ArrayList;

public class MeetingItem {

    private String name, desc, date, time, id, place;


    public MeetingItem(){
        name = "Unnamed Meeting";
        desc = "No Description";
        date = "9-9-9999";
        time = "11:59 PM";
        id = "0";
        place = "Not Listed";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace() { return place; }

    public void setPlace(String place) { this.place = place; }
}
