package com.savant.savantandroidteam.poker;

import java.util.ArrayList;

/*
*
* SessionItem.java
*
* This object will store all info related to a poker session.
*
* name: String - The name of the session
* host: String - The first name of the one who created it
* responses: ArrayList - The names and responses of all users
* id: String - number to create session uniqueness
* revealed: String - has the host ended the session?
*
*/


public class SessionItem {

    private String name, host, id, revealed, hostEmail;
    private ArrayList<Tuple> responses;


    public SessionItem(){
        hostEmail = "";
        name = "";
        host = "";
        responses = new ArrayList<>();
        id = "";
        revealed = "false";
    }

    public SessionItem(String name, String host){
        this.name = name;
        this.host = host;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public ArrayList<Tuple> getResponses() {
        return responses;
    }

    public void setResponses(ArrayList<Tuple> responses) {
        this.responses = responses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevealed() {
        return revealed;
    }

    public void setRevealed(String revealed) {
        this.revealed = revealed;
    }
}
