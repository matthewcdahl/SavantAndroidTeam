package com.savant.savantandroidteam.startup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.savant.savantandroidteam.main.MainActivity;
import com.savant.savantandroidteam.R;

public class LoginActivity extends AppCompatActivity {

    //UI Declarations
    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginBtn;
    private Button mForgotPasswordBtn;

    //Firebase Declarations
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //UI Initializations
        mEmail = (EditText) findViewById(R.id.et_email);
        mPassword = (EditText) findViewById(R.id.et_password);
        mLoginBtn = (Button) findViewById(R.id.button_login);
        mForgotPasswordBtn = (Button) findViewById(R.id.button_forgot_password);

        //Firebase Initialzation
        mAuth = FirebaseAuth.getInstance();

        //Button Listeners
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                startSignIn();
            }
        });
        mForgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });


        //Will automatically log in a user if they have logged in berfore.
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };




    }

    /**
     * add the listener to mAuth on start of activity
     */
    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Does not allow use of the back button
     */
    @Override
    public void onBackPressed(){}


    /**
     * Will sign the user in with correct credentials
     */
    private void startSignIn(){

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Wrong Credentials!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(LoginActivity.this, "Must Fill in Both Fields!", Toast.LENGTH_LONG).show();
        }

    }

}
