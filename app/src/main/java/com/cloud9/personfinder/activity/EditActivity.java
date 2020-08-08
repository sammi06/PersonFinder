package com.cloud9.personfinder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloud9.personfinder.R;
import com.cloud9.personfinder.model.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class EditActivity extends AppCompatActivity {
    DatabaseReference reference;
    private TextInputLayout textInputLayoutEmail, textInputLayoutname, textInputLayoutusername, textInputLayoutaddress, textInputLayoutcity, textInputLayoutpassword;
    private TextInputEditText EditTextEmail, EditTextname, EditTextaddress, EditTextcity, EditTextpassword;
    private String fullName, cityName, password, fullAddress, useremail;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private static final String TAG = "EditActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Edit user profile");
        textInputLayoutEmail = findViewById(R.id.til_SignupEmail);
        textInputLayoutname = findViewById(R.id.til_UserFullname);
        textInputLayoutaddress = findViewById(R.id.til_UserAddress);
        textInputLayoutcity = findViewById(R.id.til_UserCity);
        textInputLayoutpassword = findViewById(R.id.til_UserPassword);
        EditTextEmail = findViewById(R.id.et_SignupEmail);
        EditTextname = findViewById(R.id.et_UserFullname);
        EditTextaddress = findViewById(R.id.et_UserAddress);
        EditTextcity = findViewById(R.id.et_UserCity);
        EditTextpassword = findViewById(R.id.et_UserPassword);
        btnSignUp = findViewById(R.id.buttonSignUp);
        progressBar = findViewById(R.id.progress_signup);
        reference= FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           //     Toast.makeText(EditActivity.this,""+dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();
                fullName = dataSnapshot.getValue(String.class);
                EditTextname.setText(fullName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        reference.child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(EditActivity.this,""+dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();
                fullAddress = dataSnapshot.getValue(String.class);
                EditTextaddress.setText(fullAddress);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        reference.child("city").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(EditActivity.this,""+dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();
                cityName = dataSnapshot.getValue(String.class);
                EditTextcity.setText(cityName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        reference.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(EditActivity.this,""+dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();
                useremail = dataSnapshot.getValue(String.class);
                EditTextEmail.setText(useremail);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        reference.child("pass").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(EditActivity.this,""+dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();
                password = dataSnapshot.getValue(String.class);
                EditTextpassword.setText(password);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
    }
    private void signUpUser() {
        cityName = EditTextcity.getText().toString();
        fullName = EditTextname.getText().toString();
        password = EditTextpassword.getText().toString();
        fullAddress = EditTextaddress.getText().toString();
        useremail = EditTextEmail.getText().toString();
        if (!useremail.isEmpty()) {
            reference.child("email").setValue(useremail);
            EditTextEmail.setText("");
        } if (!fullName.isEmpty()) {
            reference.child("Name").setValue(fullName);
            EditTextname.setText("");
        }  if (!fullAddress.isEmpty()) {
            reference.child("address").setValue(fullAddress);
            EditTextaddress.setText("");
        }  if (!cityName.isEmpty()) {
            reference.child("city").setValue(cityName);
            EditTextcity.setText("");
        }  if (!password.isEmpty()) {
            reference.child("pass").setValue(password);
            EditTextpassword.setText("");
        }
            Toast.makeText(EditActivity.this,"Sucessfully updated",Toast.LENGTH_SHORT).show();
    }
}