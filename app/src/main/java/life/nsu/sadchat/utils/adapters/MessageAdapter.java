package life.nsu.sadchat.utils.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import life.nsu.sadchat.R;
import life.nsu.sadchat.models.Chat;
import life.nsu.sadchat.models.User;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    Context context;
    List<Chat> chatList;
    User user;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chatList, User user) {
        this.chatList = chatList;
        this.context = context;
        this.user = user;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_sender_message, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_receiver_message, parent, false);
        }

        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        holder.mShowMessage.setText(chat.getMessage());

        if (chat.getTime() != null && !chat.getTime().trim().equals("")) {
            holder.mTime.setText(holder.convertTime(chat.getTime()));
        }

        if (user.getImage().equals("default")) {
            holder.mProfilePicture.setImageResource(R.drawable.ic_profile_avatar);
        } else {
            Glide.with(context)
                    .load(user.getImage())
                    .placeholder(R.drawable.ic_profile_avatar)
                    .circleCrop()
                    .into(holder.mProfilePicture);
        }

        if (position == chatList.size() - 1 && getItemViewType(position) == 1) {
//            Toast.makeText(context, ""+chat.isIsSeen()+"\n"+chat.getMessage(), Toast.LENGTH_SHORT).show();
//            holder.mMessageSeen.setVisibility(View.VISIBLE);

            if (chat.isIsSeen()) {
                // image indicator
                holder.mReadStatus.setImageResource(R.drawable.ic_text_seen);
            } else {
                // image indicator
                holder.mReadStatus.setImageResource(R.drawable.ic_delivered);
            }
        } else if(position == chatList.size() - 1 && getItemViewType(position) == 0){
            holder.mMessageSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mShowMessage;
        public ImageView mProfilePicture;
        public TextView mMessageSeen;
        public TextView mTime;
        ImageView mReadStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            mShowMessage = itemView.findViewById(R.id.show_message);
            mProfilePicture = itemView.findViewById(R.id.profile_image);
            mMessageSeen = itemView.findViewById(R.id.txt_seen);
            mReadStatus = itemView.findViewById(R.id.iv_read_status);
            mTime = itemView.findViewById(R.id.time_tv);
        }

        public String convertTime(String time) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            return formatter.format(new Date(Long.parseLong(time)));
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}