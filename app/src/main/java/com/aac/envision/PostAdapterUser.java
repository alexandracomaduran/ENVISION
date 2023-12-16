package com.aac.envision;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PostAdapterUser extends RecyclerView.Adapter<PostAdapterUser.PostViewHolder> {

    private List<Post> postList;
    private Context context;

    public PostAdapterUser(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

   @Override
    public int getItemCount() {
        if (postList != null) {
            return postList.size();
        } else {
            return 0; // or any other value you prefer for empty state
        }
    }
    public class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView emailTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.tvEmail);
            imageView = itemView.findViewById(R.id.tvPostImage);
        }

        public void bind(Post post) {
            // Set data to views
            Glide.with(itemView.getContext())
                    .load(post.getMediaURL())
                    .into(imageView);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(currentUser != null) {
                emailTextView.setText(currentUser.getEmail());
            } else {
                Toast.makeText(context, "No user logged in.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}