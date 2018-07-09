package com.savant.savantandroidteam.tictactoe;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import com.google.firebase.auth.FirebaseAuth;
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
    private int nextGameID;
    private DataSnapshot mDataSnapshot;
    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataSnapshot = dataSnapshot;
                setupSpinner();
                setupNextGameID();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //Menu for submitting the meeting
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tictactoe_submit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.submit_game:
                submitGame();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Will upload the the meeting to firebase and do all checks to make sure everyting is filled in
     */
    private void submitGame() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        uploadGame();
        switchToUser();

    }


    /**
     * Actually connecting with firebase to submit the meeting
     */
    private void uploadGame() {

        DatabaseReference tictactoeRef = mRootRef.child("tictactoe");
        DatabaseReference currGameRef = tictactoeRef.child(Integer.toString(nextGameID));

        String host = getHostNickname();
        String opp = usersSpinner.getSelectedItem().toString();
        String turn = host;
        String winner = "";
        String state = "";
        String id = Integer.toString(nextGameID);

        currGameRef.child("host").setValue(host);
        currGameRef.child("opp").setValue(opp);
        currGameRef.child("turn").setValue(turn);
        currGameRef.child("winner").setValue(winner);
        currGameRef.child("state").setValue(state);
        currGameRef.child("id").setValue(id);



    }

    /**
     * Switch to the user fragment
     */
    private void switchToUser() {
        SharedPreferences.Editor tttPrefs = getContext().getSharedPreferences("tictactoe", MODE_PRIVATE).edit();
        tttPrefs.putString("from", "host");
        tttPrefs.putString("id", Integer.toString(nextGameID));
        tttPrefs.commit();

        TicTacToeUserFragment fragment = new TicTacToeUserFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void setupSpinner() {
        List<String> spinnerArray = getUsersNicknames();
        ArrayAdapter<String> usersSpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        usersSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        usersSpinner.setAdapter(usersSpinnerAdapter);
    }

    private List<String> getUsersNicknames() {
        DataSnapshot ss = mDataSnapshot;
        List<String> finalArrList = new ArrayList<>();
        Iterable<DataSnapshot> iter = ss.getChildren();
        for (DataSnapshot child : iter) {
            if (child.getKey().equals("users")) {
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for (DataSnapshot child2 : iter2) {
                    Iterable<DataSnapshot> iter3 = child2.getChildren();
                    boolean hasNickname = false;
                    for (DataSnapshot child3 : iter3) {
                        if (child3.getKey().equals("nickname")) {
                            finalArrList.add(child3.getValue().toString());
                            hasNickname = true;
                        }
                    }
                    if (!hasNickname) finalArrList.add(getUserFirstName(child2.getChildren()));
                }
            }
        }


        return finalArrList;
    }

    private String getUserFirstName(Iterable<DataSnapshot> iter) {
        for (DataSnapshot child : iter) {
            if (child.getKey().equals("name")) {
                String fullName = child.getValue().toString();
                return fullName.substring(0, fullName.indexOf(" "));
            }
        }
        return "ERROR";
    }

    private void setupNextGameID() {
        List<Integer> allSessions = new ArrayList<>();
        Iterable<DataSnapshot> iter = mDataSnapshot.getChildren();
        for (DataSnapshot child : iter) {
            if (child.getKey().equals("tictactoe")) {
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for (DataSnapshot child2 : iter2) {
                    allSessions.add(Integer.parseInt(child2.getKey()));
                }
            }
        }

        nextGameID = getHighestValue(allSessions) + 1;

    }

    private int getHighestValue(List<Integer> list) {
        int highest = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > highest) highest = list.get(i);
        }
        return highest;
    }

    private String getHostNickname() {
        DataSnapshot ss = mDataSnapshot;
        String email = getHostModifiedEmail();
        Iterable<DataSnapshot> iter = ss.getChildren();
        for (DataSnapshot child : iter) {
            if (child.getKey().equals("users")) {
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for (DataSnapshot child2 : iter2) {
                    if(child2.getKey().equals(email)) {
                        Iterable<DataSnapshot> iter3 = child2.getChildren();
                        for (DataSnapshot child3 : iter3) {
                            if (child3.getKey().equals("nickname")) {
                                return child3.getValue().toString();
                            }
                        }
                    }
                }
            }
        }
        return getHostFirstName();
    }

    private String getHostModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
    }

    private String getHostFirstName(){
        String email = mAuth.getCurrentUser().getEmail();
        String rtn = (email.substring(0, email.indexOf('.')));
        if(rtn.length() > 1) {
            return rtn.substring(0, 1).toUpperCase() + rtn.substring(1);
        }
        else return rtn.toUpperCase();
    }


}
