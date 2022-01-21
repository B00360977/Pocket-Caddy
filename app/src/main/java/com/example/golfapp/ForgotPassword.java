package com.example.golfapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText mEmail;
    Button mResetBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    ImageButton mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmail = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        mResetBtn = findViewById(R.id.resetButton);
        mBackBtn = findViewById(R.id.backButton);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the spinning progress bar to show that the app is processing
                progressBar.setVisibility(View.VISIBLE);

                // Gets email and sends reset link
                fAuth.sendPasswordResetEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Please check you email for password reset link", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
                // Moves user back to Login Screen
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}