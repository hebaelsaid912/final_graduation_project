package com.example.android.graduationproject.ui.phone_verifying;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendOTPSMS  {
    private  String phoneNo = "";
    private String mVerificationID;
    private FirebaseAuth mAuth;
    public  boolean FLAG_PROCESS_DONE = false;
   public Activity activity ;
    public String FinalCode="";
    DialogFragment fragment;
    public SendOTPSMS(String phoneNo , Activity activity) {
        this.phoneNo = phoneNo;
        this.activity = activity;

        mAuth = FirebaseAuth.getInstance();
        sendVerificationCodeToUser(phoneNo);
       //  mAuth.getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);
        // mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

    }


    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+20" + phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code=phoneAuthCredential.getSmsCode();
                    System.out.println( "Code :  " + code);
                    FinalCode = code;
                    System.out.println("onVerificationCompleted :  1" );
                    if(code !=null){
                        verifyCode(code);
                        System.out.println("onVerificationCompleted :  3" );
                        RegisterViewModel registerViewModel = new RegisterViewModel();
                        registerViewModel.setCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(e.getMessage());
                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    mVerificationID = s;
                    System.out.println("onVerificationCompleted :  4" );
                }
            };
    private void verifyCode(String code) {
        PhoneAuthCredential credential =PhoneAuthProvider.getCredential(mVerificationID,code);
        System.out.println("onVerificationCompleted :  2" );
        sign_in(credential);
    }

    private void sign_in(PhoneAuthCredential credential) {
        FirebaseAuth auth =FirebaseAuth.getInstance();
        auth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(activity, "Phone number verified successfully", Toast.LENGTH_SHORT).show();
                    System.out.println("onVerificationCompleted :  5" );
                    FLAG_PROCESS_DONE = true;
                    Intent intent = new Intent(activity.getBaseContext(),No_Invitation.class);
                    activity.startActivity(intent);

                }
                else{
                    Toast.makeText(activity,"Phone number not verified " , Toast.LENGTH_SHORT).show();
                    System.out.println("Error " + task.getException().getMessage());
                    System.out.println("onVerificationCompleted :  6" );

                }

            }
        });
    }
public String getFinalOTPCode(){
        return FinalCode;
}

}
