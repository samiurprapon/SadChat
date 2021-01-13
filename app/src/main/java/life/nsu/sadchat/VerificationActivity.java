package life.nsu.sadchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import life.nsu.sadchat.utils.CustomProgressBar;

public class VerificationActivity extends AppCompatActivity {

    EditText mVerificationCode;
    Button mVerify;

    private String verificationId;

    private FirebaseAuth mAuth;
    private CustomProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        progressBar = new CustomProgressBar(this);
        mAuth = FirebaseAuth.getInstance();

        mVerificationCode = findViewById(R.id.et_verification_code);
        mVerify = findViewById(R.id.btn_verification);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        request(phoneNumber);

        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mVerificationCode.getText().toString();

                if (!otpValidation(code)) {
                    mVerificationCode.setError("incomplete");
                    return;
                }
                progressBar.show("");

                verify(code);

            }
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
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.hide();
                                    route();
                                }
                            }, 550);

                        } else {
                            progressBar.hide();
                            Toast.makeText(VerificationActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
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
            Log.d("onVerificationFailed", e.getMessage());
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
        Intent intent = new Intent(VerificationActivity.this, CreateProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

}