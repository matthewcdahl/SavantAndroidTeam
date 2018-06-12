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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;
import com.savant.savantandroidteam.meetings.MeetingsHostFragment;

public class ProfileFragment extends Fragment {

    Button editPic;
    TextView mName;
    TextView mEmail;
    ImageView mImage;

    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference mUsersRef;
    DatabaseReference mUserRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ((MainActivity)getActivity()).setUpToolbar("Profile");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        mUsersRef = db.getReference("users");
        mUserRef = mUsersRef.child(getModifiedEmail());

        mName = (TextView) view.findViewById(R.id.profile_name);
        mEmail = (TextView) view.findViewById(R.id.profile_email);
        mImage = (ImageView) view.findViewById(R.id.profile_img);

        ((MainActivity) getActivity()).setTitle("Profile");

        editPic = view.findViewById(R.id.edit_btn_profile);

        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((MainActivity) getActivity()).getSupportActionBar().hide();
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
        mUserRef.addValueEventListener(new ValueEventListener() {


            //Not Working
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               Iterable<DataSnapshot> iter = dataSnapshot.getChildren();
               for(DataSnapshot id: iter){
                   if(id.getKey().equals("picture")){
                       mImage.setImageResource(mThumbIds[Integer.parseInt(id.getValue().toString())]);
                       break;
                   }

                   else mImage.setImageResource(mThumbIds[15]);
               }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        String name = getName();
        String email = getEmail();

        mName.setText(name);
        mEmail.setText(email);
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

    private String getModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
    }


}
