package com.prasunpersonal.ExamManagementUser.Fragments;

import static com.prasunpersonal.ExamManagementUser.App.ME;
import static com.prasunpersonal.ExamManagementUser.App.PREFERENCES;
import static com.prasunpersonal.ExamManagementUser.App.QUEUE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prasunpersonal.ExamManagementUser.Activities.WaitingActivity;
import com.prasunpersonal.ExamManagementUser.Helpers.API;
import com.prasunpersonal.ExamManagementUser.Models.User;
import com.prasunpersonal.ExamManagementUser.databinding.FragmentSignupBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class SignupFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentSignupBinding binding;

    public SignupFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);

        binding.signup.setOnClickListener(v -> {
            if (binding.name.getText().toString().trim().isEmpty()) {
                binding.name.setError("Name is required!");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).matches()) {
                binding.email.setError("A valid email is required!");
                return;
            }
            if (binding.pass1.getText().toString().trim().length() < 6) {
                binding.pass1.setError("Enter a valid password of minimum length 6!");
                return;
            }
            if (!binding.pass2.getText().toString().trim().equals(binding.pass1.getText().toString().trim())) {
                binding.pass2.setError("Passwords doesn't match!");
                return;
            }

            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.USER_SIGNUP, null, response -> {
                ME = new Gson().fromJson(response.toString(), User.class);
                if (binding.remember.isChecked()) {
                    PREFERENCES.edit().putString("EMAIL", ME.getEmail()).putString("PASSWORD", ME.getPassword()).apply();
                }
                startActivity(new Intent(requireContext(), WaitingActivity.class));
                requireActivity().finish();
            }, error -> {
                Log.d(TAG, "onCreateView: ", error);
                Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(new User(binding.name.getText().toString().trim(), binding.email.getText().toString().trim(), binding.pass1.getText().toString().trim())).getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

        return binding.getRoot();
    }
}