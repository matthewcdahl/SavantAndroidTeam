package com.savant.savantandroidteam.poker;

/*

//Firebase EXAMPLE
        mDatabase = FirebaseDatabase.getInstance();
        mPokerDatabase = mDatabase.getReference("poker");
        DatabaseReference child10 = mPokerDatabase.child("1");
        DatabaseReference child11 = child10.child("Responses");
        DatabaseReference child12 = child11.child("Matt");
        DatabaseReference child13 = child11.child("Tyler");
        child10.setValue("Add Button");
        child12.setValue("5");
        child13.setValue("7");
        child10.removeValue();


 */

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.appcompat.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

import java.util.zip.Inflater;

public class PokerHostFragment extends Fragment {

    //UI
    private Button mShowResponses;
    private Button mSubmitName;
    private Button mSubmitDiff;
    private TextView mResponsesText;
    private EditText mName;
    private EditText mDiff;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mPokerDatabase;
    private DatabaseReference mCurrentSession;
    private DatabaseReference mResponsesRef;
    private DatabaseReference mNameRef;

    //Logic
    private boolean dialogIsOpen;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_poker_host_new, container, false);
        ((MainActivity) getActivity()).setTitle("Create Session");




        //UI
        mShowResponses = (Button) view.findViewById(R.id.poker_show_response_btn);
        mSubmitName = (Button) view.findViewById(R.id.submit_activity_name_host);
        mSubmitDiff = (Button) view.findViewById(R.id.submit_activity_diff_host);
        mResponsesText = (TextView) view.findViewById(R.id.poker_response_number);
        dialogIsOpen = false;

        mName = (EditText) view.findViewById(R.id.et_sprint_name);
        mName.setText(getSessionNAME());

        mDiff = (EditText) view.findViewById(R.id.et_difficulty);
        mDiff.setText(getSessionDIFF());

        //Firebase
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


    private void submitName(){
        String name = mName.getText().toString();
        mCurrentSession.child("name").setValue(name);
    }

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

    private String getSessionID(){
        Bundle arguments = getArguments();
        String session = arguments.getString("ID");
        return session;
    }

    private String getSessionNAME(){
        Bundle arguments = getArguments();
        String name = arguments.getString("NAME");
        return name;
    }

    private String getSessionDIFF(){
        Bundle arguments = getArguments();
        String diff = arguments.getString("DIFF");
        return diff;
    }

    private void updateResponseTag(DataSnapshot ds){
        Iterable<DataSnapshot> snapshotIterable = ds.getChildren();
        int count = 0;
        for(DataSnapshot h: snapshotIterable){
            count++;
        }
        mResponsesText.setText("Responses: " + count);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return true;
    }

    








}
