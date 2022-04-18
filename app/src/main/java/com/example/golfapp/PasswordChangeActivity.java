package com.example.golfapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

/**
 * This class handles all user requests to update their account password
 */

public class PasswordChangeActivity extends AppCompatActivity {

    private EditText currentPasswordField, newPasswordField, confirmNewPasswordField;
    private String currentPassword, newPassword, confirmNewPassword, userEmail;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        userEmail = firebaseUser.getEmail();
        currentPasswordField = findViewById(R.id.currentPassword);
        newPasswordField = findViewById(R.id.newPassword);
        confirmNewPasswordField = findViewById(R.id.confirmNewPassword);
        progressBar = findViewById(R.id.progressBar5);

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            currentPassword = currentPasswordField.getText().toString().trim();
            newPassword = newPasswordField.getText().toString().trim();
            confirmNewPassword = confirmNewPasswordField.getText().toString().trim();
            boolean passwordCheckPass = validatePasswords();
            if (passwordCheckPass) {
                reauthenticateUser();
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // checks that the passwords given match
    private boolean validatePasswords() {

        if(TextUtils.isEmpty(currentPassword)){
            currentPasswordField.setError("You must provide your current password");
            return false;
        }
        if(TextUtils.isEmpty(newPassword)){
            newPasswordField.setError("Password is required");
            return false;
        } else if (newPassword.length() < 6) {
            newPasswordField.setError("Password must be at least 6 Characters");
            return false;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordField.setError("Passwords do not match");
            newPasswordField.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    // users have to be reauthenticated before changing password
    private void reauthenticateUser() {
        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPassword);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updatePassword();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PasswordChangeActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // sends change request to Firebase to update the users password and logs the user out
    private void updatePassword() {
        firebaseUser.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Info", "User password updated.");
                        Toast.makeText(PasswordChangeActivity.this, "Password change successful. Please log in again", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PasswordChangeActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}