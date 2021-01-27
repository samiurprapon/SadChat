package life.nsu.sadchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import life.nsu.sadchat.models.User;


public class SplashActivity extends AppCompatActivity {

    //    private ProgressBar mProgressBar;
    DatabaseReference reference;
    ValueEventListener existenceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prevent screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // initialize firebase
        FirebaseApp.initializeApp(this);
        // UI screen
        setContentView(R.layout.activity_splash);


//        mProgressBar = findViewById(R.id.progressBar);

        // pause for 450 milli seconds
        new Handler(Looper.myLooper()).postDelayed(this::initialize, 550);
    }

    //  Checking either logged in or a new user
    private void initialize() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // when user is logged in this block performs

            reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid());

            existenceListener = reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Intent intent;

                    if(user != null) {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, CreateProfileActivity.class);
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // check internet connection and retry
                }
            });

        } else {
            // when user is not logged in this block performs
            Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(reference != null && existenceListener != null) {
            reference.removeEventListener(existenceListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(reference != null && existenceListener != null) {
            reference.removeEventListener(existenceListener);
        }
    }
}