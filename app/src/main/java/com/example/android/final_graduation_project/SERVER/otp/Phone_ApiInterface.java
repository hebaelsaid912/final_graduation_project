package com.example.android.final_graduation_project.SERVER.otp;

import com.example.android.final_graduation_project.pojo.CreateUser.User;
import com.example.android.final_graduation_project.pojo.OTP.SendOTPCodeSuccessfully;
import com.example.android.final_graduation_project.pojo.OTP.VerifyOTPCode;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Phone_ApiInterface {
    //"https://yalla-dardasha-user.herokuapp.com/api/v1/";
    //"https://yalla-dardasha-otp.herokuapp.com/api/v1/";
   @POST("otp/code")
    public Observable<SendOTPCodeSuccessfully> getOTPCode(@Body HashMap<String , Object> phone);
    @POST("otp/verify")
    public Observable<VerifyOTPCode> verifyPhoneNumber(@Body HashMap<String , Object> data);

}
