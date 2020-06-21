package com.cloud9.personfinder.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPost;
        TextView tv;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
