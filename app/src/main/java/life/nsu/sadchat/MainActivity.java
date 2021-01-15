package life.nsu.sadchat;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import life.nsu.sadchat.models.Chat;
import life.nsu.sadchat.models.User;
import life.nsu.sadchat.utils.CustomLoader;
import life.nsu.sadchat.utils.FragmentAdapter;
import life.nsu.sadchat.utils.OnItemClickListener;
import life.nsu.sadchat.views.ChatFragment;
import life.nsu.sadchat.views.ContactsFragment;
import life.nsu.sadchat.views.ProfileFragment;
import life.nsu.sadchat.views.ViewProfileActivity;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    DatabaseReference reference;

    private FirebaseUser firebaseUser;

    private CircleImageView mProfilePicture;
    private TextView mUsername;

    private CustomLoader dialog;

    OnItemClickListener onItemClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prevent screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);

        this.onItemClick = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        dialog = new CustomLoader(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mProfilePicture = findViewById(R.id.ci_profile_picture);
        mUsername = findViewById(R.id.tv_username);

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabLayout.Tab tab = tabLayout.getTabAt(2);
                tab.select();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                mUsername.setText(user.getUsername());

                if (user.getImage() != null && !user.getImage().equals("default")) {

                    Glide.with(getApplicationContext())
                            .load(user.getImage())
                            .placeholder(R.drawable.ic_profile_avatar)
                            .circleCrop()
                            .into(mProfilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // check internet connection and recall
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("chats");
        dialog.show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

                int unread = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isSeen()) {
                        unread++;
                    }
                }

                if (unread == 0) {
                    adapter.addFragment(ChatFragment.newInstance(onItemClick), "Chats");
                } else {
                    adapter.addFragment(ChatFragment.newInstance(onItemClick), "(" + unread + ") " + "Chats");
                }

                adapter.addFragment(ContactsFragment.newInstance(onItemClick), "Users");
                adapter.addFragment(ProfileFragment.newInstance(onItemClick), "Profile");

                viewPager.setAdapter(adapter);

                tabLayout.setupWithViewPager(viewPager);

                if (dialog != null) {
                    dialog.hide();
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

    @Override
    public void onItemClick(String id, View v) {
        ViewProfileActivity viewProfileActivity = ViewProfileActivity.newInstance(id, this);

        viewProfileActivity.show(getSupportFragmentManager(), "viewProfile");
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