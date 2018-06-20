package com.savant.savantandroidteam.gettingstarted;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.savant.savantandroidteam.main.MainActivity;
import com.savant.savantandroidteam.R;

public class GettingStartedFragment extends Fragment {

    //UI Declarations
    private Button mHomepage;
    private Button mDeveloping;
    private Button mScrum;
    private Button mRules;
    private Button mProblem;
    private Button mMVP;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_getting_started, container, false);
        ((MainActivity)getActivity()).setUpToolbar("Savant Android Wiki Links");

        //UI Initializations
        mHomepage = view.findViewById(R.id.homepage_btn);
        mDeveloping = view.findViewById(R.id.start_dev_btn);
        mScrum = view.findViewById(R.id.scrum_btn);
        mRules = view.findViewById(R.id.rules_btn);
        mProblem = view.findViewById(R.id.problem_btn);
        mMVP = view.findViewById(R.id.mvp_btn);

        //Button Listeners
        setupListeners();

        return view;
    }

    /**
     * This will listen for any clicks on the links and will then
     * redirect to the appropriate webpage
     */
    private void setupListeners(){
        mHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://wiki.savant.com/display/ENG/Android"));
                startActivity(browserIntent);
            }
        });
        mDeveloping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://wiki.savant.com/display/ENG/Getting+Started+with+Development"));
                startActivity(browserIntent);
            }
        });
        mScrum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://wiki.savant.com/pages/viewpage.action?pageId=3382357"));
                startActivity(browserIntent);
            }
        });
        mRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://wiki.savant.com/display/ENG/Rules%2C+Standards%2C+Known+Issues%2C+FQA"));
                startActivity(browserIntent);
            }
        });
        mProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://wiki.savant.com/display/ENG/Guide+to+problem+solving"));
                startActivity(browserIntent);
            }
        });
        mMVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://wiki.savant.com/display/ENG/MVP+Design"));
                startActivity(browserIntent);
            }
        });
    }


}
