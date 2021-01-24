package life.nsu.sadchat.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import life.nsu.sadchat.R;
import life.nsu.sadchat.models.User;
import life.nsu.sadchat.utils.OnItemClickListener;
import life.nsu.sadchat.utils.adapters.ContactAdapter;


public class ContactsFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static ContactsFragment fragment = null;

    private List<User> contactList;
    private ContactAdapter adapter;

    FrameLayout frameLayout;

    private RecyclerView recyclerView;
    EditText mSearch;

    static OnItemClickListener onItemClickListener;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance(OnItemClickListener onItemClick) {
        if (fragment == null) {
            fragment = new ContactsFragment();
        }

        onItemClickListener = onItemClick;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearch = view.findViewById(R.id.search_users);
        recyclerView = view.findViewById(R.id.recycler_view);
        frameLayout = view.findViewById(R.id.lt_empty_layout);

        contactList = new ArrayList<>();

        readUsers();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void searchUsers(String s) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("username")
                .startAt(s.toLowerCase())
                .endAt(s.toLowerCase() + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                        contactList.add(user);
                    }
                }

                if (contactList.size() == 0) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    frameLayout.setVisibility(View.GONE);
                }

                adapter = new ContactAdapter(getContext(), contactList, onItemClickListener, false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mSearch.getText().toString().equals("")) {
                    contactList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                            contactList.add(user);
                        }
                    }

                    if (contactList.size() == 0) {
                        frameLayout.setVisibility(View.VISIBLE);
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }

                    adapter = new ContactAdapter(getContext(), contactList, onItemClickListener, false);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}