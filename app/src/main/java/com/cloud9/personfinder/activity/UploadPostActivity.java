package com.cloud9.personfinder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud9.personfinder.R;
import com.cloud9.personfinder.model.MissingPersonsPost;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadPostActivity extends AppCompatActivity {
    private CircleImageView circleImageViewPersonone, circleImageViewPersontwo, circleImageViewPersonthree;
    private TextInputEditText edtPersonName, edtPersonAge, edtPersonAddress, edtPersonLostPlace, edtPersonCity, edtPersonGuardianContactone, edtPersonGuardianContacttwo, edtPersonGuardianInstructions, edtPersonGuardianCNIC;
    private Button btnSubmitPost;
    private TextView tvLocation;
    ProgressBar progressBar;
    private String personName, personAge, personAddress, personLostPlace, personCity, personGuardianInstructions, personContactone, personContacttwo, personGuardianCnic, personGuardianLocation;
    private Bitmap bitmap;
    private Bitmap bitmapTwo;
    private Bitmap bitmapThree;
    private String FirstimageId, SecondimageId, ThirdimageId;
    private Uri uriOne;
    private Uri uriTwo;
    private Uri uriThree;

    public static final int PICK_IMAGE_One = 1;
    public static final int PICK_IMAGE_Two = 2;
    public static final int PICK_IMAGE_Three = 3;
    private static final String TAG = "cvv";

    private StorageReference storageReference;
    private DatabaseReference dbreff;
    private StorageTask uploadtask;

    private MissingPersonsPost missingPersonsPost;

    private String personEmail, personUid;
    private int LOCATION_REQUEST_CODE = 10001;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        findingViews();
        getIntentData();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        circleImageViewPersonone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageOne();
            }
        });
        circleImageViewPersontwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageTwo();
            }
        });
        circleImageViewPersonthree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageThree();
            }
        });

        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });
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

    private void pickImageOne() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select First Picture.."), PICK_IMAGE_One);
    }

    private void pickImageTwo() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Second Picture.."), PICK_IMAGE_Two);
    }

    private void pickImageThree() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Third Picture.."), PICK_IMAGE_Three);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_One && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriOne = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriOne);
                // Log.d(TAG, String.valueOf(bitmap));
                circleImageViewPersonone.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("cvv", e.toString());
                Toast.makeText(this, "Something went wrong with image selection..", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_IMAGE_Two && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriTwo = data.getData();

            try {
                bitmapTwo = MediaStore.Images.Media.getBitmap(getContentResolver(), uriTwo);
                // Log.d(TAG, String.valueOf(bitmap));
                circleImageViewPersontwo.setImageBitmap(bitmapTwo);
                setUpFaceDetector(uriTwo);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("cvv", e.toString());
                Toast.makeText(this, "Something went wrong with image selection..", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_IMAGE_Three && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriThree = data.getData();

            try {
                bitmapThree = MediaStore.Images.Media.getBitmap(getContentResolver(), uriThree);
                // Log.d(TAG, String.valueOf(bitmap));
                circleImageViewPersonthree.setImageBitmap(bitmapThree);
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
        getValues();
        if (personName.isEmpty()) {
            edtPersonName.setError("Please fill this field");
        } else if (personGuardianLocation.isEmpty()) {
            getLastLocation();
        } else if (personAge.isEmpty()) {
            edtPersonAge.setError("Please fill this field");
        } else if (personAddress.isEmpty()) {
            edtPersonAddress.setError("Please fill this field");
        } else if (personLostPlace.isEmpty()) {
            edtPersonLostPlace.setError("Please fill this field");
        } else if (personCity.isEmpty()) {
            edtPersonCity.setError("Please fill this field");
        } else if (personContactone.isEmpty()) {
            edtPersonGuardianContactone.setError("Please fill this field");
        } else if (personContacttwo.isEmpty()) {
            edtPersonGuardianContacttwo.setError("Please fill this field");
        } else if (personGuardianCnic.isEmpty()) {
            edtPersonGuardianCNIC.setError("Please fill this field");
        } else if (personGuardianInstructions.isEmpty()) {
            edtPersonGuardianInstructions.setError("Please fill this field");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            storageReference = FirebaseStorage.getInstance().getReference("Images");
            dbreff = FirebaseDatabase.getInstance().getReference().child("Missing Person");

            if (uriOne != null) {
                FirstimageId = System.currentTimeMillis() + "." + getExtension(uriOne);

            } else if (uriTwo != null) {
                SecondimageId = System.currentTimeMillis() + "." + getExtension(uriOne);

            } else if (uriThree != null) {
                ThirdimageId = System.currentTimeMillis() + "." + getExtension(uriOne);

            } else {
                Toast.makeText(this, "Please Select Image.", Toast.LENGTH_SHORT).show();
            }
            missingPersonsPost = new MissingPersonsPost(personName, personAge, personAddress, personLostPlace, personCity, personGuardianInstructions, personContactone, personContacttwo, FirstimageId, SecondimageId, ThirdimageId, personGuardianCnic, personGuardianLocation);

            if (uploadtask != null && uploadtask.isInProgress()) {
                Toast.makeText(this, "Post is uploading.", Toast.LENGTH_SHORT).show();
            } else {
                //adding uid + auto gen id with post
                dbreff.child(personUid).push().setValue(missingPersonsPost);

//            missingPersonsPost.setUid(personUid);
//            dbreff.child(""+System.currentTimeMillis()).setValue(missingPersonsPost);
                //TODO  multiple storages. getting crashon more imges
                StorageReference referenceOne = storageReference.child(FirstimageId);
                StorageReference referenceTwo = storageReference.child(SecondimageId);
                StorageReference referenceThree = storageReference.child(ThirdimageId);

                uploadtask = referenceOne.putFile(uriOne);// image number one
                uploadtask = referenceTwo.putFile(uriTwo);//image number two and three were making crash last night..
                uploadtask = referenceThree.putFile(uriThree)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressBar.setVisibility(View.GONE);
                                clearControls();
                                Toast.makeText(getApplicationContext(), "Image and Post Uploaded Successfully ", Toast.LENGTH_LONG).show();
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
    }

    private void clearControls() {
        circleImageViewPersonone.setImageResource(R.drawable.ic_launcher_foreground);
        circleImageViewPersontwo.setImageResource(R.drawable.ic_launcher_foreground);
        circleImageViewPersonthree.setImageResource(R.drawable.ic_launcher_foreground);

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
        circleImageViewPersonone = findViewById(R.id.person_imageone);
        circleImageViewPersontwo = findViewById(R.id.person_imagetwo);
        circleImageViewPersonthree = findViewById(R.id.person_imagethree);

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
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            try {
                                Geocoder geocoder = new Geocoder(UploadPostActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                tvLocation.setText("GPS Address: " + addresses.get(0).getAddressLine(0));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                Toast.makeText(UploadPostActivity.this, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UploadPostActivity.this, "Unable to get your location.\n Please Turn your location on", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void setUpFaceDetector(Uri Image) throws FileNotFoundException {
        FaceDetector faceDetector=new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .build();
        if(!faceDetector.isOperational())
        {
            new AlertDialog.Builder(this).setMessage("Could not setup Face Detector").show();
            return;
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;
        InputStream stream=getContentResolver().openInputStream(Image);
        Bitmap bitmap=BitmapFactory.decodeStream(stream);
        Frame frame=new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faceSparseArray=faceDetector.detect(frame);
        Log.d("TEST", "setUpFaceDetector: "+faceSparseArray.size());
        detectedResponse(bitmap,faceSparseArray);
    }

    private void detectedResponse(Bitmap bitmap, SparseArray<Face> faceSparseArray) {
        Paint paint=new Paint();
        paint.setStrokeWidth(15);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        Bitmap TempBitmap= Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
        Canvas TempCanvas=new Canvas(TempBitmap);
        TempCanvas.drawBitmap(bitmap,0,0,null);
        for (int i=0;i<faceSparseArray.size();i++)
        {
            Face thisFace=faceSparseArray.valueAt(i);
            float x1=thisFace.getPosition().x;
            float y1=thisFace.getPosition().y;
            float x2=x1+thisFace.getWidth();
            float y2=y1+thisFace.getHeight();
            /*if(thisFace.getId()==0)
            {
                Toast.makeText(reportmissing.this,"YES",Toast.LENGTH_SHORT).show();
            }*/
            TempCanvas.drawRoundRect(new RectF(x1,y1,x2,y2),2,2,paint);
            circleImageViewPersontwo.setImageDrawable(new BitmapDrawable(getResources(),TempBitmap));
            if(faceSparseArray.size()<1)
            {
                new AlertDialog.Builder(this).setMessage("'Error' No Face Detected").show();
            }
            else if (faceSparseArray.size()==1)
            {
                new AlertDialog.Builder(this).setMessage("1 Face Detected").show();
                //flag_face=true;
            }
            else if(faceSparseArray.size()>1)
            {
                new AlertDialog.Builder(this).setMessage("'Error' More than 1 Face detected!").show();
            }
        }
    }
}
