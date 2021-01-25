package life.nsu.sadchat.utils.fcm;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateToken(token);
        }

    }

    private void updateToken(String refreshToken) {
        FirebaseDatabase.getInstance().getReference("tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new Token(refreshToken));
    }
}
