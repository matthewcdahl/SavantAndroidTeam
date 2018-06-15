package com.savant.savantandroidteam.meetings;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.savant.savantandroidteam.poker.PokerHostFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        for (int i = 0; i < userName.length(); i++) {
            if (userName.charAt(i) == '.') {
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
        String status;
        Log.d("DATEiii", listItem.getDate());
        if (meetingDateHasPassed(listItem.getDate(), listItem.getTime())) {
            status = "PASSED";
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.sprintMeetingOver));
        } else {
            status = "UPCOMING";
            holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.sprintMeetingUpcoming));
        }

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

        holder.id.setText(listItem.getDate() + " " + listItem.getTime());
        holder.name.setText(listItem.getName() + ": " + status);

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

        public TextView id, name;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            id = (TextView) itemView.findViewById(R.id.tv_host);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_test);
        }

    }

    private void handleClick(int position, final View view) {
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

    private String getMeetingID(int pos) {
        return hb.getId(pos);
    }

    private boolean meetingDateHasPassed(String meetingDate, String meetingTime) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        String rMeetingTime = reformatMeetingTime(meetingTime);

        int meetingYear = getMeetingYear(meetingDate);
        int meetingMonth = getMeetingMonth(meetingDate);
        int meetingDay = getMeetingDay(meetingDate);
        int meetingHour = getMeetingHour(rMeetingTime);
        int meetingMinute = getMeetingMinute(rMeetingTime);

        int currYear = getCurrentYear(dateFormat.format(date));
        int currMonth = getCurrentMonth(dateFormat.format(date));
        int currDay = getCurrentDay(dateFormat.format(date));
        int currHour = getCurrentHour(dateFormat.format(date));
        int currMinute = getCurrentMinute(dateFormat.format(date));



        //This is beautiful but what does it all mean?
        if (currYear > meetingYear) return true;
        else if (currYear == meetingYear) {
            if (currMonth > meetingMonth) return true;
            else if (currMonth == meetingMonth) {
                if (currDay > meetingDay) return true;
                else if (currDay == meetingDay) {
                    if (currHour > meetingHour) return true;
                    else if (currHour == meetingHour) {
                        if (currMinute >= meetingMinute) return true;
                        else return false;
                    } else return false;
                } else return false;
            } else return false;
        } else return false;


    }


    //All of these get methods are to parse the strings given to get the current times and dates
    //They are all pretty self explanatory by looking at their names
    private int getMeetingYear(String date) {
        int firstDashIndexPlueOne = date.indexOf('-') + 1;
        String hold = date.substring(firstDashIndexPlueOne);
        int secondDashIndexPlusOne = hold.indexOf('-') + 1;
        String yearString = hold.substring(secondDashIndexPlusOne).trim();
        int year = Integer.parseInt(yearString);
        return year;
    }

    private int getMeetingDay(String date) {
        int firstDashIndexPlueOne = date.indexOf('-') + 1;
        String hold = date.substring(firstDashIndexPlueOne);
        String dayString = hold.substring(0, hold.indexOf('-')).trim();
        int day = Integer.parseInt(dayString);
        return day;
    }

    private int getMeetingMonth(String date) {
        return Integer.parseInt(date.substring(0, date.indexOf('-')).trim());
    }

    private int getMeetingHour(String time) {
        return Integer.parseInt(time.substring(0, time.indexOf(':')));
    }

    private int getMeetingMinute(String time) {
        return Integer.parseInt(time.substring(time.indexOf(':') + 1, time.length()));
    }

    private int getCurrentYear(String date) {
        int firstDashIndexPlueOne = date.indexOf('-') + 1;
        String hold = date.substring(firstDashIndexPlueOne);
        int secondDashIndexPlusOne = hold.indexOf('-') + 1;
        String yearString = hold.substring(secondDashIndexPlusOne, hold.indexOf(' ')).trim();
        int year = Integer.parseInt(yearString);
        return year;
    }

    private int getCurrentMonth(String date) {
        return Integer.parseInt(date.substring(0, date.indexOf('-')).trim());
    }

    private int getCurrentDay(String date) {
        int firstDashIndexPlueOne = date.indexOf('-') + 1;
        String hold = date.substring(firstDashIndexPlueOne);
        String dayString = hold.substring(0, hold.indexOf('-')).trim();
        int day = Integer.parseInt(dayString);
        return day;
    }

    private int getCurrentHour(String date) {
        return Integer.parseInt(date.substring(date.indexOf(' ') + 1, date.indexOf(':')));
    }

    private int getCurrentMinute(String date) {
        return Integer.parseInt(date.substring(date.indexOf(':') + 1, date.indexOf(':') + 3));
    }

    //The purpose of this is to change the time from 12 hour to 24 hour.
    private String reformatMeetingTime(String time) {
        String hourString;
        int hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
        String minute = time.substring(time.indexOf(':') + 1, time.indexOf(' '));
        String ampm = time.substring(time.indexOf(' ') + 1, time.indexOf('M'));

        if (ampm.equals("A")) {//AM
            if (hour == 12) hour = 0;
        } else {//PM
            if (hour == 12) hour = 12;
            else hour += 12;
        }
        hourString = Integer.toString(hour);

        return hourString + ":" + minute;
    }


}

