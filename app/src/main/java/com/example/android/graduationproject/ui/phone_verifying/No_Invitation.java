package com.example.android.graduationproject.ui.phone_verifying;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.android.graduationproject.R;
import com.example.android.graduationproject.databinding.ActivityNoInvitationBinding;
import com.example.android.graduationproject.ui.StatusBar;

public class No_Invitation extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNoInvitationBinding binding =
                DataBindingUtil.setContentView(this,R.layout.activity_no__invitation);
        new StatusBar(this,R.color.browser_actions_bg_grey);
        binding.noInvitationBackImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}