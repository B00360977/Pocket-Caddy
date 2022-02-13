package com.example.golfapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.golfapp.AccountNameChangeActivity;
import com.example.golfapp.EmailChangeActivity;
import com.example.golfapp.PasswordChangeActivity;
import com.example.golfapp.SearchResultActivity;
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

        updateEmailBtn.setOnClickListener(view -> startEmailChangeProcess());

        updateNameBtn.setOnClickListener(view -> startNameChangeProcess());

        updatePasswordBtn.setOnClickListener(view -> startPasswordChangeProcess());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void startPasswordChangeProcess() {
        Intent i = new Intent(getActivity(), PasswordChangeActivity.class);
        startActivity(i);
    }

    public void startNameChangeProcess() {
        Intent i = new Intent(getActivity(), AccountNameChangeActivity.class);
        startActivity(i);
    }

    public void startEmailChangeProcess() {
        Intent i = new Intent(getActivity(), EmailChangeActivity.class);
        startActivity(i);
    }
}