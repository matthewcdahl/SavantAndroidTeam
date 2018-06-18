package com.savant.savantandroidteam.poker;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.R;

import java.util.List;

public class PokerAdapter extends RecyclerView.Adapter<PokerAdapter.ViewHolder> {

    private List<SessionItem> listItems;
    private Context context;

    //Firebase
    private FirebaseAuth mAuth;
    private String userName;
    private FirebaseDatabase mDB;
    private DatabaseReference mRoot;
    PokerHomebase hb;
    PokerResultsHomebase rHb;

    public PokerAdapter(List<SessionItem> listItems, final Context context, DataSnapshot ss) {
        this.listItems = listItems;
        this.context = context;
        hb = new PokerHomebase(ss);
        System.out.println("SS!!!!" + ss.toString()); //SS is the poker ss not the overall!!!!
        mAuth = FirebaseAuth.getInstance();
        userName = mAuth.getCurrentUser().getEmail();
        for(int i = 0; i<userName.length(); i++){
            if(userName.charAt(i) == '.'){
                userName = userName.substring(0, i);
            }
        }
        userName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
        mDB = FirebaseDatabase.getInstance();
        mRoot = mDB.getReference();
        final PokerAdapter a  = this;
        mRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rHb = new PokerResultsHomebase(dataSnapshot);
                a.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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

        String sessionStatus;

        if(hb.isRevealed(position)){ // Session is over
            System.out.println(position);
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.pokerSessionClosed));
            sessionStatus = "CLOSED";
        }
        else{ //Session is still going
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.pokerSessionOpen));
            sessionStatus = "OPEN";
        }

        Resources r = context.getResources();
        int px6 = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
        int px3 = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());


        if(listItems.size() == 1){
            setMargins(holder.linearLayout, 0,px6,0,px6);
        }
        else if(position == 0){
            setMargins(holder.linearLayout, 0,px6,0,px3);

        }
        else if(position == getItemCount()-1){
            setMargins(holder.linearLayout, 0,px3,0,px6);
        }

        String hostModEmail = listItem.getHost();
        String nickname;
        if(rHb!=null) nickname = rHb.getNickname(hostModEmail);
        else nickname = "Loading...";

        holder.name.setText(listItem.getName() + ": " + sessionStatus);
        holder.host.setText("Host: " + nickname);
        holder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view){
                handleClick(position, view);
            }
        });
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
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
            host.setText("Loading...");
            //linearLayout.setBackgroundColor(itemView.getResources().getColor(R.color.white));
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

