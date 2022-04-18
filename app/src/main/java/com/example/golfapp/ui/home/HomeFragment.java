package com.example.golfapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.golfapp.GlobalVariables;
import com.example.golfapp.NewRound;
import com.example.golfapp.R;
import com.example.golfapp.databinding.FragmentHomeBinding;

import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.accountName;
        textView.setText(GlobalVariables.getInstance().getUserName());

        Button viewHistoryBtn = binding.historyButton;
        viewHistoryBtn.setOnClickListener(this);

        Button newRoundBtn = binding.newRoundButton;
        newRoundBtn.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.history_button:
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_history);
                break;
            case R.id.new_round_button:
                Intent i = new Intent(getActivity(), NewRound.class);
                startActivity(i);
                requireActivity().overridePendingTransition(0, 0);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}