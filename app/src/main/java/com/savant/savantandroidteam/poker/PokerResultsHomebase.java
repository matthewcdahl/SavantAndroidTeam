package com.savant.savantandroidteam.poker;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*
* This class acts as a center spot for all poker Firebase updates.
* It is not in charge of everything but is a good way to keep it all contained.
*
* It will listen for changes and update its variables appropriately
 */

public class PokerResultsHomebase {

    //Firebase
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private DatabaseReference mPokerRef;


    String sessionID;

    //Private Variable for inner use
    public DataSnapshot mDataSnapshot;
    private DataSnapshot mPokerSnapshot;
    private DataSnapshot mUsersSnapshot;


    public PokerResultsHomebase(DataSnapshot ss, String id){ // For loading purposes this will help speed things along if the snapshot is available
        sessionID = id;
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRootRef = mDatabase.getReference();
        mPokerRef = mRootRef.child("poker");
        mDataSnapshot = ss;
        mPokerSnapshot = mDataSnapshot.child("poker");
        mUsersSnapshot = mDataSnapshot.child("users");
    }

    public PokerResultsHomebase(DataSnapshot ss){ // For loading purposes this will help speed things along if the snapshot is available
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRootRef = mDatabase.getReference();
        mPokerRef = mRootRef.child("poker");
        mDataSnapshot = ss;
        mPokerSnapshot = mDataSnapshot.child("poker");
        mUsersSnapshot = mDataSnapshot.child("users");
    }




    //This is the home for reading from firebase. It takes the snapshot and
    //puts it into a list of SessionItems
    public List<SessionItem> getSessions(){
        List<SessionItem> finalList = new ArrayList<>();
        DataSnapshot ss = mPokerSnapshot;
        Iterable<DataSnapshot> children = ss.getChildren();


        for(DataSnapshot child: children){
            if(child.getKey().toString().equals("sessions")){

            }
            else {
                Iterable<DataSnapshot> metaData = child.getChildren();
                SessionItem toAdd = new SessionItem();
                toAdd.setId(child.getKey().toString());
                for (DataSnapshot metaChild : metaData) {
                    String key = metaChild.getKey().toString();
                    if (key.equals("host")) {
                        toAdd.setHost(metaChild.getValue().toString());
                    } else if (key.equals("name")) {
                        toAdd.setName(metaChild.getValue().toString());
                    } else if(key.equals("revealed")){
                        toAdd.setRevealed(metaChild.getValue().toString());
                    } else {
                        ArrayList<Tuple> arr = new ArrayList<Tuple>();
                        Iterable<DataSnapshot> resIter = metaChild.getChildren();
                        for (DataSnapshot resIterChild : resIter) {
                            String userName = resIterChild.getKey().toString();
                            String diff = resIterChild.getValue().toString();
                            Tuple addTup = new Tuple(userName, diff);
                            arr.add(addTup);
                        }
                        toAdd.setResponses(arr);
                    }
                }
                finalList.add(toAdd);
            }

        }
        //printArrayList(finalList);
        return finalList;
    }


    //For debugging purposes - do not use in distribution
    private void printArrayList(List<SessionItem> arr){
        for(int i = 0; i<arr.size(); i++){
            SessionItem curr = arr.get(i);
            String name = curr.getName();
            String host = curr.getHost();
            String id = curr.getId();
            String revealed = curr.getRevealed();
            ArrayList<Tuple> tupArr = curr.getResponses();

            Log.d("NAME", name);
            Log.d("HOST", host);
            Log.d("ID", id);
            Log.d("REVEALED", revealed);
            Log.d("RESPONSES------", "-------------");

            for(int j = 0; j<tupArr.size(); j++){
                Tuple currTup = tupArr.get(j);
                String Uname = currTup.getName();
                String diff = currTup.getResponse();
                Log.d("R Name", Uname);
                Log.d("R diff", diff);


            }

        }
    }



    public boolean isRevealed(int position){
        if(position == -1) return false;

        String revealed = getSessions().get(position).getRevealed();
        if(revealed.equals("false")) return false;
        else return true;
    }









    public String getUserName(){
        String email = mAuth.getCurrentUser().getEmail();
        String hold = email.substring(0, email.indexOf("."));
        String userName = hold.substring(0, 1).toUpperCase() + hold.substring(1);
        return userName;
    }




    private int getIdPos(int pos){

        List<SessionItem> list = getSessions();

        return Integer.parseInt(list.get(pos).getId());
    }

    public int getPosId(String id){

        List<SessionItem> list = getSessions();
        for(int i = 0; i<list.size(); i++){
            if(list.get(i).getId().equals(id)) return i;
        }
        return -1;

    }

    public List<ResultItem> getResults(){
        List<ResultItem> finalList = new ArrayList<>();
        //DataSnapshot ss = getSnapshot();


        DataSnapshot currRef = mPokerSnapshot.child(sessionID);
        DataSnapshot respRef = currRef.child("responses");
        Iterable<DataSnapshot> children = respRef.getChildren();

        for(DataSnapshot child: children) {
            ResultItem toAdd = new ResultItem();
            toAdd.setName(getNickname(child.getKey()));
            toAdd.setResult(child.getValue().toString());
            toAdd.setPicId(getUserPicture(child.getKey()));

            finalList.add(toAdd);


        }
        //printResultsArrayList(finalList);
        return finalList;
    }

    private String getUserPicture(String name){
        DataSnapshot userRef = mUsersSnapshot;
        Iterable<DataSnapshot> iter = userRef.getChildren();

        for(DataSnapshot child: iter){
            if(name.equals(child.getKey())){
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for(DataSnapshot child2: iter2){
                    if(child2.getKey().equals("picture")){
                        return child2.getValue().toString();
                    }
                }
            }
        }

        return "15";
    }

    public String getNickname(String email){
        System.out.println("host email: " + email);
        DataSnapshot userRef = mUsersSnapshot;
        System.out.println("USER REF: " + userRef.toString());
        Iterable<DataSnapshot> iter = userRef.getChildren();
        for(DataSnapshot child: iter){
            if(email.equals(child.getKey())){
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for(DataSnapshot child2: iter2){
                    if(child2.getKey().equals("nickname")){
                        System.out.println("child2  " + child2.getValue().toString());
                        return child2.getValue().toString();
                    }
                }
            }
        }
        System.out.println("Before Return: " + email);
        return getFirstName(email);
    }



    //For debugging purposes - do not use in distribution
    private void printResultsArrayList(List<ResultItem> arr){
        for(int i = 0; i<arr.size(); i++){
            ResultItem curr = arr.get(i);
            String name = curr.getName();
            String result = curr.getResult();
            //String picId = curr.getId();


            Log.d("NAME", name);
            Log.d("Result", result);
            //Log.d("ID", id);


        }
    }


    public String getResultSessionName(String id){

        int idPos = getPosId(id);
        List<SessionItem> list = getSessions();
        String name = list.get(idPos).getName();
        return name;
    }

    public void removeSession(String id){
        mPokerRef.child(id).removeValue();
    }

    public String getFirstName(String email){
        System.out.println(email);
        String name = email.substring(0, email.indexOf(','));
        try {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }catch (Exception e){
            return name.substring(0, 1).toUpperCase();
        }
    }




}
