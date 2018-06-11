package com.savant.savantandroidteam.meetings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

import static android.content.Context.MODE_PRIVATE;

public class MeetingTimeFragment extends Fragment {

    TimePicker mTimePicker;
    Button mSetTime;

    //TOOLBAR
    private ActionBar masterBarHolder;
    Toolbar toolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meetings_time, container, false);

        ((MainActivity) getActivity()).setTitle("Select Time");


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
                        .replace(R.id.fragment_container, new MeetingsHostFragment()).commit();
            }
        });

        mTimePicker = (TimePicker) view.findViewById(R.id.timeView);
        mSetTime = (Button) view.findViewById(R.id.button_set_time);

        mSetTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Switch to meeting host Fragment
                String minString = "00";
                int hour = mTimePicker.getCurrentHour();
                if(hour>12) hour = hour %12;
                if(hour == 0) hour = 12;

                int minute = mTimePicker.getCurrentMinute();
                if(minute < 10) minString = "0" + Integer.toString(minute);
                else minString = Integer.toString(minute);
                String ampm;
                if(mTimePicker.getCurrentHour() > 11) ampm = "PM";
                else ampm = "AM";
                String time = hour + ":" + minString + " " + ampm;

                SharedPreferences.Editor prefs = getContext().getSharedPreferences("MeetingPrefs", MODE_PRIVATE).edit();
                prefs.putString("time", time);
                prefs.apply();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, new MeetingsHostFragment()).commit();
            }
        });


        return view;


    }
}
