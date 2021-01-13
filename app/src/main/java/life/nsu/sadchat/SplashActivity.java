package life.nsu.sadchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class SplashActivity extends AppCompatActivity {

//    private ProgressBar mProgressBar;
    private SharedPreferences preferences;
    private boolean isProfileCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // initialize firebase
        FirebaseApp.initializeApp(this);
        // UI screen
        setContentView(R.layout.activity_splash);

        preferences = getApplication().getApplicationContext().getSharedPreferences("status", Context.MODE_PRIVATE);
        isProfileCompleted = preferences.getBoolean("isCompleted", false);

//        mProgressBar = findViewById(R.id.progressBar);

        // pause for 450 milli seconds
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        }, 550);
    }

    //  Checking either logged in or a new user
    private void initialize() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            // when user is logged in this block performs

            if(isProfileCompleted) {
                // if profile not complete then intent to profile setting
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                // if profile not complete then intent to profile setting
                Intent intent = new Intent(SplashActivity.this, CreateProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        } else {
            // when user is not logged in this block performs
            Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        finish();
    }
}