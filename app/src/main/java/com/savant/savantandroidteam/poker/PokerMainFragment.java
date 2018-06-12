package com.savant.savantandroidteam.poker;

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

public class PokerMainFragment extends Fragment {

    //Firebase
    FirebaseDatabase mDatabase;
    DatabaseReference mPoker;
    private FirebaseAuth mAuth;
    private PokerHomebase mPokerHomebase;


    //UI
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<SessionItem> sessionItems;
    private TextView mNoCurrentText;
    private TextView mClickPlusText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_poker_main, container, false);
        ((MainActivity)getActivity()).setUpToolbar("Poker Sessions");



        //Firebase
        mDatabase = FirebaseDatabase.getInstance();
        mPoker = mDatabase.getReference("poker");
        sessionItems = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        //UI
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNoCurrentText = (TextView) view.findViewById(R.id.no_current_sessions);
        mClickPlusText = (TextView) view.findViewById(R.id.press_plus);


        //Will listen for sessions on firebase
        mPoker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPokerHomebase = new PokerHomebase(dataSnapshot);
                addSessionsToView();
                setStartText();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });



        return view;
    }

    //Menu with add button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.poker_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.add_poker:
                addSession();

        }

        return super.onOptionsItemSelected(item);
    }

    //Recycler view adapting
    private void addSessionsToView(){
        sessionItems = mPokerHomebase.getSessions();
        adapter = new PokerAdapter(sessionItems, getContext());
        mRecyclerView.setAdapter(adapter);
    }

    private void addSession(){
        if(mPokerHomebase == null){ // need to wait for loading to prevent data from not being initialized
            Toast.makeText(getContext(), "Wait for connection...", Toast.LENGTH_LONG).show();
        }
        else {
            String numOfSessions = getLastSessionID();
            String numOfSessionsFinal = addOneToString(numOfSessions);
            DatabaseReference ref = mPoker.child(numOfSessionsFinal);

            //Set Session ID
            String newSessionName = "Session " + numOfSessionsFinal;
            ref.setValue(newSessionName);

            //Set Session name
            ref.child("name").setValue(newSessionName);

            //Set Host of session
            String userEmail = mAuth.getCurrentUser().getEmail();
            for (int i = 0; i < userEmail.length(); i++) {
                if (userEmail.charAt(i) == '.') {
                    userEmail = userEmail.substring(0, i);
                }
            }
            userEmail = userEmail.substring(0, 1).toUpperCase() + userEmail.substring(1);
            ref.child("host").setValue(userEmail);

            //Set is Revealed
            ref.child("revealed").setValue("false");

            startSession(numOfSessionsFinal, newSessionName);
        }

    }

    //Switch to the host fragment with with name and id bundled
    private void startSession(String id, String name){
        PokerHostFragment fragment = new PokerHostFragment();
        Bundle arguments = new Bundle();
        arguments.putString("ID", id);
        arguments.putString("NAME", name);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null).commit();
    }

    private String getLastSessionID(){
        /*if(rawNumberOfSessions == null) return 0;
        else return Integer.parseInt(rawNumberOfSessions);*/
        return mPokerHomebase.getLastSession();
    }

    private String addOneToString(String str){
        int i = Integer.parseInt(str);
        i++;
        return Integer.toString(i);
    }

    private void setStartText(){
        if(mPokerHomebase.getNumberOfSessions() == 0){
            mNoCurrentText.setVisibility(View.VISIBLE);
            mClickPlusText.setVisibility(View.VISIBLE);
        }
        else{
            mNoCurrentText.setVisibility(View.GONE);
            mClickPlusText.setVisibility(View.GONE);
        }
    }

}
