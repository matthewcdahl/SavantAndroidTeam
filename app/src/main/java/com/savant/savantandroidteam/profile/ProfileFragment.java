package com.savant.savantandroidteam.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;
import com.savant.savantandroidteam.meetings.MeetingsHostFragment;

public class ProfileFragment extends Fragment {

    Button editPic;
    TextView mName;
    TextView mEmail;
    ImageView mImage;

    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mName = (TextView) view.findViewById(R.id.profile_name);
        mEmail = (TextView) view.findViewById(R.id.profile_email);
        mImage = (ImageView) view.findViewById(R.id.profile_img);

        ((MainActivity) getActivity()).setTitle("Profile");

        editPic = view.findViewById(R.id.edit_btn_profile);

        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileIconsFragment fragment = new ProfileIconsFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack(null).commit();
            }
        });

        setInfo();



        return view;
    }

    private void setInfo(){
        String name = getName();
        String email = getEmail();
        int image = mThumbIds[getImage()];

        mName.setText(name);
        mEmail.setText(email);
        mImage.setImageResource(image);
    }

    private String getName(){
        String email = mAuth.getCurrentUser().getEmail();
        String first = email.substring(0, email.indexOf('.'));
        String last = email.substring(email.indexOf('.')+1, email.indexOf('@'));
        first = first.substring(0,1).toUpperCase() + first.substring(1);
        last = last.substring(0,1).toUpperCase() + last.substring(1);
        return first + " " + last;
    }

    private String getEmail(){
        return mAuth.getCurrentUser().getEmail();
    }
    private int getImage(){
        SharedPreferences prefs = getContext().getSharedPreferences("Profile", Context.MODE_PRIVATE);
        return prefs.getInt("pictureID", 16);
    }


    private Integer[] mThumbIds = {
            R.drawable.profile_icon_1, R.drawable.profile_icon_2, R.drawable.profile_icon_3,
            R.drawable.profile_icon_4, R.drawable.profile_icon_5, R.drawable.profile_icon_6,
            R.drawable.profile_icon_7, R.drawable.profile_icon_8, R.drawable.profile_icon_9,
            R.drawable.profile_icon_10, R.drawable.profile_icon_11, R.drawable.profile_icon_12,
            R.drawable.profile_icon_13, R.drawable.profile_icon_14, R.drawable.profile_icon_15,
            R.drawable.profile_icon_16, R.drawable.profile_icon_17, R.drawable.profile_icon_18,
            R.drawable.profile_icon_19, R.drawable.profile_icon_20, R.drawable.profile_icon_21,
            R.drawable.profile_icon_22, R.drawable.profile_icon_23, R.drawable.profile_icon_24,
            R.drawable.profile_icon_25, R.drawable.profile_icon_26, R.drawable.profile_icon_27,
            R.drawable.profile_icon_28, R.drawable.profile_icon_29, R.drawable.profile_icon_30,
            R.drawable.profile_icon_31, R.drawable.profile_icon_32, R.drawable.profile_icon_33,
            R.drawable.profile_icon_34, R.drawable.profile_icon_35, R.drawable.profile_icon_36,
            R.drawable.profile_icon_37, R.drawable.profile_icon_38, R.drawable.profile_icon_39,
            R.drawable.profile_icon_40, R.drawable.profile_icon_41, R.drawable.profile_icon_42,
            R.drawable.profile_icon_43, R.drawable.profile_icon_44, R.drawable.profile_icon_45,
            R.drawable.profile_icon_46, R.drawable.profile_icon_47, R.drawable.profile_icon_48,
            R.drawable.profile_icon_49, R.drawable.profile_icon_50
    };


}
