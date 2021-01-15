package life.nsu.sadchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import life.nsu.sadchat.utils.CustomLoader;

public class AuthenticationActivity extends AppCompatActivity {

    EditText mCountryCode;
    EditText mPhone;
    AppCompatButton mLogin;

    CustomLoader progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prevent screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_authentication);

        mCountryCode = findViewById(R.id.et_country);
        mPhone = findViewById(R.id.et_phone);
        mLogin = findViewById(R.id.btn_login);

        progressBar = new CustomLoader(this);

        mLogin.setOnClickListener(v -> {
            final String phoneNumber = mPhone.getText().toString().trim();
            final String code = mCountryCode.getText().toString();

            if (phoneValidation(phoneNumber) && countryCodeValidation(code + phoneNumber)) {
                progressBar.show();

                new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AuthenticationActivity.this, VerificationActivity.class);
                        intent.putExtra("phoneNumber", code+phoneNumber);

                        Log.d("PhoneNumber", code+phoneNumber);

                        progressBar.hide();
                        startActivity(intent);
                    }
                }, 200);

            } else {
                if (!countryCodeValidation(code)) {
                    mCountryCode.setError("invalid");
                } else {
                    mPhone.setError("invalid");
                }
            }
        });
    }

    public boolean phoneValidation(@NonNull String phone) {
        return phone.length() == 11 || phone.length() == 10 || phone.length() == 9;
    }

    public boolean countryCodeValidation(@NonNull String code) {

        Pattern p = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$");

        Matcher m = p.matcher(code);
        return (m.find() && m.group().equals(code));
    }
}