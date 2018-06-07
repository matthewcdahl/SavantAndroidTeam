package com.savant.savantandroidteam.poker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.savant.savantandroidteam.R;

import java.util.List;

public class PokerAdapter extends RecyclerView.Adapter<PokerAdapter.ViewHolder> {

    private List<SessionItem> listItems;
    private Context context;

    //Firebase
    private FirebaseAuth mAuth;
    private String userName;
    private FirebaseDatabase mDB;
    private DatabaseReference mPoker;
    PokerHomebase hb = new PokerHomebase();

    public PokerAdapter(List<SessionItem> listItems, Context context) {
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
        mPoker = mDB.getReference("poker");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SessionItem listItem = listItems.get(position);

        holder.host.setText(listItem.getHost());
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

        public TextView host, name;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            host = (TextView) itemView.findViewById(R.id.tv_host);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_test);
        }

    }

    private void handleClick(int position, final View view){

        if(hb.isRevealed(position)){
            //Go to results page
            SessionItem sessionItem = hb.getClickedSession(position);
            String id = sessionItem.getId();
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            PokerResultsFragment fragment = new PokerResultsFragment();
            Bundle arguments = new Bundle();
            arguments.putString("ID", id);
            fragment.setArguments(arguments);
            final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }
        else{
            if(hb.isHostOfSession(position)){
                //Go to Host Page
                SessionItem sessionItem = hb.getClickedSession(position);
                String id = sessionItem.getId();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                PokerHostFragment fragment = new PokerHostFragment();
                Bundle arguments = new Bundle();
                arguments.putString("DIFF", hb.getResponse(position));
                arguments.putString("NAME", hb.getName(position));
                arguments.putString("ID", id);
                fragment.setArguments(arguments);
                final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            }
            else{
                SessionItem sessionItem = hb.getClickedSession(position);
                String id = sessionItem.getId();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                PokerUserFragment fragment = new PokerUserFragment();
                Bundle arguments = new Bundle();
                arguments.putString("NAME", hb.getName(position));
                arguments.putString("DIFF", hb.getResponse(position));
                arguments.putString("ID", id);
                fragment.setArguments(arguments);
                final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            }
        }


    }


}

