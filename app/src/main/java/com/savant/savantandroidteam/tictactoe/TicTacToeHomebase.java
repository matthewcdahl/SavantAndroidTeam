package com.savant.savantandroidteam.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.R;
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
    private Context context;

    public TicTacToeHomebase(DataSnapshot ss, Context context) {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDataSnapshot = ss;
        this.context = context;
    }


    /**
     * @return a list of all current scheduled meetings and their info in the form of a MeetingIem list
     */
    public List<TicTacToeItem> getGames() {
        List<TicTacToeItem> finalList = new ArrayList<>();
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        if (iter != null) {
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
        //printArrayList(finalList);
        return finalList;
    }

    //For debugging purposed only!
    public void printArrayList(List<TicTacToeItem> arr) {
        for (int i = 0; i < arr.size(); i++) {
            TicTacToeItem curr = arr.get(i);
            String name = curr.getName();
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


    public void deleteAllMeetings() {
        mRootRef.removeValue();
    }

    public List<String> getUsers() {
        List<String> toRtn = new ArrayList<>();
        toRtn.add("Select Opponent");

        Iterable<DataSnapshot> iter = getConcentratedSnapshot("users");
        boolean isHost = false;
        for (DataSnapshot child : iter) {
            Iterable<DataSnapshot> iter2 = child.getChildren();
            boolean hasNickName = false;
            for (DataSnapshot child2 : iter2) {
                if (child2.getKey().equals("nickname")) {
                    hasNickName = true;
                    if (!child.getKey().equals(getModifiedEmail())) {
                        toRtn.add(child2.getValue().toString());
                    } else isHost = true;

                }
            }
            if (!hasNickName && !isHost) toRtn.add(getNameFromEmail(child.getKey()));
            isHost = false;
        }

        return toRtn;
    }

    private Iterable<DataSnapshot> getConcentratedSnapshot(String str) {
        Iterable<DataSnapshot> iter = mDataSnapshot.getChildren();
        for (DataSnapshot child : iter) {
            if (child.getKey().equals(str)) {
                return child.getChildren();
            }
        }
        return null;
    }

    private String getNameFromEmail(String email) {
        email = email.replace('.', ',');
        email = email.substring(0, email.indexOf(','));
        if (email.length() > 1) return email.substring(0, 1).toUpperCase() + email.substring(1);
        else return email.toUpperCase();
    }

    public String getNicknameOfHost() {
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        SharedPreferences tttPrefs = context.getSharedPreferences("tictactoe", Context.MODE_PRIVATE);
        String currGamePos = tttPrefs.getString("gamePos", "0");
        String currGameID = getIdFromPos(currGamePos);
        for (DataSnapshot child : iter) {
            if (child.getKey().equals(currGameID)) {
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for (DataSnapshot child2 : iter2) {
                    if (child2.getKey().equals("host")) {
                        return child2.getValue().toString();
                    }
                }

            }
        }
        return "";
    }

    /**
     * @return the current users email with ',' instead of '.'
     */
    private String getModifiedEmail() {
        return mAuth.getCurrentUser().getEmail().trim().replace('.', ',');
    }

    public String getNextGameID() {
        List<TicTacToeItem> games = getGames();
        int size = games.size();
        if (size == 0) return "1";
        else {
            String currId = games.get(size - 1).getName();
            int currIdInt = Integer.parseInt(currId);
            currIdInt++;
            return Integer.toString(currIdInt);
        }
    }

    public String getTurn() {
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        SharedPreferences tttPrefs = context.getSharedPreferences("tictactoe", Context.MODE_PRIVATE);
        String currGamePos = tttPrefs.getString("gamePos", "0");
        String currGameID = getIdFromPos(currGamePos);
        for (DataSnapshot child : iter) {
            if (child.getKey().equals(currGameID)) {
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for (DataSnapshot child2 : iter2) {
                    if (child2.getKey().equals("turn")) {
                        return child2.getValue().toString();
                    }
                }

            }
        }
        return "";
    }

    private String getIdFromPos(String pos) {
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        int count = 0;
        if (iter == null) return "-1";
        for (DataSnapshot child : iter) {
            if (Integer.toString(count).equals(pos)) {
                return child.getKey();
            }
            count++;
        }
        return "-1";
    }

    public String getPosFromId(String id) {
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        int count = 0;
        if (iter == null) return "0";
        for (DataSnapshot child : iter) {
            System.out.println(child.toString());
            if (child.getKey().equals(id)) {
                return Integer.toString(count);
            }
            count++;
        }
        return "-1";
    }

    public String getNicknameOfOpp() {
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        SharedPreferences tttPrefs = context.getSharedPreferences("tictactoe", Context.MODE_PRIVATE);
        String currGamePos = tttPrefs.getString("gamePos", "0");
        String currGameID = getIdFromPos(currGamePos);
        for (DataSnapshot child : iter) {
            if (child.getKey().equals(currGameID)) {
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for (DataSnapshot child2 : iter2) {
                    if (child2.getKey().equals("opp")) {
                        return child2.getValue().toString();
                    }
                }

            }
        }
        return "";
    }

    public String getNicknameOfDeviceUser() {
        String email = getModifiedEmail();
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("users");
        for (DataSnapshot child : iter) {
            if (child.getKey().equals(email)) {
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for (DataSnapshot child2 : iter2) {
                    if (child2.getKey().equals("nickname")) {
                        return child2.getValue().toString();
                    }
                }
            }
        }
        return getNameFromEmail(email);
    }

    public String getGameId() {
        SharedPreferences tttPrefs = context.getSharedPreferences("tictactoe", Context.MODE_PRIVATE);
        String currGamePos = tttPrefs.getString("gamePos", "0");
        String currGameID = getIdFromPos(currGamePos);
        return currGameID;
    }

    public boolean isDeviceTurn(String turnNickname) {
        String deviceNN = getNicknameOfDeviceUser();
        return deviceNN.equals(turnNickname);
    }

    public int getTTTatSpot(String mTT) {
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        SharedPreferences tttPrefs = context.getSharedPreferences("tictactoe", Context.MODE_PRIVATE);
        String currGamePos = tttPrefs.getString("gamePos", "0");
        String currGameID = getIdFromPos(currGamePos);
        for (DataSnapshot child : iter) {
            if (child.getKey().equals(currGameID)) {
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for (DataSnapshot child2 : iter2) {
                    if (child2.getKey().equals(mTT)) {
                        if (child2.getValue().toString().equals("X")) {
                            return R.drawable.ic_close_black_80dp;
                        } else {
                            return R.drawable.ic_circle_80dp;
                        }
                    }
                }

            }
        }
        return 0;
    }

    public boolean isActive() {
        boolean found = false;
        Iterable<DataSnapshot> iter = getConcentratedSnapshot("tictactoe");
        SharedPreferences tttPrefs = context.getSharedPreferences("tictactoe", Context.MODE_PRIVATE);
        String currGamePos = tttPrefs.getString("gamePos", "-1");
        String currGameID = getIdFromPos(currGamePos);
        if (!currGameID.equals("-1")) {
            for (DataSnapshot child : iter) {
                if (child.getKey().equals(currGameID)) {
                    found = true;
                }
            }
        }
        return found;
    }

    public boolean gameOver(String mostRecent) {
        String tl = "";
        if (getTTTatSpot("mTL") == R.drawable.ic_close_black_80dp) tl = "O";
        else if (getTTTatSpot("mTL") == R.drawable.ic_circle_80dp) tl = "X";
        String tc = "";
        if (getTTTatSpot("mTC") == R.drawable.ic_close_black_80dp) tc = "O";
        else if (getTTTatSpot("mTC") == R.drawable.ic_circle_80dp) tc = "X";
        String tr = "";
        if (getTTTatSpot("mTR") == R.drawable.ic_close_black_80dp) tr = "O";
        else if (getTTTatSpot("mTR") == R.drawable.ic_circle_80dp) tr = "X";
        String ml = "";
        if (getTTTatSpot("mML") == R.drawable.ic_close_black_80dp) ml = "O";
        else if (getTTTatSpot("mML") == R.drawable.ic_circle_80dp) ml = "X";
        String mc = "";
        if (getTTTatSpot("mMC") == R.drawable.ic_close_black_80dp) mc = "O";
        else if (getTTTatSpot("mMC") == R.drawable.ic_circle_80dp) mc = "X";
        String mr = "";
        if (getTTTatSpot("mMR") == R.drawable.ic_close_black_80dp) mr = "O";
        else if (getTTTatSpot("mMR") == R.drawable.ic_circle_80dp) mr = "X";
        String bl = "";
        if (getTTTatSpot("mBL") == R.drawable.ic_close_black_80dp) bl = "O";
        else if (getTTTatSpot("mTL") == R.drawable.ic_circle_80dp) bl = "X";
        String bc = "";
        if (getTTTatSpot("mBC") == R.drawable.ic_close_black_80dp) bc = "O";
        else if (getTTTatSpot("mBC") == R.drawable.ic_circle_80dp) bc = "X";
        String br = "";
        if (getTTTatSpot("mBR") == R.drawable.ic_close_black_80dp) br = "O";
        else if (getTTTatSpot("mBR") == R.drawable.ic_circle_80dp) br = "X";


        if(mostRecent == "mTL"){
            if(tl.equals(tc) && tl.equals(tr)){
                System.out.println("here1");
                return true;
            }
            else if(tl.equals(ml) && tl.equals(bl)){
                System.out.println("here2");

                return true;
            }
            else if(tl.equals(mc) && tl.equals(br)){
                System.out.println("here3");

                return true;
            }
        }
        else if(mostRecent == "mTC"){
            if(tl.equals(tc) && tl.equals(tr)){
                System.out.println("here4");

                return true;
            }
            else if(tc.equals(mc) && tc.equals(bc)){
                System.out.println("here5");

                return true;
            }
        }
        else if(mostRecent == "mTR"){
            if(tl.equals(tc) && tl.equals(tr)){
                System.out.println("here6");

                return true;
            }
            else if(tr.equals(mr) && tr.equals(br)){
                System.out.println("here7");

                return true;
            }
            else if(tr.equals(mc) && tr.equals(bl)){
                System.out.println("here8");

                return true;
            }
        }
        else if(mostRecent == "mML"){
            if(ml.equals(mc) && ml.equals(mr)){
                System.out.println(ml + " " + mc + " " + mr);

                return true;
            }
            else if(tl.equals(ml) && tl.equals(bl)){
                System.out.println("here10");

                return true;
            }
        }
        else if(mostRecent == "mMC"){
            if(tl.equals(mc) && tl.equals(br)){
                System.out.println("here11");

                return true;
            }
            else if(tc.equals(mc) && tc.equals(bc)){
                System.out.println("here12");

                return true;
            }
            else if(ml.equals(mc) && ml.equals(mr)){
                System.out.println("here13");

                return true;
            }
            else if(tr.equals(mc) && tr.equals(bl)){
                System.out.println("here14");

                return true;
            }
        }
        else if(mostRecent == "mMR"){
            if(mr.equals(mc) && mr.equals(ml)){
                System.out.println("here15");

                return true;
            }
            else if(mr.equals(tr) && mr.equals(br)){
                System.out.println("here16");

                return true;
            }
        }
        else if(mostRecent == "mBL"){
            if(bl.equals(bc) && tl.equals(br)){
                System.out.println("here17");

                return true;
            }
            else if(bl.equals(ml) && bl.equals(tl)){
                System.out.println("here18");

                return true;
            }
            else if(bl.equals(mc) && tl.equals(tr)){
                System.out.println("here19");

                return true;
            }
        }
        else if(mostRecent == "mBC"){
            if(bc.equals(tc) && bc.equals(mc)){
                System.out.println("here20");

                return true;
            }
            else if(bc.equals(bl) && bc.equals(br)){
                System.out.println("here21");

                return true;
            }
        }
        else if(mostRecent == "mBR"){
            if(br.equals(tl) && br.equals(mc)){
                System.out.println("here22");

                return true;
            }
            else if(br.equals(tr) && br.equals(mr)){
                System.out.println("here23");

                return true;
            }
            else if(br.equals(bc) && bl.equals(br)){
                System.out.println("here24");

                return true;
            }
        }


        return false;

    }

}
