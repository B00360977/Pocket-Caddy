package com.example.golfapp.ui.history;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.golfapp.databinding.FragmentHistoryBinding;

import java.util.Calendar;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private DatePickerDialog startDatePickerDialog;
    private EditText startDate, endDate;
    private ImageView startCalendar, endCalendar;
    private int msDate, msMonth, msYear, meDate, meMonth, meYear;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        startDate = binding.startDate;
        startCalendar = binding.startDatePicker;
        endDate = binding.endDate;
        endCalendar = binding.endDatePicker;

        startCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal = Calendar.getInstance();
                msDate = cal.get(Calendar.DATE);
                msMonth = cal.get(Calendar.MONTH);
                msYear = cal.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        startDate.setText(day + "/" + month + "/" + year);
                    }
                }, msYear, msMonth, msDate);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        endCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal = Calendar.getInstance();
                meDate = cal.get(Calendar.DATE);
                meMonth = cal.get(Calendar.MONTH);
                meYear = cal.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        endDate.setText(day + "/" + month + "/" + year);
                    }
                }, meYear, meMonth, meDate);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });




        return  root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}