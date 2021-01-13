package life.nsu.sadchat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void updateActiveStatus(String status) {
        String uid = user.getUid();

        reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

        HashMap<String, Object> activeStatus = new HashMap<>();
        activeStatus.put("activeStatus", status);

        reference.updateChildren(activeStatus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActiveStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateActiveStatus("offline");
    }
}