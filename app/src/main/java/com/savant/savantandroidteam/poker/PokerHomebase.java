package com.savant.savantandroidteam.poker;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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

public class PokerHomebase {

    //Firebase Declarations
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;


    //Private Variable for inner use
    public DataSnapshot mDataSnapshot;
    private ArrayList<Tuple> usersPics = new ArrayList<>();
    private Context context;


    public PokerHomebase(DataSnapshot ss, Context context){ // For loading purposes this will help speed things along if the snapshot is available
        //Firebase Initializations
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mRootRef = mDatabase.getReference("poker");
        mDataSnapshot = ss;
        mUsersRef = mDatabase.getReference("users");

        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iter = dataSnapshot.getChildren();
                for(DataSnapshot child: iter){
                    String name;
                    String id = "15";
                    name = extractName(child.getKey());
                    Iterable<DataSnapshot> iter2 = child.getChildren();
                    for(DataSnapshot child2: iter2){
                        if(child2.getKey().equals("picture")){
                            id = child2.getValue().toString();
                        }
                    }
                    usersPics.add(new Tuple(name, id));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    /**
     * This will update mDataSnapshot with the most current one
     */
    public DataSnapshot getSnapshot(){
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return mDataSnapshot;
    }



    /**
     *
     * @return This is the home for reading from firebase. It takes the snapshot and
     * puts it into a list of SessionItems
     */
    public List<SessionItem> getSessions(){
        List<SessionItem> finalList = new ArrayList<>();
        DataSnapshot ss = getSnapshot();
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

    public SessionItem getClickedSession(int position){

        List<SessionItem> arr = getSessions();

        return arr.get(position);
    }

    /**
     * //For debugging purposes - do not use in distribution
     * @param arr
     */
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

    /**
     *
     * @return then number of current sessions
     */
    public int getNumberOfSessions(){
        return getSessions().size();
    }

    /**
     *
     * @param position of the session
     * @return true if the phone user is the host of the session
     */
    public boolean isHostOfSession(int position){

        String host = getSessions().get(position).getHost();
        String emailHolder = getUserModifiedEmail();
        /*String user = emailHolder.substring(0, emailHolder.indexOf('.'));
        String userName = user.substring(0, 1).toUpperCase() + user.substring(1);*/
        return emailHolder.equals(host);
    }


    /**
     *
     * @param position of the session
     * @return true if the host has clicked show results
     */
    public boolean isRevealed(int position){
        if(position == -1) return false;

        String revealed = getSessions().get(position).getRevealed();
        if(revealed.equals("false")) return false;
        else return true;
    }

    /**
     *
     * @return the number above the highest numbered session
     */
    public String getLastSession(){

        List<SessionItem> list = getSessions();
        int size = list.size();
        if(size == 0) return "0";
        else return list.get(list.size()-1).getId();
    }


    /**
     *
     * @param pos of the session
     * @return the name of the session in position pos
     */
    public String getName(int pos){

        List<SessionItem> list = getSessions();
        return list.get(pos).getName();
    }

    /**
     *
     * @param pos
     * @return the users response or if none the empty string
     */
    public String getResponse(int pos){
        List<SessionItem> list = getSessions();
        ArrayList<Tuple> res = list.get(pos).getResponses();
        for(int i = 0; i<res.size(); i++){
            if(res.get(i).getName().equals(getUserModifiedEmail())) return res.get(i).getResponse();
        }
        return "";
    }

    /**
     *
     * @param id of session to remove
     */
    public void removeSession(String id){
        mRootRef.child(id).removeValue();
    }


    /*
    *
    * Will search the list of sessions for the id of the pos given
    */
    public int getIdPos(int pos){

        List<SessionItem> list = getSessions();

        return Integer.parseInt(list.get(pos).getId());
    }


    /**
     *
     * @param email to extrace name from
     * @return the first name of the user
     */
    private String extractName(String email){
        String fn = email.substring(0, email.indexOf(","));
        fn = fn.substring(0,1).toUpperCase() + fn.substring(1);
        return fn;
    }

    /**
     *
     * @return users email with '.' replaced by ','
     */
    private String getUserModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
    }

    /**
     * will remove all sessions if they are closed
     */
    public void deleteAllClosedSessions(){
        boolean deletedAtLeastOneSession = false;
        List<SessionItem> list = getSessions();
        for(SessionItem item: list){
            if(item.getRevealed().equals("true")){
                deletedAtLeastOneSession = true;
                String itemID = item.getId();
                DatabaseReference itemRef = mRootRef.child(itemID);
                itemRef.removeValue();
            }
        }
        if(deletedAtLeastOneSession) Toast.makeText(context, "All Closed Sessions Deleted", Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, "No Closed Sessions to Delete", Toast.LENGTH_SHORT).show();
    }

}
