package com.aac.envision;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_checkbox_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        if(user!= null) {
            holder.bind(user);
        } else {
            Log.e("MyAdapter", "Error binding user at position " + position + ": user is null!");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilepicImageView;
        private TextView textView;
        private TextView emailTextView;
        private Spinner spinnerAction;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profilepicImageView = itemView.findViewById(R.id.profilepicImageView);
            textView = itemView.findViewById(R.id.textView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            spinnerAction = itemView.findViewById(R.id.spinnerAction);

            // Set up the adapter for the spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    context,
                    R.array.actions,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAction.setAdapter(adapter);
        }

        public void bind(User user) {
            // Set data to views
            Glide.with(itemView.getContext())
                    .load(user.getProfilePic())
                    .into(profilepicImageView);

            textView.setText(user.getPageDescription());
            emailTextView.setText(user.getEmail());
        }
    }
}