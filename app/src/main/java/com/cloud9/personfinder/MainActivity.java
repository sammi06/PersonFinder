package com.cloud9.personfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cloud9.personfinder.activity.ProfileActivity;
//import com.cloud9.personfinder.adapter.MissingPersonsPostAdapter;
//import com.cloud9.personfinder.adapter.MissingPersonsPostAdapter;
import com.cloud9.personfinder.model.MissingPersonsPost;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
  /*  private RecyclerView recyclerViewMissingPersonsPost;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private MissingPersonsPostAdapter missingPersonsPostAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getPostsData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        //return true;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "Please Login to countinue..", Toast.LENGTH_SHORT).show();
                Intent LoginActivity = new Intent(MainActivity.this, MainActivity.class);
                startActivity(LoginActivity);

            } else {
                Intent ProfileActivity = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(ProfileActivity);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void getPostsData() {
        Query query = databaseReference.child("Missing Person");
        Log.d("cvv", "getPostsData: "+query);

        //-------------------
        FirebaseRecyclerOptions<MissingPersonsPost> options = new FirebaseRecyclerOptions.Builder<MissingPersonsPost>()
                .setQuery(query,MissingPersonsPost.class).build();
        Log.d("cvv", "getPostsData: "+options);

        //TODO  unable to get data
        missingPersonsPostAdapter = new MissingPersonsPostAdapter(options);
        recyclerViewMissingPersonsPost = findViewById(R.id.rv_MissingPersonPosts);
        recyclerViewMissingPersonsPost.setHasFixedSize(true);
        recyclerViewMissingPersonsPost.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewMissingPersonsPost.setAdapter(missingPersonsPostAdapter);
    }

//    private void getPostsData() {
//    }


    @Override
    protected void onStart() {
        super.onStart();
        missingPersonsPostAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        missingPersonsPostAdapter.stopListening();
    }
}
*/
}