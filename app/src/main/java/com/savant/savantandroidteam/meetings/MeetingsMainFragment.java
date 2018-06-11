package com.savant.savantandroidteam.meetings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

import java.util.ArrayList;
import java.util.List;

public class MeetingsMainFragment extends Fragment {

    FirebaseDatabase mDatabase;
    DatabaseReference mMeetingsRef;
    private FirebaseAuth mAuth;



    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<MeetingItem> meetingItems;
    private MeetingsHomebase mMeetingsHomebase;

    private TextView mNoCurrentText;
    private TextView mClickPlusText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_meetings_main, container, false);

        ((MainActivity)getActivity()).setUpToolbar("Meetings");


        mDatabase = FirebaseDatabase.getInstance();
        mMeetingsRef = mDatabase.getReference("meetings");
        meetingItems = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mNoCurrentText = (TextView) view.findViewById(R.id.no_current_sessions);
        mClickPlusText = (TextView) view.findViewById(R.id.press_plus);



        mMeetingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMeetingsHomebase = new MeetingsHomebase(dataSnapshot);
                setStartText();
                addMeetingsToView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.meetings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_meeting:
                addMeeting();

        }

        return super.onOptionsItemSelected(item);
    }

    //DataSnapshot { key = 1, value = {host=Matthew, name=m, responses={Matthew=2}} }
    private void addMeetingsToView(){

       meetingItems = mMeetingsHomebase.getMeetings();

       adapter = new MeetingsAdapter(meetingItems, getContext());
       mRecyclerView.setAdapter(adapter);

    }

    private void addMeeting(){

        startMeeting();
    }


    private void startMeeting(/*String id, String name*/){

        SharedPreferences.Editor edit = getContext().getSharedPreferences("MeetingPrefsExtra", Context.MODE_PRIVATE).edit();
        edit.putString("isEdited", "false");
        edit.apply();

        MeetingsHostFragment fragment = new MeetingsHostFragment();
        Bundle arguments = new Bundle();
        //arguments.putString("ID", id);
        //arguments.putString("NAME", name);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null).commit();
    }

    private void setStartText(){
        if(mMeetingsHomebase.getNumberOfSessions() == 0){
            mNoCurrentText.setVisibility(View.VISIBLE);
            mClickPlusText.setVisibility(View.VISIBLE);
        }
        else{
            mNoCurrentText.setVisibility(View.GONE);
            mClickPlusText.setVisibility(View.GONE);
        }
    }




}
