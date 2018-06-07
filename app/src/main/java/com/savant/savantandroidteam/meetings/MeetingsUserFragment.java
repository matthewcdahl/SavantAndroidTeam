package com.savant.savantandroidteam.meetings;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

import static android.content.Context.MODE_PRIVATE;

public class MeetingsUserFragment extends Fragment {

    private TextView mNameText;
    private TextView mPlaceText;
    private TextView mIDText;
    private TextView mDescText;
    private boolean isDeleted;
    private String meetingID;
    private boolean dialogIsOpen;

    private MeetingsHomebase mMeetingHomebase;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMeetingRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_meetings_user, container, false);

        ((MainActivity) getActivity()).setTitle("Meeting Details");


        mNameText = (TextView) view.findViewById(R.id.tv_meeting_user_name);
        mPlaceText = (TextView) view.findViewById(R.id.tv_meeting_user_place);
        mIDText = (TextView) view.findViewById(R.id.tv_meeting_user_id);
        mDescText = (TextView) view.findViewById(R.id.tv_meeting_user_desc);
        mDescText.setMovementMethod(new ScrollingMovementMethod());
        isDeleted = false;
        meetingID = getMeetingID();
        dialogIsOpen = false;


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
            public void onCancelled(DatabaseError databaseError) {

            }
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

    private void setTextViews(){
        int pos = getPosition();
        mNameText.setText(mMeetingHomebase.getName(pos));
        mPlaceText.setText(mMeetingHomebase.getPlace(pos));
        mDescText.setText(mMeetingHomebase.getDesc(pos));
        mIDText.setText(mMeetingHomebase.getDate(pos) + " " + mMeetingHomebase.getTime(pos));
    }

    private int getPosition(){
        Bundle args = getArguments();
        return args.getInt("meetingPos");
    }
    private String getMeetingID(){
        Bundle args = getArguments();
        return args.getString("meetingID");
    }

    private void deleteMeeting(){
        isDeleted = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Meeting");
        builder.setMessage("Are you sure you want to delete this meeting?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogIsOpen = false;
                mMeetingHomebase.deleteMeeting(getPosition());
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogIsOpen = false;
            }
        });
        builder.show();
        dialogIsOpen = true;

    }

    private void editMeeting(){
        Log.d("MAIN SWITCH", "WHY YOU NO WORK");
        MeetingsHostFragment fragment = new MeetingsHostFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        saveInfo();

        ft.replace(R.id.fragment_container, fragment);
        ft.commit();

    }

    private void switchToMain(){
        Log.d("MAIN SWITCH", "WHY YOU NO WORK");
        MeetingsMainFragment fragment = new MeetingsMainFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private boolean isDeleted(DataSnapshot snapshot){
        Iterable<DataSnapshot> iter = snapshot.getChildren();
        if(iter == null) return true;
        else{
            for(DataSnapshot child: iter){
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for(DataSnapshot child2: iter2){
                    String key = child2.getKey();
                    if(key.equals("id")){
                        Log.d("TAG1", child2.getValue().toString());
                        Log.d("TAG2", meetingID);
                        if(child2.getValue().toString().equals(meetingID)){Log.d("TAG3", "YAY"); return false;}
                    }
                }
            }
        }
        return true;
    }

    private void saveInfo(){
        SharedPreferences.Editor preferences = getContext().getSharedPreferences("MeetingPrefs", MODE_PRIVATE).edit();
        preferences.putString("name", mMeetingHomebase.getName(getPosition()));
        preferences.putString("place", mMeetingHomebase.getPlace(getPosition()));
        preferences.putString("description", mMeetingHomebase.getDesc(getPosition()));
        preferences.putString("date", mMeetingHomebase.getDate(getPosition()));
        preferences.putString("time", mMeetingHomebase.getTime(getPosition()));
        preferences.putString("id", mMeetingHomebase.getId(getPosition()));
        preferences.putString("isEdited", "true");

        preferences.apply();

    }













}
