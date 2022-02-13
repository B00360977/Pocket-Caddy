package com.example.golfapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class EmailChangeActivity extends AppCompatActivity {

    private EditText newEmailText;
    private FirebaseUser firebaseUser;
    private String currentEmail, userPassword, newEmailAddress;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_change);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressBar = findViewById(R.id.progressBar4);
        EditText currentEmailText = findViewById(R.id.currentEmailAddress);
        currentEmail = firebaseUser.getEmail();
        currentEmailText.setText(currentEmail);
        newEmailText = findViewById(R.id.newEmailAddress);

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(view -> {
            newEmailText = findViewById(R.id.newEmailAddress);
            newEmailAddress = newEmailText.getText().toString().trim();
            if (TextUtils.isEmpty(newEmailAddress)) {
                newEmailText.setError("Email cannot be empty");
            } else {
                getPassword();
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

    public void updateEmail() {
        progressBar.setVisibility(View.VISIBLE);
        reauthenticateUser();
    }

    public void reauthenticateUser() {
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, userPassword);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateFirebase();
            } else {
                Toast.makeText(EmailChangeActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            userPassword = input.getText().toString();
            updateEmail();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void updateFirebase() {
        firebaseUser.updateEmail(newEmailAddress).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                GlobalVariables.getInstance().setUserEmail(newEmailAddress);
                Toast.makeText(EmailChangeActivity.this, "Email Update Successful. Please verify your new email address by clicking the link in the email you have received before trying to log back in", Toast.LENGTH_SHORT).show();
                sendVerificationEmail();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
            } else {
                progressBar.setVisibility(View.GONE);
                Log.e("Error", Objects.requireNonNull(task.getException()).getMessage());
                Toast.makeText(EmailChangeActivity.this, "We are unable to process your request at the moment. Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendVerificationEmail() {
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Info", "Email sent.");
                    }
                });
    }
}