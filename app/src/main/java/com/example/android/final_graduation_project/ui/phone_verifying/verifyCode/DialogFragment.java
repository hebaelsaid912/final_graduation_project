package com.example.android.final_graduation_project.ui.phone_verifying.verifyCode;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.final_graduation_project.R;
import com.example.android.final_graduation_project.SERVER.ConnectRetrofit;
import com.example.android.final_graduation_project.pojo.OTP.VerifyOTPCode;
import com.example.android.final_graduation_project.SERVER.otp.Phone_ApiInterface;
import com.example.android.final_graduation_project.databinding.FragmentDialogBinding;
import com.example.android.final_graduation_project.ui.phone_verifying.createUser.CreateUserActivity;
import com.example.android.final_graduation_project.ui.phone_verifying.sendOtp.RegisterViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    private String TOAST_TAG = "SendOTP";
    private static final int SUCCESS_CODE = 200;
    private static final int INVALID_VERIFICATION_CODE = 422;
    private static final int ALREADY_USED_VERIFICATION_CODE = 400;
    private static final String ARG_VERIFYING_PHONE = "phoneNumber";
    private static final String ARG_VERIFYING_CODE = "apiCode";
    private static final String ARG_VERIFYING_OTP_CODE = "otpCode";

    private static final int VERIFYING_CODE = 200;
    private int flag = 0;

    private String mVerifying_phone;
    private int mVerifying_code ;
    private String mVerifying_otpCode ;
    private HashMap<String , Object> data;
    Retrofit retrofit;
    Phone_ApiInterface phone_apiInterface;
    FragmentDialogBinding binding;
    SetCodeViewModel setCodeViewModel;
    VerifyCodeViewModel verifyCodeViewModel;
    String apiToken;
    String refreshToken;

    public DialogFragment() {

    }
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_bg);
        getDialog().setCanceledOnTouchOutside(false);
    }
    public static DialogFragment newInstance(String mVerifying_phone ,
                                             int  mVerifying_code  , String otpCode) {
        DialogFragment fragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VERIFYING_PHONE, mVerifying_phone);
        args.putInt(ARG_VERIFYING_CODE, mVerifying_code);
        args.putString(ARG_VERIFYING_OTP_CODE, otpCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setCodeViewModel = new ViewModelProvider(requireActivity()).get(SetCodeViewModel.class);
         verifyCodeViewModel = new ViewModelProvider(requireActivity()).get(VerifyCodeViewModel.class);
        if (getArguments() != null && getArguments().getInt(ARG_VERIFYING_CODE) == VERIFYING_CODE) {
            mVerifying_phone = getArguments().getString(ARG_VERIFYING_PHONE);
            mVerifying_otpCode = getArguments().getString(ARG_VERIFYING_OTP_CODE);
            Log.i("Dialog",mVerifying_phone);
            Log.i("Dialog",mVerifying_otpCode);
            setCodeViewModel.verifyCode(getArguments().getString(ARG_VERIFYING_OTP_CODE)+"");
            data = new HashMap<>();
            data.put("phone" , mVerifying_phone);
            data.put("code" , mVerifying_otpCode);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dialog,container,false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(setCodeViewModel);
        binding.executePendingBindings();
        binding.confirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = binding.pinView.getText().toString().trim();
                if(s.equals(mVerifying_otpCode)) {
                    verifyCodeViewModel.verifyCode(data);
                }else{
                    Toast.makeText(getContext(),"please enter correct code! " ,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        verifyCodeViewModel.verifyCodeMutableLiveData.observe(this, new Observer<VerifyOTPCode>() {
            @Override
            public void onChanged(VerifyOTPCode verifyOTPCode) {
                if (verifyCodeViewModel.verifyCodeMutableLiveData.getValue().getCode() == SUCCESS_CODE){
                    Toast.makeText(getActivity().getBaseContext(),
                            verifyCodeViewModel.verifyCodeMutableLiveData.getValue().getMessage(),
                            Toast.LENGTH_LONG).show();
                    apiToken = verifyCodeViewModel.verifyCodeMutableLiveData.getValue().getData().getToken();
                    refreshToken = verifyCodeViewModel.verifyCodeMutableLiveData.getValue().getData().getToken();
                    Intent intent = new Intent(getActivity().getBaseContext() , CreateUserActivity.class);
                    intent.putExtra("token", apiToken);
                    intent.putExtra("refreshToken", refreshToken);
                    Pair[] pairs = new Pair[1];
                    pairs[0] =new Pair<View,String>(binding.confirmCode,"openCreateUser");
                    ActivityOptions options =ActivityOptions.makeSceneTransitionAnimation(getActivity(),pairs);
                    startActivity(intent,options.toBundle());
                    dismiss();

                }else{
                    Toast.makeText(getActivity().getBaseContext(),
                            verifyCodeViewModel.verifyCodeMutableLiveData.getValue().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        return binding.getRoot();
    }


    /* @Override
    public void onStop() {
        super.onStop();
        dismiss();
    }*/

}