package com.prasunpersonal.ExamManagementUser.Activities;

import static com.prasunpersonal.ExamManagementUser.App.ME;
import static com.prasunpersonal.ExamManagementUser.App.PREFERENCES;
import static com.prasunpersonal.ExamManagementUser.App.QUEUE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementUser.Adapters.ExamAdapter;
import com.prasunpersonal.ExamManagementUser.Helpers.API;
import com.prasunpersonal.ExamManagementUser.Models.Exam;
import com.prasunpersonal.ExamManagementUser.R;
import com.prasunpersonal.ExamManagementUser.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.homeToolbar);

        binding.homeToolbar.setNavigationOnClickListener(v -> binding.homeDrawar.open());
        View navHeader = binding.homeNavigation.getHeaderView(0);
        ((TextView) navHeader.findViewById(R.id.navHeaderName)).setText(ME.getName());
        ((TextView) navHeader.findViewById(R.id.navHeaderEmail)).setText(ME.getEmail());

        binding.homeNavigation.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.logout) {
                PREFERENCES.edit().remove("EMAIL").remove("PASSWORD").apply();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            }
            return true;
        });

        updateUi();
        binding.examRefresher.setOnRefreshListener(this::updateUi);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateUi() {
        binding.examRefresher.setRefreshing(true);
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, API.ONGOING_EXAMS, null, response -> {
            ArrayList<Exam> exams = new Gson().fromJson(response.toString(), new TypeToken<List<Exam>>() {
            }.getType());
            try {
                binding.allExams.setLayoutManager(new LinearLayoutManager(this));
                binding.allExams.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                binding.allExams.setAdapter(new ExamAdapter(exams, (exam, position) -> startActivity(new Intent(this, ExamDetailsActivity.class).putExtra("EXAM_ID", exam.get_id()))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            binding.examRefresher.setRefreshing(false);
        }, error -> {
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            binding.examRefresher.setRefreshing(false);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }

}