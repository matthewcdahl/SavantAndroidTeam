package com.savant.savantandroidteam.meetings;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.main.MainActivity;
import com.savant.savantandroidteam.R;

import static android.content.Context.MODE_PRIVATE;

public class MeetingsUserFragment extends Fragment {

    //UI and Internal Declarations
    private TextView mNameText;
    private TextView mPlaceText;
    private TextView mIDText;
    private TextView mDescText;
    private boolean isDeleted;
    private String meetingID;

    //Firebase Declarations
    private MeetingsHomebase mMeetingHomebase;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMeetingRef;

    //TOOLBAR
    private ActionBar masterBarHolder;
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_meetings_user, container, false);

        ((MainActivity) getActivity()).setTitle("Meeting Details");

        //TOOLBAR
        masterBarHolder = ((MainActivity) getActivity()).getSupportActionBar();
        masterBarHolder.hide();

        toolbar = view.findViewById(R.id.toolbar_with_back);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MeetingsMainFragment()).commit();
            }
        });

        //Abstract toolbar fragment


        //UI Initializations
        mNameText = (TextView) view.findViewById(R.id.tv_meeting_user_name);
        mPlaceText = (TextView) view.findViewById(R.id.tv_meeting_user_place);
        mIDText = (TextView) view.findViewById(R.id.tv_meeting_user_id);
        mDescText = (TextView) view.findViewById(R.id.tv_meeting_user_desc);
        mDescText.setMovementMethod(new ScrollingMovementMethod());
        isDeleted = false;
        meetingID = getMeetingID();


        //Firebase Initializations
        mDatabase = FirebaseDatabase.getInstance();
        mMeetingRef = mDatabase.getReference("meetings");
        mMeetingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (MeetingsUserFragment.this.isVisible()) {
                    mMeetingHomebase = new MeetingsHomebase(dataSnapshot);
                    if (isDeleted(dataSnapshot)) {
                        switchToMain();
                    } else {
                        if (!isDeleted) setTextViews();
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.meetings_menu_user, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.delete_meeting_menu_item) deleteMeeting();
        else editMeeting();


        return super.onOptionsItemSelected(item);
    }

    /**
     * set the text views for the meeting information
     */
    private void setTextViews(){
        int pos = getPosition();
        mNameText.setText(mMeetingHomebase.getName(pos));
        mPlaceText.setText(mMeetingHomebase.getPlace(pos));
        mDescText.setText(mMeetingHomebase.getDesc(pos));
        mIDText.setText(mMeetingHomebase.getDate(pos) + " " + mMeetingHomebase.getTime(pos));
    }

    /**
     *
     * @return current position of the meeting
     */
    private int getPosition(){
        Bundle args = getArguments();
        return args.getInt("meetingPos");
    }

    /**
     *
     * @return the randomized id of the meeting
     */
    private String getMeetingID(){
        Bundle args = getArguments();
        return args.getString("meetingID");
    }

    /**
     * delete the meeting from firebase
     */
    private void deleteMeeting(){
        mMeetingHomebase.deleteMeeting(getPosition());
    }

    /**
     * switch to the host framgent and save the info that needs to be carried over
     */
    private void editMeeting(){
        MeetingsHostFragment fragment = new MeetingsHostFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        saveInfo();

        ft.replace(R.id.fragment_container, fragment);
        ft.commit();

    }

    /**
     * Swich to meetings main fragment
     */
    private void switchToMain(){
        MeetingsMainFragment fragment = new MeetingsMainFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    /**
     *
     * @param snapshot
     * @return true if the current meeting exists
     */
    private boolean isDeleted(DataSnapshot snapshot){
        Iterable<DataSnapshot> iter = snapshot.getChildren();
        if(iter == null) return true;
        else{
            for(DataSnapshot child: iter){
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for(DataSnapshot child2: iter2){
                    String key = child2.getKey();
                    if(key.equals("id")){

                        if(child2.getValue().toString().equals(meetingID)) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * will save all of the meeting info for edits in shared prefrences
     */
    private void saveInfo(){
        SharedPreferences.Editor preferences = getContext().getSharedPreferences("MeetingPrefs", MODE_PRIVATE).edit();
        preferences.putString("name", mMeetingHomebase.getName(getPosition()));
        preferences.putString("place", mMeetingHomebase.getPlace(getPosition()));
        preferences.putString("description", mMeetingHomebase.getDesc(getPosition()));
        preferences.putString("date", mMeetingHomebase.getDate(getPosition()));
        preferences.putString("time", mMeetingHomebase.getTime(getPosition()));
        preferences.putString("id", mMeetingHomebase.getId(getPosition()));
        SharedPreferences.Editor prefs = getContext().getSharedPreferences("MeetingPrefsExtra", MODE_PRIVATE).edit();
        prefs.putString("isEdited", "true");
        preferences.putString("isEdited", "true");

        preferences.apply();
        prefs.apply();

    }













}
