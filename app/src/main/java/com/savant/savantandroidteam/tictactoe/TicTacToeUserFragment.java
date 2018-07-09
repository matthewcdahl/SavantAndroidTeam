package com.savant.savantandroidteam.tictactoe;


import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import com.google.firebase.auth.FirebaseAuth;
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

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TicTacToeUserFragment extends Fragment {

    //UI and Internal Declarations
    private ImageView mTL, mTC, mTR;
    private ImageView mML, mMC, mMR;
    private ImageView mBL, mBC, mBR;
    private TicTacToeBoard gameBoard;
    private String gameID;




    //Firebase Declarations
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mTicTacToeRef;
    private DatabaseReference mCurrGameRef;
    private DataSnapshot mRootDataSnapshot;
    private DataSnapshot mGameDataSnapshot;
    private FirebaseAuth mAuth;

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
                switchToMain();
            }
        });

        //Firebase Initializations
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference();
        mUsersRef = mDatabase.getReference("users");
        mTicTacToeRef = mDatabase.getReference("tictactoe");


        initializeBoard(view);


        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(TicTacToeUserFragment.this.isVisible()) {
                    mRootDataSnapshot = dataSnapshot;
                    gameID = getGameID();
                    mCurrGameRef = mTicTacToeRef.child(gameID);
                    currGameListener();
                }
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
     * Swich to meetings main fragment
     */
    private void switchToMain(){
        TicTacToeMainFragment fragment = new TicTacToeMainFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void updateView(){
        String state = gameBoard.getBoardState();
        for(int i = 0; i<state.length(); i++){
            if(state.charAt(i) == 'X') setImage(i, "X");
            else if(state.charAt(i) == 'O') setImage(i, "O");
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
        if(userIsHost()){

        }
        else{

        }
    }


    private List<TicTacToeBoard> getGames(){
        List<TicTacToeBoard> games = new ArrayList<>();
        Iterable<DataSnapshot> iter = mRootDataSnapshot.getChildren();
        for(DataSnapshot child: iter){
            if(child.getKey().equals("tictactoe")){
                Iterable<DataSnapshot> iter2 = child.getChildren();
                for(DataSnapshot child2: iter2){
                    TicTacToeBoard toAdd = new TicTacToeBoard();
                    Iterable<DataSnapshot> iter3 = child2.getChildren();
                    for(DataSnapshot child3: iter3){
                        if(child3.getKey().equals("opp")) toAdd.setOpp(child3.getValue().toString());
                        else if(child3.getKey().equals("id")) toAdd.setId(child3.getValue().toString());
                        //If needed add other states here by using, else if(child3.getKey().equals(@state))
                    }
                    games.add(toAdd);
                }
            }
        }
        return games;
    }

    private String getGameID() {
        SharedPreferences tttPrefs = getContext().getSharedPreferences("tictactoe", MODE_PRIVATE);
        String from = tttPrefs.getString("from", "error");
        if (from.equals("host")) {
            return tttPrefs.getString("id", "-1");
        } else {//from main
            return getIdFromPos(tttPrefs.getString("id", "-1"));
        }
    }

    private String getIdFromPos(String pos){
        List<TicTacToeBoard> boards = getGames();
        return boards.get(Integer.parseInt(pos)).getId();
    }

    private void currGameListener(){
        mCurrGameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGameDataSnapshot = dataSnapshot;
                gameBoard.setId(getGameValue("id"));
                gameBoard.setHost(getGameValue("host"));
                gameBoard.setOpp(getGameValue("opp"));
                gameBoard.setBoardState(getGameValue("state"));
                gameBoard.setTurn(getGameValue("turn"));
                gameBoard.setWinner(getGameValue("winner"));
                updateView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getGameValue(String key){
        Iterable<DataSnapshot> iter = mGameDataSnapshot.getChildren();
        for(DataSnapshot child: iter){
            if(child.getKey().equals(key)) return child.getValue().toString();
        }
        return "ERROR";
    }

    private void setImage(int pos, String symbol){
        int symbolDraw;
        if(symbol.equals("X"))symbolDraw = R.drawable.ic_close_black_80dp;
        else if(symbol.equals("O")) symbolDraw = R.drawable.ic_circle_80dp;
        else symbolDraw = 0;

        if(pos == 0) mTL.setImageResource(symbolDraw);
        else if(pos == 1) mTC.setImageResource(symbolDraw);
        else if(pos == 2) mTR.setImageResource(symbolDraw);
        else if(pos == 3) mML.setImageResource(symbolDraw);
        else if(pos == 4) mMC.setImageResource(symbolDraw);
        else if(pos == 5) mMR.setImageResource(symbolDraw);
        else if(pos == 6) mBL.setImageResource(symbolDraw);
        else if(pos == 7) mBC.setImageResource(symbolDraw);
        else if(pos == 8) mBR.setImageResource(symbolDraw);

    }

    private boolean userIsHost(){
        String email = getDeviceModifiedEmail();
        return true;
    }

    private String getDeviceModifiedEmail(){
        String old = mAuth.getCurrentUser().getEmail();
        String email = old.replace('.', ',');

        DataSnapshot usersSnap = mRootDataSnapshot.child("users");
        DataSnapshot userSnap = usersSnap.child(email);
        Iterable<DataSnapshot> iter = userSnap.getChildren();

        return "";
    }


}
