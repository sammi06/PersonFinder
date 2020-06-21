package com.cloud9.personfinder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cloud9.personfinder.R;
import com.cloud9.personfinder.model.MissingPersonsPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadPostActivity extends AppCompatActivity {
    private CircleImageView circleImageViewPerson;
    private EditText edtPersonName, edtPersonAge, edtPersonAddress, edtPersonLostPlace, edtPersonCity, edtPersonGuardianContactone, edtPersonGuardianContacttwo, edtPersonGuardianInstructions;
    private Button btnSubmitPost;

    private String personName, personAge, personAddress, personLostPlace, personCity, personGuardianInstructions, personContactone, personContacttwo;
    private Bitmap bitmap;
    private Uri uri;

    public static final int PICK_IMAGE = 1;

    private StorageReference storageReference;
    private DatabaseReference dbreff;
    private StorageTask uploadtask;

    private MissingPersonsPost missingPersonsPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        findingViews();

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
                Log.e("cvv",e.toString());
                Toast.makeText(this, "Something went wrong with image selection..", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image Error..", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitPost() {
        getValues();

        storageReference = FirebaseStorage.getInstance().getReference("Images");
        dbreff = FirebaseDatabase.getInstance().getReference().child("posts");
        String imageId;
        imageId = System.currentTimeMillis() + "." + getExtension(uri);
        missingPersonsPost = new MissingPersonsPost(personName, personAge, personAddress, personCity, personGuardianInstructions, personContactone, personContacttwo, imageId);

        if (uploadtask != null && uploadtask.isInProgress()) {
            Toast.makeText(this, "Post is uploading.", Toast.LENGTH_SHORT).show();
        } else {
            dbreff.push().setValue(missingPersonsPost);
            StorageReference reference = storageReference.child(imageId);
            uploadtask = reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Image and Post Uploaded Successfully ", Toast.LENGTH_LONG).show();
//                    String ImageUploadId = dbreff.push().getKey();
//                    dbreff.child(ImageUploadId).setValue(missingPersonsPost);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("cvv",e.toString());
                    Toast.makeText(UploadPostActivity.this, "Failure \n"+e, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getValues() {
        personName = edtPersonName.getText().toString();
        personAge = edtPersonAge.getText().toString();
        personAddress = edtPersonAddress.getText().toString();
        personLostPlace = edtPersonLostPlace.getText().toString();
        personCity = edtPersonCity.getText().toString();
        personGuardianInstructions = edtPersonGuardianInstructions.getText().toString();
        personContactone = edtPersonGuardianContactone.getText().toString();
        personContacttwo = edtPersonGuardianContacttwo.getText().toString();
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
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

        btnSubmitPost = findViewById(R.id.buttonSubmitPost);
    }
}
