package com.prasunpersonal.ExamManagementUser;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.prasunpersonal.ExamManagementUser.Models.User;

public class App extends Application {
    private final String TAG = this.getClass().getSimpleName();
    public static RequestQueue QUEUE;
    public static User ME;
    public static SharedPreferences PREFERENCES;

    @Override
    public void onCreate() {
        super.onCreate();
        QUEUE = Volley.newRequestQueue(this);
        PREFERENCES = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }
}