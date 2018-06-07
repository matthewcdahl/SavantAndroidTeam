package com.savant.savantandroidteam.meetings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.savant.savantandroidteam.R;
import com.savant.savantandroidteam.poker.PokerHostFragment;

import java.util.List;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.ViewHolder> {

    private List<MeetingItem> listItems;
    private Context context;

    //Firebase
    private FirebaseAuth mAuth;
    private String userName;
    private FirebaseDatabase mDB;
    private DatabaseReference mMeetingRef;
    MeetingsHomebase hb = new MeetingsHomebase();

    public MeetingsAdapter(List<MeetingItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        userName = mAuth.getCurrentUser().getEmail();
        for(int i = 0; i<userName.length(); i++){
            if(userName.charAt(i) == '.'){
                userName = userName.substring(0, i);
            }
        }
        userName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
        mDB = FirebaseDatabase.getInstance();
        mMeetingRef = mDB.getReference("meetings");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MeetingItem listItem = listItems.get(position);

        holder.id.setText(listItem.getDate() + " " + listItem.getTime());
        holder.name.setText(listItem.getName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view){
                handleClick(position, view);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView id, name;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            id = (TextView) itemView.findViewById(R.id.tv_host);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_test);
        }

    }

    private void handleClick(int position, final View view){
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        MeetingsUserFragment fragment = new MeetingsUserFragment();
        Bundle arguments = new Bundle();
        arguments.putString("meetingID", getMeetingID(position));
        arguments.putInt("meetingPos", position);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private String getMeetingID(int pos){
        return hb.getId(pos);
    }




}

