package com.prasunpersonal.ExamManagementUser.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.prasunpersonal.ExamManagementUser.Adapters.PagerAdapter;
import com.prasunpersonal.ExamManagementUser.Fragments.LoginFragment;
import com.prasunpersonal.ExamManagementUser.Fragments.SignupFragment;
import com.prasunpersonal.ExamManagementUser.databinding.ActivityAuthenticationBinding;

import java.util.ArrayList;

public class AuthenticationActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ActivityAuthenticationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new LoginFragment());
        fragments.add(new SignupFragment());

        binding.authenticationTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.authenticationViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.authenticationViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.authenticationTab.selectTab(binding.authenticationTab.getTabAt(position));
            }
        });

        binding.authenticationViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
    }
}