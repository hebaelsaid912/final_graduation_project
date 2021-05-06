package com.example.android.graduationproject.ui.phone_verifying;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.chaos.view.PinView;
import com.example.android.graduationproject.R;
import com.example.android.graduationproject.databinding.FragmentDialogBinding;

import org.jetbrains.annotations.NotNull;

public class DialogFragment extends androidx.fragment.app.DialogFragment {

    private static final String ARG_VERIFYING_PHONE = "phoneNumber";

    private String mVerifying_phone;
    FragmentDialogBinding binding;
    RegisterViewModel registerViewModel;

    SendOTPSMS sendSMS;

    public DialogFragment() {

    }
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_bg);
        getDialog().setCanceledOnTouchOutside(false);
    }
    public static DialogFragment newInstance(String mVerifying_phone) {
        DialogFragment fragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VERIFYING_PHONE, mVerifying_phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        if (getArguments() != null) {
            mVerifying_phone = getArguments().getString(ARG_VERIFYING_PHONE);
            Log.i("Dialog",mVerifying_phone);
            sendSMS = new SendOTPSMS(mVerifying_phone , getActivity());
            Log.i("Dialog",sendSMS.FinalCode);
            registerViewModel.setCode(sendSMS.FinalCode);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_dialog,container,false);
        binding.setViewModel(registerViewModel);
        binding.setLifecycleOwner(this);
        binding.getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = binding.pinView.getText().toString().trim();
                if(sendSMS.getFinalOTPCode().equals(s)){
                    Intent intent = new Intent(getContext(),No_Invitation.class);
                    startActivity(intent);
                    dismiss();
                }else {
                    Toast.makeText(getContext(),"not valid code " , Toast.LENGTH_LONG).show();
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