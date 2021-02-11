package life.nsu.sadchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import life.nsu.sadchat.models.User;
import life.nsu.sadchat.utils.CustomLoader;

public class VerificationActivity extends AppCompatActivity {

    EditText mVerificationCode;
    Button mVerify;

    private String verificationId;

    private FirebaseAuth mAuth;
    private CustomLoader progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        progressBar = new CustomLoader(this);
        mAuth = FirebaseAuth.getInstance();

        mVerificationCode = findViewById(R.id.et_verification_code);
        mVerify = findViewById(R.id.btn_verification);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        request(phoneNumber);

        mVerify.setOnClickListener(v -> {
            String code = mVerificationCode.getText().toString();

            if (!otpValidation(code)) {
                mVerificationCode.setError("incomplete");
                return;
            }
            progressBar.show();

            verify(code);

        });
    }

    public boolean otpValidation(String code) {
        return code.length() == 6;
    }

    // verifying code from Fire base server
    public void verify(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        new Handler(Looper.myLooper()).postDelayed(() -> {
                            progressBar.hide();
                            route();
                        }, 550);

                    } else {
                        progressBar.hide();
                        Toast.makeText(VerificationActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void request(String number) {

        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions
                        .newBuilder(FirebaseAuth.getInstance())
                        .setActivity(this)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setCallbacks(mCallBack)
                        .build());
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String string, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(string, forceResendingToken);
            verificationId = string;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                mVerificationCode.setText(code);
                verify(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void route() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = null;

        if (uid != null) {
            reference = FirebaseDatabase.getInstance().getReference("users").child(uid);
        }

        if (reference != null) {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Intent intent;

                    if (user == null || user.getUsername() == null || user.getUsername().isEmpty() || user.getImage().equals("default")) {
                        intent = new Intent(VerificationActivity.this, CreateProfileActivity.class);
                    } else {
                        intent = new Intent(VerificationActivity.this, MainActivity.class);
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
        }

    }


}