package com.savant.savantandroidteam.poker;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;
import com.savant.savantandroidteam.profile.ProfileFragment;

import java.util.zip.Inflater;

/*
    This is the fragment for when a user creates a poker session
 */

public class PokerHostFragment extends Fragment {

    //UI Declarations
    private Button mShowResponses;
    private Button mSubmitName;
    private Button mSubmitDiff;
    private TextView mResponsesText;
    private EditText mName;
    private EditText mDiff;


    //Firebase Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mPokerDatabase;
    private DatabaseReference mCurrentSession;
    private DatabaseReference mResponsesRef;
    private DatabaseReference mNameRef;

    //Logic Declarations
    private boolean dialogIsOpen;

    //TOOLBAR
    private ActionBar masterBarHolder;
    Toolbar toolbar;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_poker_host_new, container, false);
        ((MainActivity) getActivity()).setTitle("Create Session");


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
                        .replace(R.id.fragment_container, new PokerMainFragment()).commit();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });



        //UI Initializations
        mShowResponses = (Button) view.findViewById(R.id.poker_show_response_btn);
        mSubmitName = (Button) view.findViewById(R.id.submit_activity_name_host);
        mSubmitDiff = (Button) view.findViewById(R.id.submit_activity_diff_host);
        mResponsesText = (TextView) view.findViewById(R.id.poker_response_number);
        dialogIsOpen = false;

        mName = (EditText) view.findViewById(R.id.et_sprint_name);
        mName.setText(getSessionNAME());

        mDiff = (EditText) view.findViewById(R.id.et_difficulty);
        mDiff.setText(getSessionDIFF());

        //Firebase Initializations
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mPokerDatabase = mDatabase.getReference("poker");
        mCurrentSession = mPokerDatabase.child(getSessionID());
        mResponsesRef = mCurrentSession.child("responses");
        mNameRef = mCurrentSession.child("name");

        mName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                    submitName();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        mDiff.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                    submitDiff();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    return true;
                }
                else {
                    return false;
                }
            }
        });



        mSubmitName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitName();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });
        mSubmitDiff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDiff();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });
        mShowResponses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitName();
                submitDiff();
                if(!dialogIsOpen) {
                    dialogIsOpen = true;
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setTitle("Show Responses");
                    builder.setMessage("This will end the session and will not allow any more responses.\n\nWould you like to continue?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogIsOpen = false;
                            showResponses();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogIsOpen = false;
                        }
                    });
                    builder.show();

                }

            }
        });

        mResponsesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateResponseTag(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    /**
     * Clear focus from the edit text
     * TODO test if it actually does anything
     */
    @Override
    public void onPause() {
        super.onPause();
        mDiff.clearFocus();
    }

    /**
     * upload name of session to firebase
     */
    private void submitName(){
        String name = mName.getText().toString();
        mCurrentSession.child("name").setValue(name);
    }

    /**
     * upload response to firebase
     */
    private void submitDiff(){
        String diff = mDiff.getText().toString();
        if(diff.isEmpty()) diff = "0";
        String userEmail = getModifiedEmail();
        DatabaseReference userRef = mCurrentSession.child("responses");
        userRef.child(userEmail).setValue(diff);
        mDiff.clearFocus();

    }

    /**
     * Switch to results fragment
     */
    private void showResponses(){
        mCurrentSession.child("revealed").setValue("true");

        String id = getSessionID();
        String name = getSessionNAME();
        PokerResultsFragment fragment = new PokerResultsFragment();
        Bundle arguments = new Bundle();
        arguments.putString("ID", id);
        arguments.putString("NAME", name);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    /*
    * Get id of session from bundle
    */

    private String getSessionID(){
        Bundle arguments = getArguments();
        String session = arguments.getString("ID");
        return session;
    }

    /*
     * Get name of session from bundle
     */
    private String getSessionNAME(){
        Bundle arguments = getArguments();
        String name = arguments.getString("NAME");
        return name;
    }

    /*
     * Get response of user for session from bundle
     */
    private String getSessionDIFF(){
        Bundle arguments = getArguments();
        String diff = arguments.getString("DIFF");
        return diff;
    }


    /**
     *
     * @param ds current session snapshot
     * counts the number of current sessions
     */
    private void updateResponseTag(DataSnapshot ds){
        Iterable<DataSnapshot> snapshotIterable = ds.getChildren();
        int count = 0;
        for(DataSnapshot h: snapshotIterable){
            count++;
        }
        mResponsesText.setText("Responses: " + count);
    }

    /**
     * Menu to go back to previous page
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return true;
    }

    /**
     *
     * @return current user email with '.' swapped with ','
     */
    private String getModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
    }

    








}
