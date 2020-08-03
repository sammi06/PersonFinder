package com.cloud9.personfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class postActivity extends AppCompatActivity {
    ImageView imageView,image1;
    TextView name, age, status, lat, lon, cell;
    TextView Nname, Nage, Nstatus, Nlat, Nlon, Ncell;
    private int time = 10;
    DatabaseReference refe;
    Boolean missing = false;
    Button locate;
    Double lat1, lon1;
    Button btn;
    String urlmissing = null, urlfound = null,respons="sd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageView = (ImageView) findViewById(R.id.image);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("My Post");
        image1 = (ImageView) findViewById(R.id.image1);
        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age);
        cell = (TextView) findViewById(R.id.cell);
        status = (TextView) findViewById(R.id.Status);
        lat = (TextView) findViewById(R.id.lat);
        lon = (TextView) findViewById(R.id.lon);
        Nname = (TextView) findViewById(R.id.name1);
        Nage = (TextView) findViewById(R.id.age1);
        Ncell = (TextView) findViewById(R.id.cell1);
        Nstatus = (TextView) findViewById(R.id.Status1);
        Nlat = (TextView) findViewById(R.id.lat1);
        Nlon = (TextView) findViewById(R.id.lon1);
        locate = (Button) findViewById(R.id.locate);
        locate.setVisibility(View.INVISIBLE);
        refe = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(postActivity.this,""+user.getPhoneNumber(),Toast.LENGTH_SHORT).show();
        if (user != null)
            refe.child("ImageMissing").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String number =  postSnapshot.child("email").getValue().toString();
                        if (number.equals(user.getEmail())) {
                            missing = true;
                            urlmissing = postSnapshot.child("imageurl").getValue().toString();
                            Picasso.get().load(urlmissing).into(imageView);
                            name.setText("Name: " + postSnapshot.child("Name").getValue().toString());
                            cell.setText("cell: " + postSnapshot.child("cell").getValue().toString());
                            age.setText("Age: " + postSnapshot.child("Age").getValue().toString());
                            status.setText("Status: " + postSnapshot.child("Status").getValue().toString());
                            lat.setText("Lat: " + postSnapshot.child("Location_lat").getValue().toString());
                            lon.setText("Lon: " + postSnapshot.child("Location_lon").getValue().toString());
                            cross_user();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        if (missing == false && user != null) {
            refe.child("ImageFound").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String number =  postSnapshot.child("email").getValue().toString();
                        if (number.equals(user.getEmail())) {
                            missing = true;
                            String ur = postSnapshot.child("imageurl").getValue().toString();
                            Picasso.get().load(ur).into(imageView);
                            name.setText("Name: " + postSnapshot.child("Name").getValue().toString());
                            cell.setText("cell: " + postSnapshot.child("cell").getValue().toString());
                            age.setText("Age: " + postSnapshot.child("Age").getValue().toString());
                            status.setText("Status: " + postSnapshot.child("Status").getValue().toString());
                            lat.setText("Latitude: " + postSnapshot.child("Location_lat").getValue().toString());
                            lon.setText("Longitude: " + postSnapshot.child("Location_lon").getValue().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            locate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if(Nname.getText()==null){
                        Intent intent = new Intent(this, MapsActivity.class);
                        intent.putExtra("lat", lat.getText().toString().trim());
                        intent.putExtra("lon", lon.getText().toString().trim());
                        startActivity(intent);}
                    else {
                        Intent intent = new Intent(this, MapsActivity.class);
                        intent.putExtra("lat", Nlat.getText().toString().trim());
                        intent.putExtra("lon", Nlon.getText().toString().trim());
                        startActivity(intent);}*/
                }
            });
        }
    }
    private void cross_user() {
        refe.child("ImageFound").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    urlfound = postSnapshot.child("imageurl").getValue().toString();
                    String urlshow=postSnapshot.child("imageurl").getValue().toString();
                    String name=postSnapshot.child("Name").getValue().toString();
                    String cell=postSnapshot.child("cell").getValue().toString();
                    String age=postSnapshot.child("Age").getValue().toString();
                    String status=postSnapshot.child("Status").getValue().toString();
                    String lat=postSnapshot.child("Location_lat").getValue().toString();
                    String lon=postSnapshot.child("Location_lon").getValue().toString();
                    respons= postRequest(urlmissing, urlfound,urlshow
                            ,name,age,cell,status,lat,lon
                    );
                }
                //else{Toast.makeText(Userprofile.this, "Person not found yet", Toast.LENGTH_LONG).show();}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private String postRequest(final String urlmissing, final String urlfound,
                               final String furlshow, final String fname, final String fage, final String fcell, final String fstatus, final String flat, final String flon
    ) {
        final String[] Return = {null};
        RequestQueue requestQueue = Volley.newRequestQueue(postActivity.this);
        String url1 = urlmissing;
        String url2 = urlfound;
        //String URL="http://192.168.43.78:5000/scratch";
        String URL="http://192.168.10.11:5000/scratch?url1="+url1+"&url2="+url2;
        final JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            respons = response.getString("message");
                            if (respons.equals("verified")) {
                                Picasso.get().load(furlshow).into(image1);
                                Nname.setText("Name: " + fname);
                                Ncell.setText("cell: " + fcell);
                                Nage.setText("Age: " + fage);
                                Nstatus.setText("Status: " + fstatus);
                                Nlat.setText("Latitude: " + flat);
                                Nlon.setText("Longitude: " + flon);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                           Toast.makeText(postActivity.this, "Error Occurred: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        {
            objectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            requestQueue.add(objectRequest);
            return respons;
        }
    }
}