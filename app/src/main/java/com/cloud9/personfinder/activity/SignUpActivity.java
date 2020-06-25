package com.cloud9.personfinder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud9.personfinder.R;
import com.cloud9.personfinder.model.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutEmail, textInputLayoutname, textInputLayoutusername, textInputLayoutaddress, textInputLayoutcity, textInputLayoutpassword;
    private TextInputEditText EditTextEmail, EditTextname, EditTextaddress, EditTextcity, EditTextpassword;
    private Button btnSignUp;
    private TextView tvMoveToLogin;
    private ProgressBar progressBar;

    private String fullName, cityName, password, fullAddress, useremail;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private String TAG = "cvv";
    private UserHelper userHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bindingViews();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
        tvMoveToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signUpUser() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth = FirebaseAuth.getInstance();
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("users");
            getDatafromViews();

            userHelper = new UserHelper(fullName, fullAddress, cityName, useremail, password);
            Log.d(TAG, "signUpUser: " + userHelper.toString());

            firebaseAuth.createUserWithEmailAndPassword(useremail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                //updateUI(user);

                                if (user != null) {
                                    reference.child(user.getUid()).setValue(userHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "Data of user Inserted Successfully", Toast.LENGTH_SHORT).show();
                                            clearControls();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String message = e.getMessage();
                                            String cause = String.valueOf(e.getCause());

                                            Toast.makeText(SignUpActivity.this, "Message is:  " + message + "\nCause is: " + cause, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "User Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        } catch (Exception ex) {
            progressBar.setVisibility(View.GONE);
            ex.printStackTrace();
            Log.d("cvv", "signUpUser: " + ex);
            Toast.makeText(this, "Something is wrong \n" + ex.getMessage() + "\nCause is: " + ex.getCause(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearControls() {
        EditTextEmail.setText("");
        EditTextname.setText("");

        EditTextaddress.setText("");
        EditTextcity.setText("");
        EditTextpassword.setText("");
    }

    private void getDatafromViews() {
        try {
            cityName = Objects.requireNonNull(EditTextcity.getText()).toString();
            fullName = Objects.requireNonNull(EditTextname.getText()).toString();
            password = Objects.requireNonNull(EditTextpassword.getText()).toString();
            fullAddress = Objects.requireNonNull(EditTextaddress.getText()).toString();
            useremail = Objects.requireNonNull(EditTextEmail.getText()).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Please Enter all fields..", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindingViews() {
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

        tvMoveToLogin = findViewById(R.id.tv_movetoLogin);

        progressBar = findViewById(R.id.progress_signup);
    }
}
