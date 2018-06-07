package com.savant.savantandroidteam.meetings;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

import static android.content.Context.MODE_PRIVATE;

public class MeetingsHostFragment extends Fragment {

    //UI
    private EditText mNameET;
    private EditText mPlaceET;
    private Button mDateBtn;
    private Button mTimeBtn;
    private EditText mDescriptionET;
    private boolean isEdited;
    private String idIfEdited;

    //Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mMeetingsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_meetings_host, container, false);

        ((MainActivity) getActivity()).setTitle("Create Meeting");



        //UI
        mNameET = (EditText) view.findViewById(R.id.et_meetings_host_name);
        mPlaceET = (EditText) view.findViewById(R.id.et_meetings_host_place);
        mDateBtn = (Button) view.findViewById(R.id.btn_meetings_host_date);
        mTimeBtn = (Button) view.findViewById(R.id.btn_meetings_host_time);
        mDescriptionET = (EditText) view.findViewById(R.id.et_meetings_host_desc);
        mDescriptionET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mDescriptionET.setRawInputType(InputType.TYPE_CLASS_TEXT);
        isEdited = false;
        idIfEdited = "00";

        //Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference();
        mMeetingsRef = mRootRef.child("meetings");

        mDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, new MeetingCalendarFragment()).commit();
            }
        });
        mTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfo();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, new MeetingTimeFragment()).commit();
            }
        });


        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        loadInfo();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.meetings_submit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.submit_meeting:
                submitMeeting();
        }

        return super.onOptionsItemSelected(item);
    }


    private void submitMeeting(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        if(mTimeBtn.getText().toString().equals("Select Time") || mDateBtn.getText().toString().equals("Select Date") ||
                mNameET.getText().toString().equals("") ||
                mPlaceET.getText().toString().equals("") ||
                mDescriptionET.getText().toString().equals("")){
            //Alert Dialog
            AlertDialog ad = new AlertDialog.Builder(getContext())
                    .create();
            ad.setCancelable(false);
            ad.setTitle("Empty Fields!");
            ad.setMessage("Must fill in all fields before creating meeting.");
            ad.setButton(Dialog.BUTTON_NEUTRAL,"Ok", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ad.show();

        }
        else {
            uploadMeeting();
            clearInfo("MeetingPrefs");
            clearInfo("MeetingPrefsExtra");
            switchToMain();
        }
    }


    private void uploadMeeting(){
        String id = "";
        boolean isEdit = false;
        SharedPreferences prefs1 = getContext().getSharedPreferences("MeetingPrefsExtra", MODE_PRIVATE);
        if(prefs1.getString("isEdited", "false").equals("true")) isEdit = true;
        else isEdit = false;
        if(isEdit){
            SharedPreferences prefs = getContext().getSharedPreferences("MeetingPrefsExtra", MODE_PRIVATE);
            id = prefs.getString("id", "OOPS");
            String formerDate = prefs.getString("date", "ERROR!!");
            String formerTime = prefs.getString("time", "ERROR TIME!!");
            Log.d("FORMER DATE", formerDate);
            Log.d("FORMER TIME", formerTime);
            mMeetingsRef.child(formerDate + " " + formerTime + " " + prefs.getString("id", "")).removeValue();

        }
        else {
            id = Integer.toString((int) (Math.random() * 5000 + 1)); //Random number so if there are 2 meetings at same time one will not be overwritten
        }
        String name = mNameET.getText().toString();
        String place = mPlaceET.getText().toString();
        String date = mDateBtn.getText().toString();
        String time = mTimeBtn.getText().toString();
        String description = mDescriptionET.getText().toString();
        DatabaseReference currMeetingRef = mMeetingsRef.child(date + " " + time + " " + id);
        currMeetingRef.child("name").setValue(name);
        currMeetingRef.child("place").setValue(place);
        currMeetingRef.child("date").setValue(date);
        currMeetingRef.child("time").setValue(time);
        currMeetingRef.child("id").setValue(id);
        currMeetingRef.child("description").setValue(description);

    }

    private void switchToMain(){
        MeetingsMainFragment fragment = new MeetingsMainFragment();
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void saveInfo(){
        SharedPreferences.Editor preferences = getContext().getSharedPreferences("MeetingPrefs", MODE_PRIVATE).edit();
        preferences.putString("name", mNameET.getText().toString());
        preferences.putString("place", mPlaceET.getText().toString());
        preferences.putString("description", mDescriptionET.getText().toString());
        preferences.putString("date", mDateBtn.getText().toString());
        preferences.putString("time", mTimeBtn.getText().toString());
        preferences.putString("isEdited", "false");

        preferences.apply();

    }
    //TODO ADD COMMENTS
    private void loadInfo(){
        SharedPreferences preferences = getContext().getSharedPreferences("MeetingPrefs", MODE_PRIVATE);

        String edit = preferences.getString("isEdited", "false");
        if(edit.equals("true")){
            isEdited = true;
            idIfEdited = preferences.getString("id", "000");
            SharedPreferences.Editor prefsExtra = getContext().getSharedPreferences("MeetingPrefsExtra", MODE_PRIVATE).edit();
            prefsExtra.putString("id", preferences.getString("id", ""));
            prefsExtra.putString("time", preferences.getString("time", ""));
            prefsExtra.putString("date", preferences.getString("date", ""));
            prefsExtra.apply();
        }
        else isEdited = false;

        mNameET.setText(preferences.getString("name", ""));
        mPlaceET.setText(preferences.getString("place", ""));
        mDescriptionET.setText(preferences.getString("description", ""));
        mDateBtn.setText(preferences.getString("date", "Select Date"));
        mTimeBtn.setText(preferences.getString("time", "Select Time"));
        String holder = preferences.getString("isEdited", "false");
        clearInfo("MeetingPrefs");

    }

    private void clearInfo(String prefName){
        getContext().getSharedPreferences(prefName, 0).edit().clear().commit();
    }










}
