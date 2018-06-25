package com.savant.savantandroidteam.tictactoe;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

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
import com.savant.savantandroidteam.poker.PokerMainFragment;

import static android.content.Context.MODE_PRIVATE;

public class TicTacToeUserFragment extends Fragment {

    //UI and Internal Declarations
    private TextView mNameText;
    private ImageView mTL, mTC, mTR;
    private ImageView mML, mMC, mMR;
    private ImageView mBL, mBC, mBR;
    private TextView hostSymbol, oppSymbol;
    private String hostNickname, oppNickname;
    private String mostRecent;



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
                if(TicTacToeUserFragment.this.isVisible()) {
                    mTicTacToeHomebase = new TicTacToeHomebase(dataSnapshot, getContext());
                    updateView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        initializeBoard(view);

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
     * Swich to meetings main fragment
     */
    private void switchToMain(){
        TicTacToeMainFragment fragment = new TicTacToeMainFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void updateView(){
        mTL.setImageResource(mTicTacToeHomebase.getTTTatSpot("mTL"));
        mTC.setImageResource(mTicTacToeHomebase.getTTTatSpot("mTC"));
        mTR.setImageResource(mTicTacToeHomebase.getTTTatSpot("mTR"));
        mML.setImageResource(mTicTacToeHomebase.getTTTatSpot("mML"));
        mMC.setImageResource(mTicTacToeHomebase.getTTTatSpot("mMC"));
        mMR.setImageResource(mTicTacToeHomebase.getTTTatSpot("mMR"));
        mBL.setImageResource(mTicTacToeHomebase.getTTTatSpot("mBL"));
        mBC.setImageResource(mTicTacToeHomebase.getTTTatSpot("mBC"));
        mBR.setImageResource(mTicTacToeHomebase.getTTTatSpot("mBR"));

        if(mTicTacToeHomebase.gameOver(mostRecent)){
            Toast.makeText(getContext(), "GAME OVER!!!!", Toast.LENGTH_SHORT).show();
        }


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
                handleBoardClick(mTL, "mTL");
            }
        });
        mTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoardClick(mTC, "mTC");
            }
        });
        mTR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoardClick(mTR, "mTR");
            }
        });
        mML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoardClick(mML, "mML");
            }
        });
        mMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoardClick(mMC, "mMC");
            }
        });
        mMR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoardClick(mMR, "mMR");
            }
        });
        mBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoardClick(mBL, "mBL");
            }
        });
        mBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoardClick(mBC, "mBC");
            }
        });
        mBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBoardClick(mBR, "mBR");
            }
        });

    }

    private void handleBoardClick(ImageView img, String pos){
        String turn = mTicTacToeHomebase.getTurn();
        if(mTicTacToeHomebase.isDeviceTurn(turn) && img.getDrawable()==null) {
            if (turn.equals(mTicTacToeHomebase.getNicknameOfHost())) {
                DatabaseReference gameRef = mTicTacToeRef.child(mTicTacToeHomebase.getGameId());
                gameRef.child("turn").setValue(mTicTacToeHomebase.getNicknameOfOpp());
                img.setImageResource(R.drawable.ic_close_black_80dp);
                uploadToFirebase(pos, "X", gameRef);
                mostRecent = pos;
            } else {
                DatabaseReference gameRef = mTicTacToeRef.child(mTicTacToeHomebase.getGameId());
                gameRef.child("turn").setValue(mTicTacToeHomebase.getNicknameOfHost());
                img.setImageResource(R.drawable.ic_circle_80dp);
                uploadToFirebase(pos, "O", gameRef);
                mostRecent = pos;
            }
        }
        else{
            //Toast
        }
    }

    private void uploadToFirebase(String pos, String XorO, DatabaseReference gameRef){
        gameRef.child(pos).setValue(XorO);
    }

}
