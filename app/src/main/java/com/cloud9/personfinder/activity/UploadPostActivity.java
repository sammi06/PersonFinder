package com.cloud9.personfinder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud9.personfinder.R;
import com.cloud9.personfinder.model.MissingPersonsPost;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadPostActivity extends AppCompatActivity {
    private CircleImageView circleImageViewPerson;
    private TextInputEditText edtPersonName, edtPersonAge, edtPersonAddress, edtPersonLostPlace, edtPersonCity, edtPersonGuardianContactone, edtPersonGuardianContacttwo, edtPersonGuardianInstructions, edtPersonGuardianCNIC;
    private Button btnSubmitPost;

    private TextView tvLocation;
    ProgressBar progressBar;
    private String personName, personAge, personAddress, personLostPlace, personCity, personGuardianInstructions, personContactone, personContacttwo, personGuardianCnic, personGuardianLocation;
    private Bitmap bitmap;
    String imageId;
    private Uri uri;

    public static final int PICK_IMAGE = 1;
    private static final String TAG = "cvv";

    private StorageReference storageReference;
    private DatabaseReference dbreff;
    private StorageTask uploadtask;

    private MissingPersonsPost missingPersonsPost;

    private String personEmail, personUid;
    private int LOCATION_REQUEST_CODE = 10001;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        findingViews();
        getIntentData();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        circleImageViewPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                circleImageViewPerson.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("cvv", e.toString());
                Toast.makeText(this, "Something went wrong with image selection..", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image Error..", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void submitPost() {
        progressBar.setVisibility(View.VISIBLE);
        getValues();

        storageReference = FirebaseStorage.getInstance().getReference("Images");
        dbreff = FirebaseDatabase.getInstance().getReference().child("posts");
        if (uri != null){
            imageId = System.currentTimeMillis() + "." + getExtension(uri);
        }else{
            Toast.makeText(this, "Please Select Image.", Toast.LENGTH_SHORT).show();
        }
        missingPersonsPost = new MissingPersonsPost(personName, personAge, personAddress, personLostPlace, personCity, personGuardianInstructions, personContactone, personContacttwo, imageId, personGuardianCnic, personGuardianLocation);

        if (uploadtask != null && uploadtask.isInProgress()) {
            Toast.makeText(this, "Post is uploading.", Toast.LENGTH_SHORT).show();
        } else {
            dbreff.child(personUid).setValue(missingPersonsPost);
            StorageReference reference = storageReference.child(imageId);
            uploadtask = reference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            clearControls();
                            Toast.makeText(getApplicationContext(), "Image and Post Uploaded Successfully ", Toast.LENGTH_LONG).show();
//                    String ImageUploadId = dbreff.push().getKey();
//                    dbreff.child(ImageUploadId).setValue(missingPersonsPost);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.toString());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UploadPostActivity.this, "Failure \n" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearControls() {
        circleImageViewPerson.setImageResource(R.drawable.ic_launcher_foreground);

        edtPersonGuardianCNIC.setText("");
        edtPersonName.setText("");
        edtPersonAddress.setText("");

        edtPersonAge.setText("");
        edtPersonCity.setText("");
        edtPersonGuardianContactone.setText("");

        edtPersonGuardianContacttwo.setText("");
        edtPersonGuardianInstructions.setText("");
        edtPersonLostPlace.setText("");
    }

    public void getValues() {
        personName = Objects.requireNonNull(edtPersonName.getText()).toString();
        personAge = Objects.requireNonNull(edtPersonAge.getText()).toString();
        personAddress = Objects.requireNonNull(edtPersonAddress.getText()).toString();
        personLostPlace = Objects.requireNonNull(edtPersonLostPlace.getText()).toString();
        personCity = Objects.requireNonNull(edtPersonCity.getText()).toString();
        personGuardianInstructions = Objects.requireNonNull(edtPersonGuardianInstructions.getText()).toString();
        personContactone = Objects.requireNonNull(edtPersonGuardianContactone.getText()).toString();
        personContacttwo = Objects.requireNonNull(edtPersonGuardianContacttwo.getText()).toString();

        personGuardianCnic = Objects.requireNonNull(edtPersonGuardianCNIC.getText()).toString();

        personGuardianLocation = tvLocation.getText().toString();

    }

    private void findingViews() {
        circleImageViewPerson = findViewById(R.id.person_image);

        edtPersonName = findViewById(R.id.edt_PersonName);
        edtPersonAge = findViewById(R.id.edt_PersonAge);
        edtPersonAddress = findViewById(R.id.edt_PersonAddress);
        edtPersonLostPlace = findViewById(R.id.edt_PersonLostPlace);
        edtPersonCity = findViewById(R.id.edt_PersonCity);
        edtPersonGuardianContactone = findViewById(R.id.edt_PersonGuardianContactone);
        edtPersonGuardianContacttwo = findViewById(R.id.edt_PersonGuardianContacttwo);
        edtPersonGuardianInstructions = findViewById(R.id.edt_PersonGuardianInstructions);
        edtPersonGuardianCNIC = findViewById(R.id.edt_PersonGuardianCNIC);

        tvLocation = findViewById(R.id.your_gps_location);

        btnSubmitPost = findViewById(R.id.buttonSubmitPost);

        progressBar = findViewById(R.id.progress_post);
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            personEmail = bundle.getString("personEmail");
            personUid = bundle.getString("personUid");
        }
    }


    //---------GET LATLONG AND ADDRESS (LOCATION)----------------------

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation();
            } else {
                //Permission not granted
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //We have a location
                    Log.d(TAG, "onSuccess: " + location.toString());
                    Log.d(TAG, "onSuccess: " + location.getLatitude());
                    Log.d(TAG, "onSuccess: " + location.getLongitude());
                    tvLocation.setText("Latitude as: " + location.getLatitude() + "\n" + "Longitude as: " + location.getLongitude());
                } else {
                    Log.d(TAG, "onSuccess: Location was null...");
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
            }
        });

        //-------------------------------------------
//        final LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(50000);
//        locationRequest.setFastestInterval(3000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationServices.getFusedLocationProviderClient(UploadPostActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                LocationServices.getFusedLocationProviderClient(UploadPostActivity.this).removeLocationUpdates(this);
//
//                if (locationResult != null && locationResult.getLocations().size() > 0) {
//                    int latestLocationIndex = locationResult.getLocations().size() - 1;
//
//                    double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
//                    double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
//                    String textLatLong;
//                    textLatLong = String.format("Latitude as: %s\nLongitude as: %s",
//                            latitude,
//                            longitude
//                    );
//                    Log.d(TAG, "onLocationResult: \n" + textLatLong);
//
//                    Location location = new Location("providerNA");
//                    location.setLatitude(latitude);
//                    location.setLongitude(longitude);
//                    fetchAddressFromLocation(location);
//
//                    //Toast.makeText(MainActivity.this, "" + location, Toast.LENGTH_LONG).show();
//                    // Log.d(TAG, "onLocationResult: \n" + location);
//                }
//            }
//        }, Looper.getMainLooper());
    }
}
