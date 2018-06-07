package com.savant.savantandroidteam.poker;

/*
*
* Tuple.java
*
* (String name, String response)
*
* The purpose for this object is to group together a users
* Name with their response in poker.
* Used for uploading to fire base.
*
* Could be more general but firebase is the only use for it now.
*
*/

public class Tuple {
    private String name, response;
    public Tuple(String name, String response){
        this.name = name;
        this.response = response;
    }

    public String getName() {
        return name;
    }

    public String getResponse() {
        return response;
    }
}
