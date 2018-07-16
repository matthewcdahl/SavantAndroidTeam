package com.savant.savantandroidteam.poker;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.main.MainActivity;
import com.savant.savantandroidteam.R;

public class PokerUserFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //UI Declarations
    private TextView mWaitingText;
    private TextView mSessionName;
    private Spinner mDiffSpinner;


    //Firebase Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mPokerDatabase;
    private DatabaseReference mCurrentSession;
    private DatabaseReference mNameRef;
    private DatabaseReference mRevealedRef;

    //TOOLBAR
    private android.support.v7.app.ActionBar masterBarHolder;
    Toolbar toolbar;

    //Internal


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_poker_user, container, false);
        ((MainActivity) getActivity()).setTitle("Submit Response");

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
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });


        //UI
        mDiffSpinner = view.findViewById(R.id.spinner_difficulty);
        setUpSpinner();
        mDiffSpinner.setSelection(getPosFromName(getSessionDIFF()));
        mDiffSpinner.setOnItemSelectedListener(this);

        mWaitingText = (TextView) view.findViewById(R.id.tv_waiting);
        waitingText();

        mSessionName = (TextView) view.findViewById(R.id.tv_session_name_user);
        mSessionName.setText(getSessionNAME());

        //Firebase Initializations
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mPokerDatabase = mDatabase.getReference("poker");
        mCurrentSession = mPokerDatabase.child(getSessionID());
        mRevealedRef = mCurrentSession.child("revealed");
        mNameRef = mCurrentSession.child("name");


        //Firebase Reference for when the host shows responses
        mRevealedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().toString().equals("true")) {
                    String id = getSessionID();
                    PokerResultsFragment fragment = new PokerResultsFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString("ID", id);
                    fragment.setArguments(arguments);
                    FragmentTransaction ft;
                    try {
                        ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, fragment);
                        ft.commit();
                    } catch (Exception e) {
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Firebase reference for showing the name of the session real time.
        mNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mSessionName.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        return view;
    }

    /**
     * Pushes the users name and response to firebase in the form of Key: First name, Value: Response
     */
    private void submitDiff() {
        String diff = mDiffSpinner.getSelectedItem().toString();
        if(!diff.equals("Select...")) {
            String userEmail = getModifiedEmail();
            DatabaseReference userRef = mCurrentSession.child("responses");
            userRef.child(userEmail).setValue(diff);
            waitingText();
        }


    }

    /**
     * @return the unique id of the session from the bundled arguments from the main fragment
     */
    private String getSessionID() {
        Bundle arguments = getArguments();
        String session = arguments.getString("ID");
        return session;

    }

    /**
     * @return the current response from the user
     */
    private String getSessionDIFF() {
        Bundle arguments = getArguments();
        String diff = arguments.getString("DIFF");
        return diff;

    }

    /**
     * If the user has given a response they will be told to wait for host
     */
    private void waitingText() {
        if (mDiffSpinner.getSelectedItem().toString().equals("Select...")) mWaitingText.setVisibility(View.GONE);
        else {
            mWaitingText.setVisibility(View.VISIBLE);
        }

    }


    /**
     * @return name of the session from the main fragment bundle
     */
    private String getSessionNAME() {
        Bundle arguments = getArguments();
        String name = arguments.getString("NAME");
        return name;
    }

    /**
     * @return the modified email with ',' from firebase
     */
    @NonNull
    private String getModifiedEmail() {
        return mAuth.getCurrentUser().getEmail().trim().replace('.', ',');
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        submitDiff();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void setUpSpinner() {
        String[] arr;
        arr = new String[]{"Select...", "Mouse", "Groundhog", "Fox", "Lion", "Buffalo", "Elephant", "Honey Badger"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arr);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDiffSpinner.setAdapter(spinnerArrayAdapter);
    }

    private int getPosFromName(String name){
        if(name!=null) {
            switch (name) {
                case "Mouse":
                    return 1;
                case "Groundhog":
                    return 2;
                case "Fox":
                    return 3;
                case "Lion":
                    return 4;
                case "Buffalo":
                    return 5;
                case "Elephant":
                    return 6;
                case "Honey Badger":
                    return 7;
                default:
                    return 0;
            }
        }
        else return 0;
    }


}
