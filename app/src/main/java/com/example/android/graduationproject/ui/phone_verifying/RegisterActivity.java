package com.example.android.graduationproject.ui.phone_verifying;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.graduationproject.R;
import com.example.android.graduationproject.databinding.ActivityRegisterBinding;
import com.example.android.graduationproject.ui.StatusBar;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    public String Number_entered_by_user, code_by_system;
    Button getcode;
    TextInputLayout phoneNumber;
    DialogFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding Binding =
                DataBindingUtil.setContentView(this, R.layout.activity_register);
        new StatusBar(this,R.color.browser_actions_bg_grey);
        Binding.getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
                String phoneNo = Binding.signupPhoneNumber.getEditText().getText().toString().trim();
                fragment =  DialogFragment.newInstance(phoneNo);
                fragment.show(getSupportFragmentManager(), null);
            }
        });
        Binding.signupBackImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                    Manifest.permission.RECEIVE_SMS
            }, 100);
        }
    }


   /* @Override
    protected void onRestart() {
        super.onRestart();
        if(fragment != null){
            fragment.dismiss();
        }

    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}