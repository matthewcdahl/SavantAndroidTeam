package com.savant.savantandroidteam.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.MainActivity;
import com.savant.savantandroidteam.R;

public class ProfileFragment extends Fragment {

    //UI Declarations
    Button editPicBtn;
    TextView mName;
    TextView mEmail;
    ImageView mImage;
    EditText mNickname;

    //Firebase Declarations
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference mUsersRef;
    DatabaseReference mUserRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(false);
        ((MainActivity)getActivity()).setUpToolbar("Profile");

        //Firebase Initializations
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        mUsersRef = db.getReference("users");
        mUserRef = mUsersRef.child(getModifiedEmail());

        //UI Initializations
        mName = (TextView) view.findViewById(R.id.profile_name);
        mEmail = (TextView) view.findViewById(R.id.profile_email);
        mImage = (ImageView) view.findViewById(R.id.profile_img);
        mNickname = (EditText) view.findViewById(R.id.et_profile_nickName);
        mNickname.setCursorVisible(false);
        editPicBtn = view.findViewById(R.id.edit_btn_profile);

        //Switch to Profile Icons Fragment if clicked
        editPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((MainActivity) getActivity()).getSupportActionBar().hide();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                ProfileIconsFragment fragment = new ProfileIconsFragment();
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack(null).commit();
            }
        });

        setInfo();

        mNickname.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitNickname();
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });


        initiateListeners();


        return view;
    }

    /**
     *
     * These methods will initialize the menu for submitting nickname
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.profile_submit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.submit_nickname:
                submitNickname();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Load the info from firebase to populate cells
     */
    private void setInfo(){
        mUserRef.addValueEventListener(new ValueEventListener() {
            boolean setProPic = false;
            boolean setNickname = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               Iterable<DataSnapshot> iter = dataSnapshot.getChildren();
               for(DataSnapshot id: iter){
                   if(id.getKey().equals("picture")){
                       mImage.setImageResource(mThumbIds[Integer.parseInt(id.getValue().toString())]);
                       setProPic = true;
                   }
                   else if(id.getKey().equals("nickname")){
                       mNickname.setText(id.getValue().toString());
                       setNickname = true;
                   }
               }
               if(!setProPic)mImage.setImageResource(mThumbIds[15]);
               if(!setNickname)mNickname.setText(getFirstName());


            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        String name = getName();
        String email = getEmail();

        mName.setText(name);
        mEmail.setText(email);
    }

    /**
     *
     * @return Full name of the user
     */
    private String getName(){
        String email = mAuth.getCurrentUser().getEmail();
        String last = email.substring(email.indexOf('.')+1, email.indexOf('@'));
        last = last.substring(0,1).toUpperCase() + last.substring(1);
        String first = getFirstName();
        return first + " " + last;
    }

    /**
     * @return the email of the user
     */
    private String getEmail(){
        return mAuth.getCurrentUser().getEmail();
    }

    /**
     *
     * @return first name of the user
     */
    private String getFirstName(){
        String email = mAuth.getCurrentUser().getEmail();
        String first = email.substring(0, email.indexOf('.'));
        first = first.substring(0,1).toUpperCase() + first.substring(1);
        return first;
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
     *
     * @return the users email with '.' replaced to ',' for firebase purposes
     */
    private String getModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
    }

    /**
     * Submit the nickmane to firebase and do error checking
     */
    private void submitNickname(){
        String nickname = mNickname.getText().toString().trim();
        mNickname.setText(nickname);
        if(nickname.length()==0 || nickname.length() > 14){
            Toast.makeText(getContext(), "Invalid Length must be 1-14 characters, Emoji's count as 2", Toast.LENGTH_LONG).show();
        }
        else{
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            setHasOptionsMenu(false);
            //Submit the nickname to firebase
            Toast.makeText(getContext(), "Submitted! Your nickname is used for Poker", Toast.LENGTH_LONG).show();
            mUserRef.child("nickname").setValue(nickname);
            mNickname.setCursorVisible(false);
        }
    }


    /**
     * initiate the click listener for entering a nickname
     */
    private void initiateListeners(){
        mNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHasOptionsMenu(true);
                mNickname.setCursorVisible(true);
            }
        });
    }


}
