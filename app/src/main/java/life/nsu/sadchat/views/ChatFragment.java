package life.nsu.sadchat.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import life.nsu.sadchat.R;
import life.nsu.sadchat.models.ChatItem;
import life.nsu.sadchat.models.Token;
import life.nsu.sadchat.models.User;
import life.nsu.sadchat.utils.OnItemClickListener;
import life.nsu.sadchat.utils.adapters.ContactAdapter;


public class ChatFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static ChatFragment fragment = null;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ContactAdapter adapter;

    private List<ChatItem> chatItems;
    private ArrayList<User> userList;

    FrameLayout frameLayout;
    RecyclerView recyclerView;

    private static OnItemClickListener onItemClickListener;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(OnItemClickListener onItemClick) {
        if(fragment == null) {
            fragment = new ChatFragment();
        }

        onItemClickListener = onItemClick;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recycler_view);
        frameLayout = view.findViewById(R.id.lt_empty_layout);

        chatItems = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        reference = FirebaseDatabase.getInstance().getReference("chatsList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatItems.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatItem chatItem = snapshot.getValue(ChatItem.class);

                    chatItems.add(chatItem);
                }

                if (chatItems.size() == 0) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    frameLayout.setVisibility(View.GONE);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String tokenString){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tokens");
        Token token = new Token(tokenString);
        reference.child(firebaseUser.getUid()).setValue(token);
    }


    private void chatList() {
        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    for (ChatItem chatList : chatItems) {
                        if (user != null && user.getId() != null && chatList != null && chatList.getId() != null && user.getId().equals(chatList.getId())) {
                            userList.add(user);
                        }
                    }
                }

                adapter = new ContactAdapter(getContext(), userList, onItemClickListener, true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}