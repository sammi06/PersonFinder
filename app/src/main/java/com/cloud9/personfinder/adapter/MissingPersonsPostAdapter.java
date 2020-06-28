package com.cloud9.personfinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cloud9.personfinder.R;
import com.cloud9.personfinder.model.MissingPersonsPost;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MissingPersonsPostAdapter extends FirebaseRecyclerAdapter<MissingPersonsPost, MissingPersonsPostAdapter.PostHolder> {

    public MissingPersonsPostAdapter(@NonNull FirebaseRecyclerOptions<MissingPersonsPost> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull MissingPersonsPost model) {
        holder.tv.setText(model.getPersonName());
    }
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new PostHolder(view);
    }

    class PostHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPost;
        TextView tv;
        public PostHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.textviewUsername);
        }
    }
}
