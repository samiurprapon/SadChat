package life.nsu.sadchat;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import life.nsu.sadchat.models.Chat;
import life.nsu.sadchat.models.User;
import life.nsu.sadchat.utils.adapters.MessageAdapter;
import life.nsu.sadchat.utils.fcm.Client;
import life.nsu.sadchat.utils.fcm.MyResponse;
import life.nsu.sadchat.utils.fcm.Notification;
import life.nsu.sadchat.utils.fcm.NotificationService;
import life.nsu.sadchat.utils.fcm.Sender;
import life.nsu.sadchat.utils.fcm.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    CircleImageView mProfilePicture;
    TextView mUsername;
    ImageButton mSend;
    EditText mTextMessage;
    RecyclerView recyclerView;

    MessageAdapter adapter;
    List<Chat> chats;

    ValueEventListener seenListener;
    NotificationService service;

    private String userId;
    private boolean isNotify = false;
//    SharedPreferences preferences;

    private URL url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prevent screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        getWindow().setBackgroundDrawableResource(R.drawable.ic_chat_background);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_messaging);

//        preferences = getSharedPreferences("personal", MODE_PRIVATE);
//        String imageUrl = preferences.getString("profile", "https://i.imgur.com/3ENZDMZ.jpeg");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        service = Client.getInstance("https://fcm.googleapis.com/").create(NotificationService.class);

        if (getIntent() != null) {
            userId = getIntent().getStringExtra("userId");
        }

        // ui elements
        mProfilePicture = findViewById(R.id.profile_image);
        mUsername = findViewById(R.id.tv_username);
        mSend = findViewById(R.id.btn_send);
        mTextMessage = findViewById(R.id.et_message);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mSend.setOnClickListener(view -> {
            isNotify = true;

            String message = mTextMessage.getText().toString();
            String time = String.valueOf(System.currentTimeMillis());

            if (!message.equals("")) {
                sendMessage(firebaseUser.getUid(), userId, message, time);

                // clear message box
                mTextMessage.setText("");
            } else {
                Toast.makeText(MessagingActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }

        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    mUsername.setText(user.getUsername());
                }

                if (user != null) {
                    if (user.getImage() == null || user.getImage().equals("default")) {
                        mProfilePicture.setImageResource(R.drawable.ic_profile_avatar);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(user.getImage())
                                .placeholder(R.drawable.ic_profile_avatar)
                                .circleCrop()
                                .into(mProfilePicture);
                    }
                }

                readMessages(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userId);
    }

    private void seenMessage(final String userId) {
        reference = FirebaseDatabase.getInstance().getReference("chats");
        String time = String.valueOf(System.currentTimeMillis());

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null && chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        hashMap.put("seenTime", time);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String sender, final String receiver, String message, String time) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);
        hashMap.put("time", time);

        reference.child("chats").push().setValue(hashMap);

        // add user to chat fragment
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("chatList")
                .child(userId)
                .child(firebaseUser.getUid());
        chatRefReceiver.child("id").setValue(firebaseUser.getUid());

        // later encrypt the message here
        final String encryptedMessage = message;

        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (isNotify) {
                    if (user != null) {
                        sendNotification(receiver, user.getUsername(), encryptedMessage);
                    }
                }

                isNotify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(User user) {
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null && (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(user.getId()) || chat.getReceiver().equals(user.getId()) && chat.getSender().equals(firebaseUser.getUid()))) {
                        chats.add(chat);
                    }

                    adapter = new MessageAdapter(MessagingActivity.this, chats, user);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);

                    Notification notification = new Notification(firebaseUser.getUid(), R.drawable.ic_profile_avatar, username + ": " + message, "New Message", userId);
                    Sender sender = null;

                    if (token != null) {
                        sender = new Sender(notification, token.getToken());
                    }

                    service.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

//                            if (response.code() == 200 && response.body() != null) {
////                                MyResponse response1 = response.body();
////                                Log.d("FIREBASE_RESP_Success", response1.toString());
//
//                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
//                            Log.d("FIREBASE_RESP_fail", t.getMessage());

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateActiveStatus(String status) {
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> activeStatus = new HashMap<>();
        activeStatus.put("activeStatus", status);

        reference.updateChildren(activeStatus);
    }

    private void currentUser(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("preference", MODE_PRIVATE).edit();
        editor.putString("currentUser", userId);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messaging_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.audio_call:
                // redirect into audioCalling option
                voiceCall();
                return true;
            case R.id.video_call:
                // redirect into videoCalling option
                videoCall();
                return false;
        }

        return false;
    }

    private void initializeJitsi() {
        try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            url = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(url)
                .setWelcomePageEnabled(false)
                .setFeatureFlag("add-people.enabled", false)
                .setFeatureFlag("kick-out.enabled", false)
                .setFeatureFlag("raise-hand.enabled", false)
                .setFeatureFlag("recording.enabled", false)
                .setFeatureFlag("meeting-password.enabled", false)
                .setFeatureFlag("live-streaming.enabled", false)
                .setFeatureFlag("filmstrip.enabled", false)
                .setFeatureFlag("meeting-name.enabled", false)
                .setFeatureFlag("chat.enabled", false)
                .setFeatureFlag("invite.enabled", false)
                .setFeatureFlag("overflow-menu.enabled", false)
                .build();

        JitsiMeet.setDefaultConferenceOptions(options);
    }

    private void videoCall() {
        initializeJitsi();

        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(generateToken())
                .build();

        JitsiMeetActivity.launch(this, options);

    }

    private String generateToken() {
        String myUid = FirebaseAuth.getInstance().getUid();
        String friendUid = userId;
        String token = "null";

        if (myUid != null) {
            if (myUid.hashCode() < friendUid.hashCode()) {
                token = myUid + friendUid;
            } else {
                token = friendUid + myUid;
            }
        }

        return token;

    }

    private void voiceCall() {
        initializeJitsi();

        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(generateToken())
                .setAudioOnly(true)
                .build();

        JitsiMeetActivity.launch(this, options);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActiveStatus("online");
        currentUser(userId);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (reference != null && seenListener != null) {
            reference.removeEventListener(seenListener);
        }

        updateActiveStatus("offline");
        currentUser(userId);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (reference != null && seenListener != null) {
            reference.removeEventListener(seenListener);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}