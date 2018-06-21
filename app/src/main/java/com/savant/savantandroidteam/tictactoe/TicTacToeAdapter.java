package com.savant.savantandroidteam.tictactoe;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.savant.savantandroidteam.R;
import com.savant.savantandroidteam.meetings.MeetingItem;
import com.savant.savantandroidteam.meetings.MeetingsUserFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TicTacToeAdapter extends RecyclerView.Adapter<TicTacToeAdapter.ViewHolder> {

    private List<TicTacToeItem> listItems;
    private Context context;

    //Firebase
    private FirebaseAuth mAuth;
    private String userName;

    public TicTacToeAdapter(List<TicTacToeItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        userName = mAuth.getCurrentUser().getEmail();
        for (int i = 0; i < userName.length(); i++) {
            if (userName.charAt(i) == '.') {
                userName = userName.substring(0, i);
            }
        }
        userName = userName.substring(0, 1).toUpperCase() + userName.substring(1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tictactoe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TicTacToeItem listItem = listItems.get(position);
        String status;

        Resources r = context.getResources();
        int px6 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
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

        holder.opp.setText(listItem.getOpp());
        holder.name.setText(listItem.getName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, opp;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            opp = (TextView) itemView.findViewById(R.id.tv_opp);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_test);
        }

    }

    private void handleClick(int position, final View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        TicTacToeUserFragment fragment = new TicTacToeUserFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("gamePos", position);
        fragment.setArguments(arguments);
        final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }





}

