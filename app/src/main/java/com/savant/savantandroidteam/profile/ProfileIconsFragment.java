package com.savant.savantandroidteam.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.savant.savantandroidteam.main.MainActivity;
import com.savant.savantandroidteam.R;

public class ProfileIconsFragment extends Fragment {

    //UI Declarations
    private GridView gridView;
    private NavigationView navView;

    //Firebase Declarations
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mUsersRef;
    private DatabaseReference mUserRef;

    //TOOLBAR Declarations
    private ActionBar masterBarHolder;
    private Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_icons, container, false);
        ((MainActivity) getActivity()).setTitle("Select Icon");

        //Firebase initializations
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mUsersRef = database.getReference("users");
        mUserRef = mUsersRef.child(getModifiedEmail());

        //TOOLBAR initialization
        masterBarHolder = ((MainActivity) getActivity()).getSupportActionBar();
        masterBarHolder.hide();
        toolbar = view.findViewById(R.id.toolbar_with_back);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment()).commit();            }
        });


        //UI Initializations
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(getContext()));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mUserRef.child("picture").setValue(Integer.toString(position));
                //SharedPreferences.Editor prefs = getContext().getSharedPreferences("Profile", Context.MODE_PRIVATE).edit();
                //prefs.putInt("pictureID", position);
                //prefs.apply();
                setHeaderInfo(position);
                ProfileFragment fragment = new ProfileFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack(null).commit();
                Toast.makeText(getContext(), "Profile Picture Changed", Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }


    /**
     *
     * @param pos position of the picture logo
     */
    private void setHeaderInfo(int pos) {
        navView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View hView = navView.getHeaderView(0);
        ImageView profileImage = (ImageView) hView.findViewById(R.id.iv_image);
        int img = getProfileImage(pos);
        profileImage.setImageResource(img);
    }

    /**
     *
     * @param pos
     * @return the image drawable from the given pos.
     */
    private int getProfileImage(int pos) {
        return mThumbIds[pos];
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

    /**
     *@return users Email from Firebase with the ','
     */
    private String getModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
    }
}
