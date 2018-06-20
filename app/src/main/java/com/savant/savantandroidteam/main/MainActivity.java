package com.savant.savantandroidteam.main;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savant.savantandroidteam.R;
import com.savant.savantandroidteam.gettingstarted.GettingStartedFragment;
import com.savant.savantandroidteam.meetings.MeetingsMainFragment;
import com.savant.savantandroidteam.poker.PokerMainFragment;
import com.savant.savantandroidteam.profile.ProfileFragment;
import com.savant.savantandroidteam.startup.LoginActivity;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //UI Declarations
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private ActionBarDrawerToggle mDrawerToggle;
    public Toolbar toolbar;

    //Firebase Declarations
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRootRef;
    DatabaseReference mUserRef;
    public String imageID;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null)System.out.println(savedInstanceState.toString());

        //Firebase initializations
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference("users");
        mUserRef = mRootRef.child(getModifiedEmail());
        addUserToDatabase();

        setUpToolbar("Savant Android Wiki Links");

        //If there was no saved fragment when the app was closed it will load up the getting started fragment
        if(savedInstanceState == null) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GettingStartedFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_getting_started);
        }

        //When the nickname and profile picture are loaded this is called
        mUserRef.addValueEventListener(new ValueEventListener() {

            boolean proPicSet = false;
            boolean nicknameSet = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iter = dataSnapshot.getChildren();
                for(DataSnapshot id: iter){
                    if(id.getKey().equals("picture")){
                        setProfilePic(id.getValue().toString());
                        proPicSet = true;
                    }
                    else if(id.getKey().equals("nickname")){
                        setNickname(id.getValue().toString());
                        nicknameSet = true;
                    }
                }
                if(!proPicSet) setProfilePic("15");
                if(!nicknameSet) setNickname(getFirstName());


            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }


    /**
     *
     * @param title The title for the toolbar
     * This will set the title for the toolbar in whichever fragment
     * or activity calls it
     */
    public void setUpToolbar(String title){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().show();

        getSupportActionBar().setTitle(title);


        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { }
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}
            @Override
            public void onDrawerStateChanged(int newState) {}
        });
        //Set up drawer email
        setEmail();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //CLICK LISTENERS for the Drawer Items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_getting_started:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GettingStartedFragment()).commit();
                break;
            case R.id.nav_poker:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PokerMainFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_sprint:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MeetingsMainFragment()).commit();
                break;
            case R.id.nav_log_out:
                signOutOfAccount();
                Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show();
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Drawer Logic and Layout
    private void signOutOfAccount(){
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }


    private void setEmail(){
        navView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navView.getHeaderView(0);
        ImageView profileImage = (ImageView) hView.findViewById(R.id.iv_image);
        TextView emailView = (TextView) hView.findViewById(R.id.tv_email_drawer_header);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });

        //the fullname is set in the nickname listener
        String email = getEmail();
        emailView.setText(email);

    }



    /**
     * @return the users first and last name for the header
     * Will get the users name based off of their email address
     * Limited to emails that are in the format 'first.last@email.com'
     */
    private String getFullName(){
        String email = mAuth.getCurrentUser().getEmail();
        String firstName = getFirstName();
        String last = email.substring(email.indexOf(".")+1, email.indexOf('@'));
        String lastName = last.substring(0, 1).toUpperCase() + last.substring(1);
        return firstName + " " + lastName;
    }


    /**
     *
     * @return the first name of the user taken from their email
     */
    private String getFirstName(){
        String email = getEmail();
        String first = email.substring(0, email.indexOf("."));
        String firstName = first.substring(0, 1).toUpperCase() + first.substring(1);
        return firstName;
    }

    /**
     * @return the users Email from Firebase
     */
    private String getEmail(){
        return mAuth.getCurrentUser().getEmail().trim();
    }


    /**
     *
     * @return users Email from Firebase
     */
    private String getModifiedEmail(){
        return mAuth.getCurrentUser().getEmail().trim().replace('.',',');
        //TESTER comments
        //hi
    }



    /**
     * Phone back button pressed will not work except to close drawer
     * Looking into proper navigation for the entire app!
     */
    @Override
    public void onBackPressed(){
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        } else if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else{}
    }


    /**
     * The int array for all the profile pictures
     */
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
     * Adds users name and email to the database
     */
    private void addUserToDatabase(){
        mUserRef.child("name").setValue(getFullName());
        mUserRef.child("email").setValue(getEmail());
    }

    /**
     *
     * @param picId the position of the profile picture
     */
    private void setProfilePic(String picId){
        navView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navView.getHeaderView(0);
        ImageView profileImage = (ImageView) hView.findViewById(R.id.iv_image);
        profileImage.setImageResource(mThumbIds[Integer.parseInt(picId)]);
    }

    /**
     *
     * @param nickname the users set nickname.
     * Will set the full name and nickname given to the header
     *
     */
    private void setNickname(String nickname){
        navView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navView.getHeaderView(0);
        TextView name = (TextView) hView.findViewById(R.id.tv_name_drawer_header);
        String currName = getFullName();
        String holder = currName + " (" + nickname + ")";
        name.setText(holder);
    }









}
