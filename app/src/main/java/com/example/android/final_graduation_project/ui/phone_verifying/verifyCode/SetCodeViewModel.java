package com.example.android.final_graduation_project.ui.phone_verifying.verifyCode;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class SetCodeViewModel extends ViewModel {
    private String TOAST_TAG = "SendOTP";
    public MutableLiveData<String> codeMutableLiveData = new MutableLiveData<>();
    void verifyCode(String code){
        codeMutableLiveData.setValue(code);
        Log.i(TOAST_TAG,"code : " +codeMutableLiveData.getValue().toString());
    }
}
