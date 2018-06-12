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

public class ResultItem {

    private String name, result, picId, animal;


    public ResultItem(){
        name = "";
        result = "";
        picId = "";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
        setAnimal(result);
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    private void setAnimal(String res) {
        String retAnimal = "";
        if(res.equals("1")) retAnimal = "Mouse";
        else if(res.equals("2")) retAnimal = "Groundhog";
        else if(res.equals("3")) retAnimal = "Fox";
        else if(res.equals("5")) retAnimal = "Lion";
        else if(res.equals("8")) retAnimal = "Buffalo";
        else if(res.equals("13")) retAnimal = "Elephant";
        else if(res.equals("21")) retAnimal = "Honey Badger";
        else retAnimal = res;

        animal = retAnimal;
    }

    public String getAnimal(){
        return animal;
    }

}
