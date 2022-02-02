package com.example.golfapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.golfapp.GlobalVariables;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(GlobalVariables.getInstance().getUserName());
    }

    public LiveData<String> getText() {
        return mText;
    }
}