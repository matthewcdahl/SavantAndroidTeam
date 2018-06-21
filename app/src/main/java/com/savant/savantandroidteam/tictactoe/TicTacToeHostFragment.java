package com.savant.savantandroidteam.tictactoe;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.R;
import com.savant.savantandroidteam.main.MainActivity;
import com.savant.savantandroidteam.meetings.MeetingCalendarFragment;
import com.savant.savantandroidteam.meetings.MeetingTimeFragment;
import com.savant.savantandroidteam.meetings.MeetingsMainFragment;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TicTacToeHostFragment extends Fragment {

    //UI Declarations
    Spinner usersSpinner;

    //Firebase Declarations
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRootRef;
    private TicTacToeHomebase tttHB;

    //TOOLBAR Declarations
    private ActionBar masterBarHolder;
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_tictactoe_host, container, false);
        ((MainActivity) getActivity()).setTitle("Tic Tac Toe");


        //TOOLBAR Initializations
        masterBarHolder = ((MainActivity) getActivity()).getSupportActionBar();
        masterBarHolder.hide();

        toolbar = view.findViewById(R.id.toolbar_with_back);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TicTacToeMainFragment()).commit();
            }
        });



        //UI Initializations
        usersSpinner = view.findViewById(R.id.spinner_users);

        //Firebase Initializations
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference();

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tttHB = new TicTacToeHomebase(dataSnapshot);
                setupSpinner();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    //Menu for submitting the meeting
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.tictactoe_submit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.submit_game:
                submitGame();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Will upload the the meeting to firebase and do all checks to make sure everyting is filled in
     */
    private void submitGame(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        uploadGame();
        switchToUser();

    }


    /**
     * Actually connecting with firebase to submit the meeting
     */
    private void uploadGame(){

        DatabaseReference mTicTacToeRef = mRootRef.child("tictactoe");
        DatabaseReference mGameRef = mTicTacToeRef.child(tttHB.getNextGameID());
        String host = tttHB.getNicknameOfDeviceUser();

        mGameRef.child("host").setValue(tttHB.getNicknameOfDeviceUser());
        mGameRef.child("opp").setValue(usersSpinner.getSelectedItem().toString());
        mGameRef.child("finished").setValue("false");
        mGameRef.child("turn").setValue(tttHB.getNicknameOfDeviceUser());

    }

    /**
     * Switch to the user fragment
     */
    private void switchToUser(){
        TicTacToeUserFragment fragment = new TicTacToeUserFragment();
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void setupSpinner() {
        List<String> spinnerArray = getUsers();
        ArrayAdapter<String> usersSpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        usersSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        usersSpinner.setAdapter(usersSpinnerAdapter);
    }

    private List<String> getUsers(){
        return tttHB.getUsers();
    }




}
