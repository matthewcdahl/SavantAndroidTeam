package com.savant.savantandroidteam.startup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.savant.savantandroidteam.R;

public class SplashActivity extends AppCompatActivity {


    //Declarations

    //Animaitons
    Animation mSlideRight;
    Animation mSlideLeft;
    Animation mWait;

    //Images
    ImageView mSavantLogo;
    ImageView mAndroidLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        //Initializations

        //Animations
        mSlideRight = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_right);
        mSlideLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_left);
        mWait = AnimationUtils.loadAnimation(getBaseContext(), R.anim.wait);

        //Images
        mSavantLogo = (ImageView) findViewById(R.id.savant_logo);
        mAndroidLogo = (ImageView) findViewById(R.id.android_logo);

        //Play the animations to load in
        mSavantLogo.startAnimation(mSlideRight);
        mAndroidLogo.startAnimation(mSlideLeft);


        //Listeners for the animations on events
        mSlideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                //Play the animation to wait: 800 (edit in R.anim.wait)
                mSavantLogo.startAnimation(mWait);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mWait.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });


    }
}
