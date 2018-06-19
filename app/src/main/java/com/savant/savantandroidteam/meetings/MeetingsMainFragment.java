package com.savant.savantandroidteam.meetings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class MeetingsMainFragment extends Fragment {

    //Firebase Declarations
    FirebaseDatabase mDatabase;
    DatabaseReference mMeetingsRef;
    private MeetingsHomebase mMeetingsHomebase;


    //UI Declarations
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private List<MeetingItem> meetingItems;
    private TextView mNoCurrentText;
    private TextView mClickPlusText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_meetings_main, container, false);

        ((MainActivity) getActivity()).setUpToolbar("Meetings");


        mDatabase = FirebaseDatabase.getInstance();
        mMeetingsRef = mDatabase.getReference("meetings");
        meetingItems = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mNoCurrentText = (TextView) view.findViewById(R.id.no_current_sessions);
        mClickPlusText = (TextView) view.findViewById(R.id.press_plus);


        mMeetingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMeetingsHomebase = new MeetingsHomebase(dataSnapshot);
                setStartText();
                addMeetingsToView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.meetings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_meeting:
                addMeeting();

        }

        return super.onOptionsItemSelected(item);
    }

    //Initialize the adapter to populate the recycler view
    private void addMeetingsToView() {
        meetingItems = mMeetingsHomebase.getMeetings();
        adapter = new MeetingsAdapter(meetingItems, getContext());
        mRecyclerView.setAdapter(adapter);

        //Code for the swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //mMeetingsHomebase.deleteMeeting();
                mMeetingsHomebase.deleteMeeting(viewHolder.getAdapterPosition());
                Toast.makeText(getContext(), "Meeting Deleted", Toast.LENGTH_LONG).show();

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
     *
     * @param dp to convert
     * @return the px equal
     */
    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    /**
     * All this does is start up the Host fragment to create a meeting
     */
    private void addMeeting() {
        SharedPreferences.Editor edit = getContext().getSharedPreferences("MeetingPrefsExtra", Context.MODE_PRIVATE).edit();
        edit.putString("isEdited", "false");
        edit.apply();

        MeetingsHostFragment fragment = new MeetingsHostFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null).commit();
    }

    //If there are no current meetings this will diplay the starter text
    private void setStartText() {
        if (mMeetingsHomebase.getNumberOfSessions() == 0) {
            mNoCurrentText.setVisibility(View.VISIBLE);
            mClickPlusText.setVisibility(View.VISIBLE);
        } else {
            mNoCurrentText.setVisibility(View.GONE);
            mClickPlusText.setVisibility(View.GONE);
        }
    }


}
