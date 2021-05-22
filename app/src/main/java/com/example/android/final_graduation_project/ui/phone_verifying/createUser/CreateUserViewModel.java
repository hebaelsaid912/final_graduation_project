package com.example.android.final_graduation_project.ui.phone_verifying.createUser;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.final_graduation_project.SERVER.create_user.UserClient;
import com.example.android.final_graduation_project.pojo.CreateUser.User;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateUserViewModel extends ViewModel {
    private String TOAST_TAG = "SendOTP";
    MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    Observable observable;
    Observer<User> observer;

    public void setUser(HashMap data, String refreshToken) {
        observable = UserClient.getINSTANCE().getUser(data, refreshToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observer = new Observer<User>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onNext(@NotNull User user) {
                if (user != null) {
                    userMutableLiveData.setValue(user);
                    Log.i(TOAST_TAG, user.getCode() + "");
                    Log.i(TOAST_TAG, user.getStatus());
                    Log.i(TOAST_TAG, user.getMessage());
                } else {
                    Log.i(TOAST_TAG, "onError : " + "User is null");
                }
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.i(TOAST_TAG, "onError : " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(observer);
    }

}
