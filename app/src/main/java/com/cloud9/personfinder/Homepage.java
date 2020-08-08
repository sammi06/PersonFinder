package com.cloud9.personfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cloud9.personfinder.activity.EditActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Homepage extends AppCompatActivity {
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef,like;
    Boolean isopen;
    FloatingActionButton mMainAddFab,edit;
    TextView mAddUserText,mAddContactText,madd_user_text;
    Boolean liker=false;
    String postkey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mRecyclerView=findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("ImageMissing");//Enter path of DB............
        mMainAddFab=findViewById(R.id.main_add_fab);
        edit=findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Homepage.this, EditActivity.class);
                startActivity(intent);
            }
        });
        mAddUserText=findViewById(R.id.add_user_text);
        mAddContactText=findViewById(R.id.add_contact_text);
        madd_user_text=findViewById(R.id.add_user_text2);
        like=mFirebaseDatabase.getReference("like");
        isopen=false;
        mMainAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isopen){
                    mAddUserText.setVisibility(View.INVISIBLE);
                    mAddContactText.setVisibility(View.INVISIBLE);
                    madd_user_text.setVisibility(View.INVISIBLE);
                    isopen=false;
                }
                else{
                    mAddUserText.setVisibility(View.VISIBLE);
                    mAddContactText.setVisibility(View.VISIBLE);
                    madd_user_text.setVisibility(View.VISIBLE);
                    isopen=true;
                }
            }
        });
        madd_user_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Migrate to profile page
                Intent intent=new Intent(Homepage.this,postActivity.class);
                startActivity(intent);

            }
        });
        mAddContactText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lost some one
                Intent intent=new Intent(Homepage.this,Foundsomeone.class);
                startActivity(intent);
            }
        });
        mAddUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FoundsomeOne
                Intent intent=new Intent(Homepage.this,LostSomeone.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Model,ViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Model, ViewHolder>(
                Model.class,
                R.layout.row,
                ViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, Model model, int i) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                final String c_user=user.getUid();
                postkey=getRef(i).getKey();
                viewHolder.setlikebutton(postkey);
                viewHolder.likebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        liker=true;
                        like.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(liker.equals(true)){
                                    if(dataSnapshot.child(postkey).hasChild(c_user)){
                                        like.child(postkey).child(c_user).removeValue();
                                        liker=false;
                                    }
                                    else {
                                        like.child(postkey).child(c_user).setValue(true);
                                        liker=false;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
                viewHolder.setDetails(getApplicationContext(),model.getName(),model.getCell(),model.getImageurl(),model.getRating());
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
