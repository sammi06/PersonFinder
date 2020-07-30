package com.cloud9.personfinder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud9.personfinder.Homepage;
import com.cloud9.personfinder.MainActivity;
import com.cloud9.personfinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    private TextInputEditText textInputEditTextEmail, textInputEditTextPassword;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private Button buttonLogin;
    private TextView tvMoveToSignup;
    private ProgressBar progressBar;
    private String emailAddress, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Sign-in");
        bindingViews();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        tvMoveToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getDatefromViews() {
        emailAddress = Objects.requireNonNull(textInputEditTextEmail.getText()).toString();
        password = Objects.requireNonNull(textInputEditTextPassword.getText()).toString();

    }
    private void userLogin() {
        getDatefromViews();
        if (emailAddress.isEmpty()) {
            textInputEditTextEmail.setError("Please Fill field");
            textInputEditTextEmail.requestFocus();
        } else if (password.isEmpty()) {
            textInputEditTextPassword.setError("Please Fill field");
            textInputEditTextPassword.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if ((task.isSuccessful())) {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(LogInActivity.this, Homepage.class);
                        /*intent.putExtra("personEmail", task.getResult().getUser().getEmail());
                        intent.putExtra("personUid", task.getResult().getUser().getUid());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
                        startActivity(intent);
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String message = Objects.requireNonNull(task.getException()).getMessage();
                        String cause = String.valueOf(task.getException().getCause());
                        Toast.makeText(LogInActivity.this, Objects.requireNonNull("Message is:  " + message + "\nCause is:  " + cause),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void bindingViews() {
        textInputLayoutEmail = findViewById(R.id.til_LoginEmail);
        textInputLayoutPassword = findViewById(R.id.til_Loginpassword);
        textInputEditTextEmail = findViewById(R.id.et_LoginEmail);
        textInputEditTextPassword = findViewById(R.id.et_Loginpassword);
        buttonLogin = findViewById(R.id.btnLogin);
        tvMoveToSignup = findViewById(R.id.tv_movetoSignup);
        progressBar = findViewById(R.id.progress_login);
    }
}
