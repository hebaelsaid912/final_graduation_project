package com.example.android.graduationproject.ui.phone_verifying;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
    public MutableLiveData<String> phoneNumMutableLiveData = new MutableLiveData<>();
    public void setCode(String code){
        phoneNumMutableLiveData.setValue(code);
        Log.i("Dialog","mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm + " + phoneNumMutableLiveData.getValue());
    }
    public MutableLiveData<String> getName() {
        return phoneNumMutableLiveData;
    }
}
