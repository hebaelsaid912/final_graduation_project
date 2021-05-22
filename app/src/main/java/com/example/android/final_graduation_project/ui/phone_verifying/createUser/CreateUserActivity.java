package com.example.android.final_graduation_project.ui.phone_verifying.createUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.example.android.final_graduation_project.R;
import com.example.android.final_graduation_project.StatusBar;
import com.example.android.final_graduation_project.databinding.ActivityCreateUserBinding;
import com.example.android.final_graduation_project.pojo.CreateUser.User;
import com.example.android.final_graduation_project.ui.Home.HomeActivity;

import java.util.HashMap;

public class CreateUserActivity extends AppCompatActivity {
    private String TOAST_TAG = "SendOTP";
    private static final int SUCCESS_CODE = 200;
    private static final int INVALID_VERIFICATION_CODE = 422;
    private static final int ALREADY_USED_VERIFICATION_CODE = 400;
    private static final String ARG_USER_TOKEN = "token";
    private static final String ARG_USER_REFRESH_TOKEN = "refreshToken";
    ActivityCreateUserBinding binding;
    HashMap<String, Object> data;
    CreateUserViewModel createUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =
                DataBindingUtil.setContentView(this, R.layout.activity_create_user);
        new StatusBar(this, R.color.browser_actions_bg_grey);
        createUserViewModel = new ViewModelProvider(this).get(CreateUserViewModel.class);
        binding.createNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.createUserUserName.getEditText().getText().toString().trim();
                String username = binding.createUserUserUsername.getEditText().getText().toString().trim();
                data = new HashMap<>();
                data.put("name", name);
                data.put("username", username);
                String refreshToken = "Bearer " + getIntent().getStringExtra(ARG_USER_REFRESH_TOKEN);
                 Log.i(TOAST_TAG,refreshToken);
                createUserViewModel.setUser(data, refreshToken);
                binding.waitToCreateNewUser.setVisibility(View.VISIBLE);
            }
        });

        createUserViewModel.userMutableLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                binding.waitToCreateNewUser.setVisibility(View.GONE);
                if (createUserViewModel.userMutableLiveData.getValue().getCode() == SUCCESS_CODE) {
                    Toast.makeText(getBaseContext(), createUserViewModel.userMutableLiveData.getValue().getMessage()
                            , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(binding.createNewUser, "openHome");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CreateUserActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), createUserViewModel.userMutableLiveData.getValue().getMessage()
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}