package com.savant.savantandroidteam.meetings;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.poker.SessionItem;
import com.savant.savantandroidteam.poker.Tuple;

import java.util.ArrayList;
import java.util.List;

public class MeetingsHomebase {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    //Private Variable for inner use
    public DataSnapshot mDataSnapshot;

    public MeetingsHomebase() {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRootRef = mDatabase.getReference("meetings");
        mDataSnapshot = getSnapshot();
    }

    public MeetingsHomebase(DataSnapshot ss) {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRootRef = mDatabase.getReference("meetings");
        mDataSnapshot = ss;
    }

    public DataSnapshot getSnapshot() {
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mDataSnapshot;
    }

    public List<MeetingItem> getMeetings() {
        List<MeetingItem> finalList = new ArrayList<>();
        DataSnapshot ss = getSnapshot();
        Iterable<DataSnapshot> children = ss.getChildren();


        for (DataSnapshot child : children) {

            Iterable<DataSnapshot> metaData = child.getChildren();
            MeetingItem toAdd = new MeetingItem();
            for (DataSnapshot metaChild : metaData) {
                String key = metaChild.getKey().toString();
                if (key.equals("time")) {
                    toAdd.setTime(metaChild.getValue().toString());
                } else if (key.equals("date")) {
                     toAdd.setDate(metaChild.getValue().toString());
                } else if (key.equals("id")) {
                    toAdd.setId(metaChild.getValue().toString());
                } else if (key.equals("description")) {
                    toAdd.setDesc(metaChild.getValue().toString());
                } else if (key.equals("name")) {
                    toAdd.setName(metaChild.getValue().toString());
                } else if (key.equals("place")) {
                    toAdd.setPlace(metaChild.getValue().toString());
                }
            }
            finalList.add(toAdd);
        }


        return finalList;
    }

    public MeetingItem getClickedSession(int position) {
        List<MeetingItem> arr = getMeetings();
        return arr.get(position);
    }

    private void printArrayList(List<SessionItem> arr) {
        for (int i = 0; i < arr.size(); i++) {
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

            for (int j = 0; j < tupArr.size(); j++) {
                Tuple currTup = tupArr.get(j);
                String Uname = currTup.getName();
                String diff = currTup.getResponse();
                Log.d("R Name", Uname);
                Log.d("R diff", diff);


            }

            Log.d("**********************", "*********************");
        }
    }

    public int getNumberOfSessions() {
        return getMeetings().size();
    }


    //Get all variables associated with the meetings.
    public String getName(int pos) {
        List<MeetingItem> list = getMeetings();
        return list.get(pos).getName();
    }
    public String getDate(int pos) {
        List<MeetingItem> list = getMeetings();
        return list.get(pos).getDate();
    }
    public String getDesc(int pos) {
        List<MeetingItem> list = getMeetings();
        return list.get(pos).getDesc();
    }
    public String getTime(int pos) {
        List<MeetingItem> list = getMeetings();
        return list.get(pos).getTime();
    }
    public String getPlace(int pos) {
        List<MeetingItem> list = getMeetings();
        return list.get(pos).getPlace();
    }

    public String getId(int pos) {
        List<MeetingItem> list = getMeetings();
        return list.get(pos).getId();
    }

    public void deleteMeeting(int pos) {
        List<MeetingItem> list = getMeetings();
        String toRemove = list.get(pos).getDate() +" "+ list.get(pos).getTime() +" "+ list.get(pos).getId();
        mRootRef.child(toRemove).removeValue();
    }

}
