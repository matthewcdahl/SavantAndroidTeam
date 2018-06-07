package com.savant.savantandroidteam.startup;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.savant.savantandroidteam.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView mEmail;
    private Button mSendReset;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmail = (TextView) findViewById(R.id.et_forgot_password_email);
        mSendReset = (Button) findViewById(R.id.btn_send_reset_email);

        mSendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetEmail();
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    private void sendResetEmail(){
        final String email = mEmail.getText().toString();
        if(!email.isEmpty()) {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        String toastText = "Reset password instructions sent to: " + email;
                        Toast.makeText(ForgotPasswordActivity.this, toastText, Toast.LENGTH_LONG).show();
                        mEmail.setText("");
                    } else {
                        String toastText = email + " not found. Please Contact Admin.";
                        Toast.makeText(ForgotPasswordActivity.this, toastText, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            String toastText = "Must Enter an Email!";
            Toast.makeText(ForgotPasswordActivity.this, toastText, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }


}
