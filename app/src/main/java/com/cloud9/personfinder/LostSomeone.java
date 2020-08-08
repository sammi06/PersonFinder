package com.cloud9.personfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LostSomeone extends AppCompatActivity {
    Boolean flag_age=false,flag_cell=false,flag_face=false,flag_map=false;
    List<Address> addresses;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference reference;
    ImageView addimage,location_button;
    EditText name,age,cell;
    TextView map_location;
    Button submit;
    StorageReference mStorageRef;
    public Uri imguri;
    private StorageTask uploadtask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_someone);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Lost Someone");

        map_location=(TextView)findViewById(R.id.maptxt);
        name=(EditText)findViewById(R.id.name);
        age=(EditText)findViewById(R.id.age);
        cell=(EditText)findViewById(R.id.cell);
        addimage=(ImageView)findViewById(R.id.imge);
        submit=(Button)findViewById(R.id.report);
        reference = FirebaseDatabase.getInstance().getReference();
        mStorageRef= FirebaseStorage.getInstance().getReference("ImagesMissing");
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filechooser();
            }
        });
        getLocation();
        cell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                String reg = "^[0-9]{10}";
                if (cell.getText().toString().matches(reg)&&cell.getText().toString().startsWith("3")) {
                    flag_cell = true;
                }
                else if(cell.getText().toString().trim().equals(null))
                {
                    cell.setError("Required");
                }
                else
                    cell.setError("Invalid Format or Incomplete Number");
            }
        });
        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (age.getText().toString().length()<=2) {
                    flag_age = true;
                }
                else if (age.getText().toString().trim().equals(null))
                    age.setError("Required");
                else
                    age.setError("Invalid Age");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setEnabled(false);
                if(flag_age=true&&flag_cell==true&&flag_face==true&&flag_map==true){
                    if(uploadtask!=null && uploadtask.isInProgress()){
                        Toast.makeText(LostSomeone.this,"Upload in progress",Toast.LENGTH_LONG).show();
                    }
                    else{if(addimage!=null) Fileuploader();
                    }

                }
                else if(flag_face==false){
                    new AlertDialog.Builder(LostSomeone.this).setMessage("No face or more than 1 face detected").show();
                }
                else if (flag_map==false)
                {
                    new AlertDialog.Builder(LostSomeone.this).setMessage("No location detected").show();
                }
                else
                    new AlertDialog.Builder(LostSomeone.this).setMessage("Fill the Fields properly").show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            imguri=data.getData();
            addimage.setImageURI(imguri);
            try {submit.setEnabled(true);
                setUpFaceDetector(imguri);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
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
            addimage.setImageDrawable(new BitmapDrawable(getResources(),TempBitmap));
            if(faceSparseArray.size()<1)
            {
                new AlertDialog.Builder(this).setMessage("'Error' No Face Detected").show();
            }
            else if (faceSparseArray.size()==1)
            {
                new AlertDialog.Builder(this).setMessage("1 Face Detected").show();
                flag_face=true;
            }
            else if(faceSparseArray.size()>1)
            {
                new AlertDialog.Builder(this).setMessage("'Error' More than 1 Face detected!").show();
            }
        }
    }
    private String getExtension( Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void Fileuploader()
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final StorageReference Imagename=mStorageRef.child("ImagesMissing"+imguri.getLastPathSegment());
        Imagename.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference imagestore= FirebaseDatabase.getInstance().getReference().child("ImageMissing");
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put("imageurl",String.valueOf(uri));
                        hashMap.put("Name",name.getText().toString().trim());
                        hashMap.put("Age",age.getText().toString().trim());
                        hashMap.put("Location_lat",""+addresses.get(0).getLatitude());
                        hashMap.put("Location_lon",""+addresses.get(0).getLongitude());
                        hashMap.put("cell",cell.getText().toString().trim());
                        hashMap.put("Status","Found");
                        hashMap.put("email",user.getEmail().toString());
                        imagestore.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LostSomeone.this,"Report Uploaded ",Toast.LENGTH_SHORT).show();
                                Intent intent
                                        =new Intent(LostSomeone.this,Homepage.class);
                                startActivity(intent);finish();
                            }
                        });
                    }
                });
            }
        });
    }
    private void filechooser()
    {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    private void getLocation()
    {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location=task.getResult();
                if(location!=null)
                {
                    Geocoder geocoder=new Geocoder(LostSomeone.this, Locale.getDefault());
                    try {
                        addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        //    Toast.makeText(lostimage.this,""+addresses,Toast.LENGTH_SHORT).show();
                        flag_map=true;
                        /*t1.setText(Html.fromHtml("<font color='#6200EE'><b>latitude :</b><br></font>'"+addresses.get(0).getLatitude()));
                        t2.setText(Html.fromHtml("<font color='#6200EE'><b>longitude :</b><br></font>'"+addresses.get(0).getLongitude()));
                        t3.setText(Html.fromHtml("<font color='#6200EE'><b>Country Name :</b><br></font>'"+addresses.get(0).getCountryName()));
                        t4.setText(Html.fromHtml("<font color='#6200EE'><b>Locality :</b><br></font>'"+addresses.get(0).getLocality()));*/
                        map_location.setText(Html.fromHtml("<font color='#6200EE'><b>Address :</b><br></font>'"+addresses.get(0).getAddressLine(0)));
                        if (addresses.size()<1){
                            map_location.setText(Html.fromHtml("<font color='#6200EE'><b>Address :</b><br></font>'"+"Cannot Get Location"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}