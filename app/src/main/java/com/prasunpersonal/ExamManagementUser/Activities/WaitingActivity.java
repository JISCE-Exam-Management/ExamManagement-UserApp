package com.prasunpersonal.ExamManagementUser.Activities;

import static com.prasunpersonal.ExamManagementUser.App.ME;
import static com.prasunpersonal.ExamManagementUser.App.PREFERENCES;
import static com.prasunpersonal.ExamManagementUser.App.QUEUE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementUser.Helpers.API;
import com.prasunpersonal.ExamManagementUser.Models.User;
import com.prasunpersonal.ExamManagementUser.databinding.ActivityWaitingBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WaitingActivity extends AppCompatActivity {
    ActivityWaitingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaitingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.waitingToolbar);
        binding.waitingRefresher.setOnRefreshListener(this::updateUi);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        binding.waitingRefresher.setRefreshing(true);
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?user=%s", API.USER_BY_ID, ME.get_id()), null, response -> {
            binding.waitingRefresher.setRefreshing(false);
            ME = new Gson().fromJson(response.toString(), User.class);
            if (ME.isVerified()) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        }, error -> {
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            binding.waitingRefresher.setRefreshing(false);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}