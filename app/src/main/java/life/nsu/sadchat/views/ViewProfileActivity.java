package life.nsu.sadchat.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import life.nsu.sadchat.R;
import life.nsu.sadchat.models.User;

public class ViewProfileActivity extends BottomSheetDialogFragment {

    String uid;
    DatabaseReference reference;
    TextView mUsername;
    TextView mBio;
    ImageView mProfilePicture;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public ViewProfileActivity() {

    }

    public static ViewProfileActivity newInstance(String uid, Context context) {
        Bundle args = new Bundle();
        args.putString("uid", uid);

        ViewProfileActivity.context = context;
        ViewProfileActivity activity = new ViewProfileActivity();
        activity.setArguments(args);
        return activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view_profile, container, false);

        if (getArguments() != null) {
            mProfilePicture = view.findViewById(R.id.ci_profile_image);
            mUsername = view.findViewById(R.id.tv_username);
            mBio = view.findViewById(R.id.et_bio);

            uid = getArguments().getString("uid");
            reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    mUsername.setText(user.getUsername());
                    mBio.setText(user.getBio());

                    if (user.getImage().equals("default")) {
                        mProfilePicture.setImageResource(R.drawable.ic_profile_avatar);
                    } else {
                        Glide.with(context)
                                .load(user.getImage())
                                .placeholder(R.drawable.ic_profile_avatar)
                                .circleCrop()
                                .into(mProfilePicture);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return view;
    }
}
