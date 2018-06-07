package com.savant.savantandroidteam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.savant.savantandroidteam.meetings.MeetingsMainFragment;
import com.savant.savantandroidteam.poker.PokerMainFragment;
import com.savant.savantandroidteam.startup.LoginActivity;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //UI
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private ActionBarDrawerToggle mDrawerToggle;

    //Firebase
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Firebase
        mAuth = FirebaseAuth.getInstance();


        //Set up the drawer
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
        //Set up drawer header
        setHeaderInfo();


        //If there was no saved fragment when the app was closed it will load up the getting started
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GettingStartedFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_getting_started);
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

    private void setHeaderInfo(){
        navView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navView.getHeaderView(0);
        TextView userNameView = (TextView) hView.findViewById(R.id.tv_name_drawer_header);
        TextView emailView = (TextView) hView.findViewById(R.id.tv_email_drawer_header);

        String fullName = getFullName();
        String email = getEmail();

        userNameView.setText(fullName);
        emailView.setText(email);

    }

    //Will get the users name based off of their email address
    //Limited to emails that are in the format 'first.last@email.com'
    private String getFullName(){
        String email = mAuth.getCurrentUser().getEmail();
        String first = email.substring(0, email.indexOf("."));
        String firstName = first.substring(0, 1).toUpperCase() + first.substring(1);
        String last = email.substring(email.indexOf(".")+1, email.indexOf('@'));
        String lastName = last.substring(0, 1).toUpperCase() + last.substring(1);
        return firstName + " " + lastName;
    }

    //Returns the users Email from Firebase
    private String getEmail(){
        return mAuth.getCurrentUser().getEmail().trim();
    }

    //Phone back button pressed will not work except to close drawer
    //Looking into proper navigation for the entire app!
    @Override
    public void onBackPressed(){
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else{
        }

    }




}
