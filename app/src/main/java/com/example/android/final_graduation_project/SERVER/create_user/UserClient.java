package com.example.android.final_graduation_project.SERVER.create_user;

import com.example.android.final_graduation_project.SERVER.ConnectRetrofit;
import com.example.android.final_graduation_project.pojo.CreateUser.User;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.Retrofit;

public class UserClient {
    private  User_ApiInterface userApiInterface;
    private static UserClient INSTANCE;
    public UserClient() {
        Retrofit retrofit = ConnectRetrofit.getUserResponse();
        userApiInterface = retrofit.create(User_ApiInterface.class);
    }
    public static UserClient getINSTANCE() {
        if(INSTANCE == null ){
            INSTANCE = new UserClient();
        }
        return INSTANCE;
    }
    public Observable<User> getUser(HashMap data, String refreshToken){
        return userApiInterface.setUserData(refreshToken , data);
    }
}
