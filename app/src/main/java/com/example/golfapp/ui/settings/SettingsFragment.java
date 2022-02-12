package com.example.golfapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.golfapp.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {


    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String accountName = firebaseUser.getDisplayName();
        String accountEmail = firebaseUser.getEmail();

        EditText accountNameText = binding.editTextTextPersonName2;
        accountNameText.setText(accountName);
        EditText emailText = binding.editTextTextPersonName3;
        emailText.setText(accountEmail);

        Button updateEmailBtn = binding.updateEmail;
        Button updateNameBtn = binding.updateAccountName;
        Button updatePasswordBtn = binding.updatePassword;

        updateEmailBtn.setOnClickListener(view -> {
            //new activity
        });

        updateNameBtn.setOnClickListener(view -> {
            //do this
        });

        updatePasswordBtn.setOnClickListener(view -> {
            //do this
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}