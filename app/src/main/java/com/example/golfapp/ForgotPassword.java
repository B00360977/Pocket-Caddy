package com.example.golfapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * The ForgotPassword class handles the Firebase reset password methods
 */

public class ForgotPassword extends AppCompatActivity {

    private EditText mEmail;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the content for the screen
        setContentView(R.layout.activity_forgot_password);

        mEmail = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        Button mResetBtn = findViewById(R.id.resetButton);

        // adding the toolbar
        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // add listener to submit button
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email cannot be empty");
                } else {

                    // Show the spinning progress bar to show that the app is processing
                    progressBar.setVisibility(View.VISIBLE);

                    // Gets email and sends reset link
                    fAuth.sendPasswordResetEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
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
            }
        });
    }
}