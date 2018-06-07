package com.savant.savantandroidteam.poker;


import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

public class PokerUserFragment extends Fragment {

    //UI

    private Button mSubmitDiff;
    private EditText mDiff;
    private TextView mWaitingText;
    private TextView mSessionName;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mPokerDatabase;
    private DatabaseReference mCurrentSession;
    private DatabaseReference mNameRef;
    private DatabaseReference mRevealedRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_poker_user, container, false);

        ((MainActivity) getActivity()).setTitle("Submit Response");


        //UI
        mSubmitDiff = (Button) view.findViewById(R.id.submit_activity_diff_user);

        mDiff = (EditText) view.findViewById(R.id.et_difficulty_user);
        mDiff.setText(getSessionDIFF());

        mWaitingText = (TextView) view.findViewById(R.id.tv_waiting);
        waitingText();

        mSessionName = (TextView) view.findViewById(R.id.tv_session_name_user);
        mSessionName.setText(getSessionNAME());

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mPokerDatabase = mDatabase.getReference("poker");
        mCurrentSession = mPokerDatabase.child(getSessionID());
        mRevealedRef = mCurrentSession.child("revealed");
        mNameRef = mCurrentSession.child("name");


        //Will submit the user answer when keyboard ime Action is pressed
        mDiff.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                    submitDiff();
                    mWaitingText.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        //Firebase Reference for when the host shows responses
        mRevealedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && dataSnapshot.getValue().toString().equals("true")){
                    String id = getSessionID();
                    PokerResultsFragment fragment = new PokerResultsFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString("ID", id);
                    fragment.setArguments(arguments);
                    FragmentTransaction ft;
                    try{
                        ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, fragment);
                        ft.commit();
                    } catch (Exception e){}


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        //Firebase reference for showing the name of the session real time.
        mNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    mSessionName.setText(dataSnapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // When the submit button is clicked
        mSubmitDiff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDiff();
                mWaitingText.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });


        return view;
    }



    //Pushes the users name and response to firebase in the form of Key: First name, Value: Response
    private void submitDiff(){
        String diff = mDiff.getText().toString();
        if(diff.isEmpty()) diff = "0";
        String userEmail = mAuth.getCurrentUser().getEmail();
        for(int i = 0; i<userEmail.length(); i++){
            if(userEmail.charAt(i) == '.'){
                userEmail = userEmail.substring(0, i);
            }
        }
        userEmail = userEmail.substring(0, 1).toUpperCase() + userEmail.substring(1);
        DatabaseReference userRef = mCurrentSession.child("responses");
        userRef.child(userEmail).setValue(diff);

    }


    //Returns the unique id of the session from the bundled arguments from the main fragment
    private String getSessionID(){
        Bundle arguments = getArguments();
        String session = arguments.getString("ID");
        return session;

    }

    //Gets the current response from the user
    private String getSessionDIFF(){
        Bundle arguments = getArguments();
        String diff = arguments.getString("DIFF");
        return diff;

    }

    //If the user has given a response they will be told to wait for host
    private void waitingText(){
        if(getSessionDIFF() == "") mWaitingText.setVisibility(View.GONE);
        else mWaitingText.setVisibility(View.VISIBLE);

    }

    //Returns the name of the session from the main fragment bundle
    private String getSessionNAME(){
        Bundle arguments = getArguments();
        String name = arguments.getString("NAME");
        return name;
    }






}
