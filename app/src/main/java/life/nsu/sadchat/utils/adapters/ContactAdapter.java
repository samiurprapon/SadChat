package life.nsu.sadchat.utils.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import life.nsu.sadchat.R;
import life.nsu.sadchat.models.Chat;
import life.nsu.sadchat.models.User;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    Context context;

    private ArrayList<User> contactList;

    private boolean isChat;
    private String theLastMessage;

    public ContactAdapter(Context context) {
        this.context = context;
    }

    public ContactAdapter(Context context, ArrayList<User> contactList, boolean isChat) {
        this.context = context;
        this.contactList = contactList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = contactList.get(position);

        holder.mUsername.setText(user.getUsername());

        if (isChat) {
            lastMessage(user.getId(), holder.mLastText);

            if (user.getActiveStatus().equals("online")) {
                holder.mActive.setVisibility(View.VISIBLE);
                holder.mUnActive.setVisibility(View.GONE);

            } else {
                holder.mActive.setVisibility(View.GONE);
                holder.mUnActive.setVisibility(View.VISIBLE);
            }

        } else {
            holder.mLastText.setVisibility(View.GONE);

            holder.mActive.setVisibility(View.GONE);
            holder.mUnActive.setVisibility(View.GONE);
        }

        if (!user.getImage().equals("default")) {
//            Log.d("onBindViewHolder", user.getImage());

            Glide.with(context)
                    .load(getBitmap(user.getImage()))
                    .placeholder(R.drawable.ic_profile_avatar)
                    .circleCrop()
                    .into(holder.mProfilePicture);
        }
    }

    public Bitmap getBitmap(String image) {
        byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    public void setContactList(ArrayList<User> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public void setChat(boolean chat) {
        isChat = chat;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mUsername;
        ImageView mProfilePicture;
        ImageView mActive;
        ImageView mUnActive;
        TextView mLastText;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsername = itemView.findViewById(R.id.username);
            mProfilePicture = itemView.findViewById(R.id.profile_image);
            mActive = itemView.findViewById(R.id.img_on);
            mUnActive = itemView.findViewById(R.id.img_off);
            mLastText = itemView.findViewById(R.id.last_msg);
        }
    }


    //check for last message
    private void lastMessage(String userId, final TextView lastText) {
        theLastMessage = "default";

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                                chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {

                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                if ("default".equals(theLastMessage)) {
                    lastText.setText(R.string.title_last_text);
                } else {
                    lastText.setText(theLastMessage);
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
