package com.prasunpersonal.ExamManagementUser.Activities;

import static com.prasunpersonal.ExamManagementUser.App.ME;
import static com.prasunpersonal.ExamManagementUser.App.PREFERENCES;
import static com.prasunpersonal.ExamManagementUser.App.QUEUE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementUser.Helpers.API;
import com.prasunpersonal.ExamManagementUser.Models.User;
import com.prasunpersonal.ExamManagementUser.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (PREFERENCES.contains("EMAIL") && PREFERENCES.contains("PASSWORD")) {
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.USER_LOGIN, null, response -> {
                ME = new Gson().fromJson(response.toString(), User.class);
                if (ME.isVerified()) {
                    startActivity(new Intent(this, HomeActivity.class));
                } else {
                    startActivity(new Intent(this, WaitingActivity.class));
                }
                finish();
            }, error -> {
                Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public byte[] getBody() {
                    Map<String, String> body = new HashMap<>();
                    body.put("email", PREFERENCES.getString("EMAIL", ""));
                    body.put("password", PREFERENCES.getString("PASSWORD", ""));
                    return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(this, AuthenticationActivity.class));
                finish();
            }, 2000);
        }
    }
}