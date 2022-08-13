package com.prasunpersonal.ExamManagementUser.Activities;

import static com.prasunpersonal.ExamManagementUser.App.ME;
import static com.prasunpersonal.ExamManagementUser.App.QUEUE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prasunpersonal.ExamManagementUser.Adapters.AttendanceAdapter;
import com.prasunpersonal.ExamManagementUser.Helpers.API;
import com.prasunpersonal.ExamManagementUser.Models.Exam;
import com.prasunpersonal.ExamManagementUser.Models.Hall;
import com.prasunpersonal.ExamManagementUser.Models.Student;
import com.prasunpersonal.ExamManagementUser.R;
import com.prasunpersonal.ExamManagementUser.databinding.ActivityExamDetailsBinding;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ExamDetailsActivity extends AppCompatActivity {
    ActivityExamDetailsBinding binding;
    private final String TAG = this.getClass().getSimpleName();
    private String examId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExamDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.examDetailsToolbar);
        examId = getIntent().getStringExtra("EXAM_ID");

        binding.examDetailsToolbar.setNavigationOnClickListener(v -> finish());

        binding.examDetailsRefresh.setOnRefreshListener(this::updateUi);
        updateUi();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_details_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void updateUi() {
        binding.examDetailsRefresh.setRefreshing(true);
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?exam=%s", API.GET_EXAM_BY_ID, examId), null, response -> {
            Exam exam = new Gson().fromJson(response.toString(), Exam.class);
            binding.examCategory.setText(String.format("%s / %s / %s / %s / %s / %s", exam.getDegree(), exam.getCourse(), exam.getStream(), exam.getRegulation(), exam.getSemester(), exam.getPaper().getCode()));
            binding.examItemName.setText(exam.getName());
            binding.examItemDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date(exam.getExamStartingTime())));
            binding.examItemTime.setText(String.format("%s - %s", new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date(exam.getExamStartingTime())), new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date(exam.getExamEndingTime()))));
            binding.examItemPaper.setText(exam.getPaper().toString());
            List<String> halls = new ArrayList<>();
            halls.add("Select Hall");
            halls.addAll(exam.getHalls().stream().map(Hall::getName).collect(Collectors.toList()));
            binding.allHalls.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, halls) {
                @Override
                public boolean isEnabled(int position) {
                    return position > 0;
                }
            });
            binding.allHalls.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        Hall hall = exam.getHalls().get(position - 1);
                        binding.candidatesFetchingProgress.setVisibility(View.VISIBLE);
                        List<Student> students = new ArrayList<>();
                        binding.allCandidates.setLayoutManager(new LinearLayoutManager(ExamDetailsActivity.this));
                        binding.allCandidates.setAdapter(new AttendanceAdapter(hall, students, (student, present, studentPosition) -> hall.getCandidates().put(student.get_id(), present)));
                        QUEUE.add(new JsonArrayRequest(Request.Method.POST, API.HALL_CANDIDATES, null, response -> {
                            students.addAll(new Gson().fromJson(response.toString(), new TypeToken<List<Student>>() {
                            }.getType()));
                            Log.d(TAG, "onItemSelected: "+students);
                            Objects.requireNonNull(binding.allCandidates.getAdapter()).notifyDataSetChanged();
                            binding.candidatesFetchingProgress.setVisibility(View.GONE);

                            if (System.currentTimeMillis() >= exam.getAttendanceStartingTime() && System.currentTimeMillis() <= exam.getAttendanceEndingTime()) {
                                binding.updateAttendanceArea.setVisibility(View.VISIBLE);
                                new CountDownTimer(exam.getExamEndingTime() - System.currentTimeMillis(), 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        binding.attendanceTimer.setText(convertMillisToTime(millisUntilFinished));
                                    }

                                    @Override
                                    public void onFinish() {
                                        Toast.makeText(ExamDetailsActivity.this, "Attendance submission time up", Toast.LENGTH_SHORT).show();
                                        binding.updateAttendanceArea.setVisibility(View.GONE);
                                    }
                                }.start();
                            } else {
                                binding.updateAttendanceArea.setVisibility(View.GONE);
                            }

                            binding.submitAttendance.setOnClickListener(v -> {
                                if (hall.getCandidates().containsValue(null)) {
                                    Toast.makeText(ExamDetailsActivity.this, "Please select attendance for every candidates!", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                binding.submitAttendance.setEnabled(false);
                                binding.addSemesterProgress.setVisibility(View.VISIBLE);

                                hall.setUpdatedBy(ME.get_id());
                                hall.setUpdatedTime(System.currentTimeMillis());

                                QUEUE.add(new JsonObjectRequest(Request.Method.PATCH, String.format("%s?exam=%s", API.UPDATE_HALL, exam.get_id()), null, newHall -> {
                                    binding.submitAttendance.setEnabled(true);
                                    binding.addSemesterProgress.setVisibility(View.GONE);
                                    Toast.makeText(ExamDetailsActivity.this, "Attendance updated successfully.", Toast.LENGTH_SHORT).show();
                                }, error -> {
                                    Log.d(TAG, "onCreateView: ", error);
                                    Toast.makeText(ExamDetailsActivity.this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                                    binding.submitAttendance.setEnabled(true);
                                    binding.addSemesterProgress.setVisibility(View.GONE);
                                }) {
                                    @Override
                                    public byte[] getBody() {
                                        return new Gson().toJson(hall).getBytes(StandardCharsets.UTF_8);
                                    }
                                });
                            });
                        }, error -> {
                            binding.candidatesFetchingProgress.setVisibility(View.GONE);
                            Log.d(TAG, "onCreate: ", error);
                            Toast.makeText(ExamDetailsActivity.this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                        }) {
                            @Override
                            public byte[] getBody() {
                                Map<String, List<String>> body = new HashMap<>();
                                body.put("candidates", new ArrayList<>(hall.getCandidates().keySet()));
                                return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                            }
                        }).setRetryPolicy(new DefaultRetryPolicy());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            binding.examDetailsRefresh.setRefreshing(false);
        }, error -> {
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            binding.examDetailsRefresh.setRefreshing(false);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }



    private String convertMillisToTime(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}