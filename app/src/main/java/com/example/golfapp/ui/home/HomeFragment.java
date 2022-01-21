package com.example.golfapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.golfapp.GlobalVariables;
import com.example.golfapp.R;
import com.example.golfapp.databinding.FragmentHomeBinding;
import com.example.golfapp.ui.gallery.GalleryFragment;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private FragmentHomeBinding binding;
    Button newRoundBtn, viewHistoryBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.accountName;
        textView.setText(GlobalVariables.getInstance().getUserName());

        newRoundBtn = binding.newRoundButton;
        viewHistoryBtn = binding.historyButton;

        viewHistoryBtn.setOnClickListener(this);

        newRoundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        return root;
    }

    @Override
    public void onClick(View view) {
        Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_history);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}