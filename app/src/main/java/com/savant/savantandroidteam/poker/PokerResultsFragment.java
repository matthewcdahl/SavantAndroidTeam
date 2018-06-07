package com.savant.savantandroidteam.poker;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

import java.util.ArrayList;

public class PokerResultsFragment extends Fragment {

    //UI
    TextView nameTextView;
    TextView animalTextView;
    TextView sessionName;
    TextView conversionText;
    Button mDeleteButton;
    ImageView infoImage;
    private boolean isVisible;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mPokerDatabase;
    private DatabaseReference mCurrentSession;
    private PokerHomebase mPokerHomebase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_poker_results, container, false);

        ((MainActivity) getActivity()).setTitle("Session Results");


        //UI
        nameTextView = (TextView) view.findViewById(R.id.results_names);
        animalTextView = (TextView) view.findViewById(R.id.results_animals);
        mDeleteButton = (Button) view.findViewById(R.id.delete_session_btn);
        mDeleteButton.setVisibility(View.GONE);
        sessionName = (TextView) view.findViewById(R.id.session_name_results);
        conversionText = (TextView) view.findViewById(R.id.conversion_box);
        conversionText.setVisibility(View.GONE);
        conversionText.setText("Conversion\n\n" +
                "Mouse: 1\nGroundhog: 2\n" +
                "Fox: 3\nLion: 5\n" +
                "Buffalo: 8\nElephant: 13\n" +
                "HoneyBadger: 21");//TODO make a seperate variable
        infoImage = (ImageView) view.findViewById(R.id.info_image_btn);
        isVisible = false;


        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mPokerDatabase = mDatabase.getReference("poker");
        mCurrentSession = mPokerDatabase.child(getSession());

        //Firebase listener
        mPokerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPokerHomebase = new PokerHomebase(dataSnapshot);
                if (PokerResultsFragment.this.isVisible()) {
                    int isRevealedPos = mPokerHomebase.getPosId(getSession());
                    if (mPokerHomebase.isRevealed(isRevealedPos)) {//if the session has not been deleted
                        setResults();
                        setName();
                        initializeDeletButton();

                    } else if(isRevealedPos == -1) {// This will happen when the session has been deleted
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        PokerMainFragment fragment = new PokerMainFragment();
                        FragmentManager fm = activity.getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.commit();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain(v);
                deleteSession();
            }
        });

        //Conversion Chart
        infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible) {
                    isVisible = true;
                    conversionText.setVisibility(View.VISIBLE);
                    infoImage.setImageResource(R.drawable.ic_close_black_24dp);
                }
                else {
                    isVisible = false;
                    conversionText.setVisibility(View.GONE);
                    infoImage.setImageResource(R.drawable.ic_info_outline_white_24dp);
                }
            }
        });


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }


    //Get session id from main fragment
    private String getSession() {
        Bundle arguments = getArguments();
        String session = arguments.getString("ID");
        return session;
    }

    private String getSessionName(String id) {
        Bundle arguments = getArguments();
        String sessionName = arguments.getString("NAME");
        return sessionName;
    }

    //Get the names of those who gave answers
    private String getFinalResultsNames() {
        return mPokerHomebase.getResultNames(getSession());
    }

    //Get their associated animals
    private String getFinalResultsAnimals() { return mPokerHomebase.getResultAnimals(getSession());}


    //Set their results
    private void setResults() {
        nameTextView.setText(getFinalResultsNames());
        animalTextView.setText(getFinalResultsAnimals());

    }


    //remove session from firebase and listeners should take care of the rest
    private void deleteSession() {
        String id = getSession();
        mPokerHomebase.removeSession(id);
    }

    //return to main fragment
    private void goToMain(View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        PokerMainFragment fragment = new PokerMainFragment();
        final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    //Will only show the delete button if they are the host
    private void initializeDeletButton() {
        if (!mPokerHomebase.isHostOfSessionDelete(getSession())) {
            mDeleteButton.setVisibility(View.GONE);
        } else mDeleteButton.setVisibility(View.VISIBLE);
    }

    //name of the session
    private void setName() {
        String name = mPokerHomebase.getResultSessionName(getSession());
        sessionName.setText(name);
    }


}
