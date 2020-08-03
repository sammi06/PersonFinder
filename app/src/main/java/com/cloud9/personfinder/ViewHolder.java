package com.cloud9.personfinder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    View mView;
    ImageView mImageTv;
    TextView name,phone,rating,liketext;
    DatabaseReference like;
    ImageView likebutton;
    int likecount=0;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
    }
    public void setDetails(Context context , String name1, String phone1,String img1,String rating1)
    {
        //ImageView mImageTv=mView.findViewById(R.id.rImageView);
         mImageTv = (ImageView)this.mView.findViewById(R.id.rImageView);
         name=(TextView) this.mView.findViewById(R.id.name);
         phone=(TextView) this.mView.findViewById(R.id.Phone);
         rating=(TextView)this.mView.findViewById(R.id.rating);
         //num=(TextView)this.mView.findViewById(R.id.cell);
         //num.setText("Contact Number # 0"+cell);
         name.setText("Name: "+name1);
         phone.setText("Phone: "+phone1);
       //  rating.setText("Viewed by: "+rating1);
         Picasso.get().load(img1).into(mImageTv);
    }
    public void setlikebutton(final String key){
        likebutton=(ImageView)this.mView.findViewById(R.id.likebutton);
        liketext=(TextView)this.mView.findViewById(R.id.liketext);
        like= FirebaseDatabase.getInstance().getReference("like");
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        final String userid=user.getUid();
        final String likes="likes";
        like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(key).hasChild(userid)){
                    likecount=(int)dataSnapshot.child(key).getChildrenCount();
                    likebutton.setBackgroundResource(R.drawable.like);
                    liketext.setText(""+Integer.toString(likecount)+" "+likes);
                }
                else {
                    likecount=(int)dataSnapshot.child(key).getChildrenCount();
                    likebutton.setBackgroundResource(R.drawable.unlike);
                    liketext.setText(""+Integer.toString(likecount)+" "+likes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
