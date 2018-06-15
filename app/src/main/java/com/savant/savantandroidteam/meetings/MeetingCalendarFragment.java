package com.savant.savantandroidteam.meetings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;

import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

import static android.content.Context.MODE_PRIVATE;

public class MeetingCalendarFragment extends Fragment {

    CalendarView mCalendar;
    //TOOLBAR
    private ActionBar masterBarHolder;
    Toolbar toolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meetings_calendar, container, false);

        ((MainActivity) getActivity()).setTitle("Select Date");

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


        mCalendar = (CalendarView) view.findViewById(R.id.calendarView);

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = month+1 + "-" + dayOfMonth + "-" + year;
                SharedPreferences.Editor prefs = getContext().getSharedPreferences("MeetingPrefs", MODE_PRIVATE).edit();
                prefs.putString("date", date);
                prefs.apply();
                //Switch to meeting host fragment
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, new MeetingsHostFragment()).commit();


            }
        });

        return view;
    }





}
