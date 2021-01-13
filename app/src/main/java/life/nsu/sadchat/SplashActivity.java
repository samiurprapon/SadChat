package life.nsu.sadchat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class SplashActivity extends AppCompatActivity {

//    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_splash);

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

        } else {
            // when user is not logged in this block performs
        }

//        finish();
    }
}