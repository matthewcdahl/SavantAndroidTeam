package com.savant.savantandroidteam.poker;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

public class PokerResultsFragment extends Fragment {

    //UI
    TextView sessionName;
    TextView conversionText;

    private boolean isVisible;
    private RecyclerView.Adapter adapter;
    private List<ResultItem> resultItems;
    private RecyclerView mRecyclerView;
    private RelativeLayout mRelativeLayout;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mPokerDatabase;
    private DatabaseReference mCurrentSession;
    private DatabaseReference mCurrentResults;
    private DatabaseReference mRootRef;
    private PokerResultsHomebase newHomebase;

    private ActionBar masterBarHolder;
    Toolbar toolbar;
    boolean dialogIsOpen;


    Animation openConversion;
    Animation closeConversion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_poker_results_new, container, false);
        setHasOptionsMenu(true);

        ((MainActivity) getActivity()).setTitle("Session Results");

        openConversion = AnimationUtils.loadAnimation(getContext(), R.anim.conversion_open);
        closeConversion = AnimationUtils.loadAnimation(getContext(), R.anim.conversion_close);
        dialogIsOpen = false;

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
            }
        });
        mRelativeLayout = view.findViewById(R.id.results_page_rl);


        //UI
        sessionName = (TextView) view.findViewById(R.id.session_name_results);
        conversionText = (TextView) view.findViewById(R.id.conversion_box);
        conversionText.setVisibility(View.GONE);
        String animalsToSet = "Conversion\n\n" +
                "Mouse: 1\nGroundhog: 2\n" +
                "Fox: 3\nLion: 5\n" +
                "Buffalo: 8\nElephant: 13\n" +
                "HoneyBadger: 21";
        conversionText.setText(animalsToSet);
        isVisible = false;
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mPokerDatabase = mDatabase.getReference("poker");
        mCurrentSession = mPokerDatabase.child(getSession());
        mCurrentResults = mCurrentSession.child("responses");
        mRootRef = mDatabase.getReference();


        //Firebase listener
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newHomebase = new PokerResultsHomebase(dataSnapshot, getSession());
                if (PokerResultsFragment.this.isVisible()) {
                    int isRevealedPos = newHomebase.getPosId(getSession());
                    if (newHomebase.isRevealed(isRevealedPos)) {//if the session has not been deleted
                        addSessionsToView();
                        setName();

                    } else if (isRevealedPos == -1) {// This will happen when the session has been deleted
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        PokerMainFragment fragment = new PokerMainFragment();
                        FragmentManager fm = activity.getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.commit();
                        Toast.makeText(getContext(), "Session Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        conversionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisible){
                    isVisible = false;
                    conversionText.startAnimation(closeConversion);
                    conversionText.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    //Menu creation and click handling
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.poker_results_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_session_menu_item:
                goToMain(getView());
                deleteSession();
                break;
            case R.id.show_info_menu_item:
                if (!isVisible) {
                    conversionText.startAnimation(openConversion);
                    isVisible = true;
                    conversionText.setVisibility(View.VISIBLE);
                } else {
                    isVisible = false;
                    conversionText.startAnimation(closeConversion);
                    conversionText.setVisibility(View.GONE);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }




    /**
     *
     * @return the current session ID from the bundled arguments from main fragment
     */
    private String getSession() {
        Bundle arguments = getArguments();
        String session = arguments.getString("ID");
        return session;
    }


    /**
     * remove session from firebase and listeners should take care of the rest
     */
    private void deleteSession() {
        String id = getSession();
        newHomebase.removeSession(id);
    }

    /**
     * @param view current
     * go to the main fragment in poker
     */
    private void goToMain(View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        PokerMainFragment fragment = new PokerMainFragment();
        final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }


    /**
     * Set the name of the session in the UI
     */
    private void setName() {
        String name = newHomebase.getResultSessionName(getSession());
        sessionName.setText(name);
    }

    /**
     * Initialize the adapter for the recycler view of responses
     */
    private void addSessionsToView() {
        resultItems = newHomebase.getResults();
        adapter = new PokerResultsAdapter(resultItems, getContext());
        mRecyclerView.setAdapter(adapter);
    }


}
