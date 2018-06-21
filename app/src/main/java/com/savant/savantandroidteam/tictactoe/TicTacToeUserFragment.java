package com.savant.savantandroidteam.tictactoe;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.R;
import com.savant.savantandroidteam.main.MainActivity;
import com.savant.savantandroidteam.meetings.MeetingsHomebase;
import com.savant.savantandroidteam.meetings.MeetingsHostFragment;
import com.savant.savantandroidteam.meetings.MeetingsMainFragment;

import static android.content.Context.MODE_PRIVATE;

public class TicTacToeUserFragment extends Fragment {

    //UI and Internal Declarations
    private TextView mNameText;
    private ImageView mTL, mTC, mTR;
    private ImageView mML, mMC, mMR;
    private ImageView mBL, mBC, mBR;
    private TextView mYourSymbol, mOppSymbol;



    //Firebase Declarations
    private TicTacToeHomebase mTicTacToeHomebase;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mTicTacToeRef;

    //TOOLBAR
    private ActionBar masterBarHolder;
    Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_tictactoe_user, container, false);

        ((MainActivity) getActivity()).setTitle("Tic Tac Toe");
        initializeBoard(view);

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
                        .replace(R.id.fragment_container, new TicTacToeMainFragment()).commit();
            }
        });

        //UI Initializations
        mNameText = (TextView) view.findViewById(R.id.tv_meeting_user_name);

        //Firebase Initializations
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference();
        mUsersRef = mDatabase.getReference("users");
        mTicTacToeRef = mDatabase.getReference("tictactoe");

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.meetings_menu_user, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    /**
     * set the text views for the meeting information
     */
    private void setTextViews(){
        int pos = getPosition();
        mNameText.setText(mTicTacToeHomebase.getName(pos));
    }

    /**
     *
     * @return current position of the meeting
     */
    private int getPosition(){
        Bundle args = getArguments();
        return args.getInt("meetingPos");
    }

    /**
     * @return the randomized id of the meeting
     */
    private String getMeetingID() {
        Bundle args = getArguments();
        return args.getString("meetingID");
    }


    /**
     * Swich to meetings main fragment
     */
    private void switchToMain(){
        TicTacToeMainFragment fragment = new TicTacToeMainFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void updateView(){

    }

    private void initializeBoard(View view){
        mTL = view.findViewById(R.id.ttt_tl);
        mTC = view.findViewById(R.id.ttt_tc);
        mTR = view.findViewById(R.id.ttt_tr);
        mML = view.findViewById(R.id.ttt_ml);
        mMC = view.findViewById(R.id.ttt_mc);
        mMR = view.findViewById(R.id.ttt_mr);
        mBL = view.findViewById(R.id.ttt_bl);
        mBC = view.findViewById(R.id.ttt_bc);
        mBR = view.findViewById(R.id.ttt_br);

        mTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTL.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });
        mTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTC.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });
        mTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTR.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });
        mML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mML.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });
        mMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMC.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });
        mMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMR.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });
        mBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBL.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });
        mBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBC.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });
        mBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBR.setImageResource(R.drawable.ic_close_black_80dp);
            }
        });



    }
}
