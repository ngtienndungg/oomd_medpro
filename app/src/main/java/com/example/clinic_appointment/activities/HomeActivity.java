package com.example.clinic_appointment.activities;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.ViewPagerAdapter;
import com.example.clinic_appointment.databinding.ActivityDashboardBinding;
import com.example.clinic_appointment.databinding.LayoutDialogNotificationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;
    private Fragment currentFragment;
    private AlertDialog alertDialog;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
    }

    private void initiate() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        bottomNav = binding.bnvDashboard;
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNav.getMenu().findItem(R.id.menu_item_home).setChecked(true);
                        break;
                    case 1:
                        bottomNav.getMenu().findItem(R.id.menu_item_patient_profile).setChecked(true);
                        break;
                    case 2:
                        bottomNav.getMenu().findItem(R.id.menu_item_my_schedule).setChecked(true);
                        break;
                    case 3:
                        bottomNav.getMenu().findItem(R.id.menu_item_notification).setChecked(true);
                        break;
                    default:
                        bottomNav.getMenu().findItem(R.id.menu_item_account).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(item ->

        {
            switch (item.getItemId()) {
                case R.id.menu_item_home:
                    binding.viewPager.setCurrentItem(0);
                    break;
                case R.id.menu_item_patient_profile:
                    binding.viewPager.setCurrentItem(1);
                    break;
                case R.id.menu_item_my_schedule:
                    binding.viewPager.setCurrentItem(2);
                    break;
                case R.id.menu_item_notification:
                    binding.viewPager.setCurrentItem(3);
                    break;
                default:
                    binding.viewPager.setCurrentItem(4);
                    break;
            }
            return true;
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        if (!isNetworkAvailable()) {
            showNetworkUnavailableDialog();
        }
        super.onResume();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void showNetworkUnavailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutDialogNotificationBinding dialogNotificationBinding = LayoutDialogNotificationBinding.inflate(getLayoutInflater());
        builder.setView(dialogNotificationBinding.getRoot());
        alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogNotificationBinding.tvTitle.setText(getString(R.string.you_are_not_connected_to_internet));
        dialogNotificationBinding.tvContent.setText(getString(R.string.please_connect_to_the_internet));
        dialogNotificationBinding.tvAction.setText(getString(R.string.accept));
        dialogNotificationBinding.tvAction.setOnClickListener(v -> dismissDialog());
        dialogNotificationBinding.ivClose.setOnClickListener(v -> dismissDialog());
        alertDialog.show();
    }

    private void dismissDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}