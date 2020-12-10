package com.shubham.blogappfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shubham.blogappfirebase.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

private ActivitySplashBinding activitySplashBinding;

private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding=ActivitySplashBinding.inflate(getLayoutInflater());
        View view=activitySplashBinding.getRoot();
        setContentView(view);

     //fullscreencode
     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

     firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        setTimerForSplashScreen();
    }

    private void setTimerForSplashScreen()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (firebaseUser!=null)
                {
                    Intent intent=new Intent(SplashActivity.this,VideoActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {

                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
}
