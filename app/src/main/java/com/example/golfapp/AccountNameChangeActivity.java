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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.Objects;

public class AccountNameChangeActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private String newAccountName;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_name_change);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progressBar3);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        EditText currentAccountName = findViewById(R.id.editTextTextPersonName4);
        EditText newAccountNameEdit = findViewById(R.id.newAccountName);
        currentAccountName.setText(firebaseUser.getDisplayName());

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(view -> {
            newAccountName = newAccountNameEdit.getText().toString().trim();
            if (TextUtils.isEmpty(newAccountName)) {
                newAccountNameEdit.setError("You must enter a new name");
            } else {
                submitBtn.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                UserProfileChangeRequest request = changeRequestBuilder();
                sendUpdateRequest(request);
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

    public UserProfileChangeRequest changeRequestBuilder() {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newAccountName)
                .build();

        return profileChangeRequest;
    }

    public void sendUpdateRequest(UserProfileChangeRequest updateRequest) {
        firebaseUser.updateProfile(updateRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                GlobalVariables.getInstance().setUserName(newAccountName);
                Toast.makeText(AccountNameChangeActivity.this, "Account Name Update Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            } else {
                progressBar.setVisibility(View.GONE);
                Log.e("Error", Objects.requireNonNull(task.getException()).getMessage());
                Toast.makeText(AccountNameChangeActivity.this, "We are unable to process your request at the moment. Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

}