package com.example.golfapp.ui.history;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.golfapp.SearchResultActivity;
import com.example.golfapp.databinding.FragmentHistoryBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private DatePickerDialog startDatePickerDialog;
    private TextView startDate, endDate;
    private ConstraintLayout startCalendar, endCalendar;
    private Button searchBtn;
    private Date endDateFormat, startDateFormat;
    private int msDate, msMonth, msYear, meDate, meMonth, meYear;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        searchBtn = binding.searchButton;
        startDate = binding.startDate;
        startCalendar = binding.constraintLayout1;
        endDate = binding.endDate;
        endCalendar = binding.constraintLayout2;

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
                        try {
                            startDateFormat = new SimpleDateFormat("dd/MM/yyy").parse(day + "/" + month + "/" + year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, msYear, msMonth, msDate);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.setTitle("Start Date");
                datePickerDialog.show();
                startDate.setError(null);
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
                        try {
                            endDateFormat = new SimpleDateFormat("dd/MM/yyy").parse(day + "/" + month + "/" + year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, meYear, meMonth, meDate);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.setTitle("End Date");
                datePickerDialog.show();
                endDate.setError(null);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSearchResults();
            }
        });

        return  root;
    }

    private void getSearchResults() {
        if (startDate.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Start Date cannot be empty", Toast.LENGTH_SHORT).show();
            startDate.setError("Start Date Required");
        } else if (endDate.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "End Date cannot be empty", Toast.LENGTH_SHORT).show();
            endDate.setError("End Date Required");
        } else if (endDateFormat.before(startDateFormat)) {
            Toast.makeText(getContext(), "Start Date must be before End Date", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(getActivity(), SearchResultActivity.class);
            i.putExtra("startDate", startDateFormat);
            i.putExtra("endDate", endDateFormat);
            startActivity(i);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}