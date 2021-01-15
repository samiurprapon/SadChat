package life.nsu.sadchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import life.nsu.sadchat.models.Chat;
import life.nsu.sadchat.models.User;
import life.nsu.sadchat.utils.adapters.MessageAdapter;

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

    String userId;
    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagingActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo
                // add notification here
                String message = mTextMessage.getText().toString();
                String time = String.valueOf(System.currentTimeMillis());

                if (!message.equals("")) {
                    sendMessage(firebaseUser.getUid(), userId, message, time);

                    // clear message box
                    mTextMessage.setText("");
                } else {
                    Toast.makeText(MessagingActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                mUsername.setText(user.getUsername());
                if (user.getImage() == null || user.getImage().equals("default") ) {
                    mProfilePicture.setImageResource(R.drawable.ic_profile_avatar);
                } else {
                    Glide.with(MessagingActivity.this)
                            .load(user.getBitmap())
                            .placeholder(R.drawable.ic_profile_avatar)
                            .circleCrop()
                            .into(mProfilePicture);
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

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
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

        //TODO
        // for notification purpose
        String encryptedMessage = message;

        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //TODO
                // for notification
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

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(user.getId()) || chat.getReceiver().equals(user.getId()) && chat.getSender().equals(firebaseUser.getUid())) {
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


    private void updateActiveStatus(String status) {
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> activeStatus = new HashMap<>();
        activeStatus.put("activeStatus", status);

        reference.updateChildren(activeStatus);
    }

    private void currentUser(String userId){
        SharedPreferences.Editor editor = getSharedPreferences("preference", MODE_PRIVATE).edit();
        editor.putString("currentUser", userId);
        editor.apply();
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
        updateActiveStatus("offline");
        currentUser(userId);

    }
}