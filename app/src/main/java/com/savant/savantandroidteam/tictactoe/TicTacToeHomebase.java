package com.savant.savantandroidteam.tictactoe;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.meetings.MeetingItem;
import com.savant.savantandroidteam.poker.SessionItem;
import com.savant.savantandroidteam.poker.Tuple;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeHomebase {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mTicTacToeRef;

    //Private Variable for inner use
    public DataSnapshot mDataSnapshot;

    public TicTacToeHomebase(DataSnapshot ss) {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDataSnapshot = ss;
    }


    /**
     *
     * @return a list of all current scheduled meetings and their info in the form of a MeetingIem list
     */
    public List<TicTacToeItem> getGames() {
        List<TicTacToeItem> finalList = new ArrayList<>();
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        if(iter!=null) {
            for (DataSnapshot child : iter) {
                TicTacToeItem toAdd = new TicTacToeItem();
                Iterable<DataSnapshot> iter2 = child.getChildren();
                toAdd.setName(child.getKey());
                for (DataSnapshot child2 : iter2) {
                    if (child2.getKey().equals("host")) toAdd.setHost(child2.getValue().toString());
                    else if (child2.getKey().equals("opp"))
                        toAdd.setOpp(child2.getValue().toString());
                    else if (child2.getKey().equals("turn"))
                        toAdd.setTurn(child2.getValue().toString());
                    else if (child2.getKey().equals("finished"))
                        toAdd.setFinished(child2.getValue().toString());
                    else if (child2.getKey().equals("tl"))
                        toAdd.setTl(child2.getValue().toString());
                    else if (child2.getKey().equals("tc"))
                        toAdd.setTc(child2.getValue().toString());
                    else if (child2.getKey().equals("tr"))
                        toAdd.setTr(child2.getValue().toString());
                    else if (child2.getKey().equals("ml"))
                        toAdd.setMl(child2.getValue().toString());
                    else if (child2.getKey().equals("mc"))
                        toAdd.setMc(child2.getValue().toString());
                    else if (child2.getKey().equals("mr"))
                        toAdd.setMr(child2.getValue().toString());
                    else if (child2.getKey().equals("bl"))
                        toAdd.setBl(child2.getValue().toString());
                    else if (child2.getKey().equals("bc"))
                        toAdd.setBc(child2.getValue().toString());
                    else if (child2.getKey().equals("br"))
                        toAdd.setBr(child2.getValue().toString());
                }
                finalList.add(toAdd);
            }

        }
        printArrayList(finalList);
        return finalList;
    }

    //For debugging purposed only!
    public void printArrayList(List<TicTacToeItem> arr) {
        for (int i = 0; i < arr.size(); i++) {
            TicTacToeItem curr = arr.get(i);
            String name = curr.getName();
            System.out.println(name);
            String host = curr.getHost();
            String opp = curr.getOpp();
            String finished = curr.getFinished();

            Log.d("**********************", "*********************");


            Log.d("NAME", name);
            Log.d("HOST", host);
            Log.d("OPP", opp);
            Log.d("FINISHED", finished);

            Log.d("**********************", "*********************");
        }
    }

    public int getNumberOfSessions() {
        return getGames().size();
    }


    //Get all variables associated with the meetings.
    public String getName(int pos) {
        List<TicTacToeItem> list = getGames();
        return list.get(pos).getName();
    }



    public void deleteAllMeetings(){
        mRootRef.removeValue();
    }

    public List<String> getUsers(){
        List<String> toRtn = new ArrayList<>();
        toRtn.add("Select Opponent");

        Iterable<DataSnapshot> iter = getConcentratedSnapshot("users");
        boolean isHost = false;
        for(DataSnapshot child: iter){
            Iterable<DataSnapshot> iter2 = child.getChildren();
            boolean hasNickName =false;
            for(DataSnapshot child2: iter2){
                if(child2.getKey().equals("nickname")){
                    hasNickName = true;
                    if(!child.getKey().equals(getModifiedEmail())) {
                        toRtn.add(child2.getValue().toString());
                    }
                    else isHost = true;

                }
            }
            if(!hasNickName && !isHost) toRtn.add(getNameFromEmail(child.getKey()));
            isHost = false;
        }

        return toRtn;
    }

    private Iterable<DataSnapshot> getConcentratedSnapshot(String str){
        Iterable<DataSnapshot> iter = mDataSnapshot.getChildren();
        for(DataSnapshot child: iter){
            if(child.getKey().equals(str)){
                return child.getChildren();
            }
        }
        return null;
    }

    private String getNameFromEmail(String email){
        email = email.replace('.', ',');
        email = email.substring(0, email.indexOf(','));
        if(email.length() > 1)return email.substring(0,1).toUpperCase() + email.substring(1);
        else return email.toUpperCase();
    }

    public String getNicknameOfDeviceUser(){
        String email = getModifiedEmail();
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("users");
        for(DataSnapshot child: iter){
            if(child.getKey().equals(email)){
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for(DataSnapshot child2: iter2){
                    if(child2.getKey().equals("nickname")){
                        return child2.getValue().toString();
                    }
                }
                return getNameFromEmail(child.getKey());
            }
        }
        return "ERROR";
    }

    /**
     *
     * @return the current users email with ',' instead of '.'
     */
    private String getModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
    }

    public String getNextGameID(){
        List<TicTacToeItem> games = getGames();
        int size = games.size();
        if(size == 0) return "1";
        else {
            String currId = games.get(size - 1).getName();
            int currIdInt = Integer.parseInt(currId);
            currIdInt++;
            return Integer.toString(currIdInt);
        }
    }

}
