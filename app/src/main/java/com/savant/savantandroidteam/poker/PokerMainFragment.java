package com.savant.savantandroidteam.poker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

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

public class PokerMainFragment extends Fragment {

    //Firebase Declarations
    private FirebaseDatabase mDatabase;
    private DatabaseReference mPoker;
    private FirebaseAuth mAuth;
    private PokerHomebase mPokerHomebase;


    //UI Declarations
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<SessionItem> sessionItems;
    private TextView mNoCurrentText;
    private TextView mClickPlusText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_poker_main, container, false);
        ((MainActivity)getActivity()).setUpToolbar("Poker Sessions");





        //Firebase Initializations
        mDatabase = FirebaseDatabase.getInstance();
        mPoker = mDatabase.getReference("poker");
        sessionItems = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        //UI Initializations
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNoCurrentText = (TextView) view.findViewById(R.id.no_current_sessions);
        mClickPlusText = (TextView) view.findViewById(R.id.press_plus);


        //Will listen for sessions on firebase
        mPoker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPokerHomebase = new PokerHomebase(dataSnapshot);
                addSessionsToView(dataSnapshot);
                setStartText();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        return view;
    }

    /**
     *
     * @param menu the menu to inflate
     * @param inflater
     * This is the menu with the add option
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.poker_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.add_poker:
                addSession();

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize the adapter for the recycler view
     */
    private void addSessionsToView(final DataSnapshot ss){
        sessionItems = mPokerHomebase.getSessions();
        adapter = new PokerAdapter(sessionItems, getContext(), ss);
        mRecyclerView.setAdapter(adapter);

        //This is the code for the swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //mMeetingsHomebase.deleteMeeting();
                System.out.println(viewHolder.getAdapterPosition());
                if(mPokerHomebase.isRevealed(viewHolder.getAdapterPosition())){
                    mPokerHomebase.removeSession(Integer.toString(mPokerHomebase.getIdPos(viewHolder.getAdapterPosition())));
                    addSessionsToView(mPokerHomebase.getSnapshot());
                    Toast.makeText(getContext(), "Session Deleted", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), "Can only delete CLOSED sessions!", Toast.LENGTH_LONG).show();
                    addSessionsToView(mPokerHomebase.getSnapshot());
                }

            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final float ALPHA_FULL = 1.0f;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;
                    Paint p = new Paint();
                    Bitmap icon;
                    icon = BitmapFactory.decodeResource(
                            getContext().getResources(), R.drawable.baseline_delete_sweep_black_36dp);
                    /* Set your color for negative displacement */
                    p.setColor(getResources().getColor(R.color.swipeDeleteBack));
                    // Draw Rect with varying left side, equal to the item's right side
                    // plus negative displacement dX
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    //Set the image icon for Left swipe
                    c.drawBitmap(icon,
                            (float) itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                            (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                            p);
                    // Fade out the view as it is swiped out of the parent's bounds
                    final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    /**
     * Converting dp to pixels
     */
    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    /**
     * Firebase initializing for the session
     */
    private void addSession(){
        if(mPokerHomebase == null){ // need to wait for loading to prevent data from not being initialized
            Toast.makeText(getContext(), "Wait for connection...", Toast.LENGTH_LONG).show();
        }
        else {
            String numOfSessions = getLastSessionID();
            String numOfSessionsFinal = addOneToString(numOfSessions);
            DatabaseReference ref = mPoker.child(numOfSessionsFinal);

            //Set Session ID
            String newSessionName = "Session " + numOfSessionsFinal;
            ref.setValue(newSessionName);

            //Set Session name
            ref.child("name").setValue(newSessionName);

            //Set Host of session
            String userEmail = getModifiedEmail();
            /*for (int i = 0; i < userEmail.length(); i++) {
                if (userEmail.charAt(i) == '.') {
                    userEmail = userEmail.substring(0, i);
                }
            }
            userEmail = userEmail.substring(0, 1).toUpperCase() + userEmail.substring(1);*/
            ref.child("host").setValue(userEmail);

            //Set is Revealed
            ref.child("revealed").setValue("false");

            startSession(numOfSessionsFinal, newSessionName);
        }

    }

    /**
     * Switch to the host fragment with with name and id bundled
     */
    private void startSession(String id, String name){
        PokerHostFragment fragment = new PokerHostFragment();
        Bundle arguments = new Bundle();
        arguments.putString("ID", id);
        arguments.putString("NAME", name);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null).commit();
    }

    /**
     *
     * @return the last session id
     * for example if there are 3 sessions active this will return 4 if the 3rd session
     * id is 3
     */
    private String getLastSessionID(){
        /*if(rawNumberOfSessions == null) return 0;
        else return Integer.parseInt(rawNumberOfSessions);*/
        return mPokerHomebase.getLastSession();
    }

    /**
     *
     * @param str this is a string that is a number
     * @return a str incremented by one
     * for example if input is "7" this will return "8"
     */
    private String addOneToString(String str){
        int i = Integer.parseInt(str);
        i++;
        return Integer.toString(i);
    }

    /**
     * If there are no current sessions the noCurrentSessions text will appear
     */
    private void setStartText(){
        if(mPokerHomebase.getNumberOfSessions() == 0){
            mNoCurrentText.setVisibility(View.VISIBLE);
            mClickPlusText.setVisibility(View.VISIBLE);
        }
        else{
            mNoCurrentText.setVisibility(View.GONE);
            mClickPlusText.setVisibility(View.GONE);
        }
    }

    /**
     *
     * @return the current users email with ',' instead of '.'
     */
    private String getModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
    }

}
