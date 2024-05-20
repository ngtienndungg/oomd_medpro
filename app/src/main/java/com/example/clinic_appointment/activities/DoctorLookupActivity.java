package com.example.clinic_appointment.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.databinding.ActivityDoctorLookupBinding;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.utilities.Constants;

import java.util.Objects;

public class DoctorLookupActivity extends AppCompatActivity {
    private HealthFacility selectedHealthFacility;
    private Department selectedDepartment;
    private ActivityDoctorLookupBinding binding;
    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent intent = result.getData();
                Integer resultCode = result.getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    if (Objects.requireNonNull(intent).getSerializableExtra(Constants.KEY_HEALTH_FACILITY) != null) {
                        selectedHealthFacility = (HealthFacility) intent.getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
                        binding.etHealthFacility.setText(Objects.requireNonNull(selectedHealthFacility).getName());
                    } else if (Objects.requireNonNull(intent).getSerializableExtra(Constants.KEY_DEPARTMENT) != null) {
                        selectedDepartment = (Department) intent.getSerializableExtra(Constants.KEY_DEPARTMENT);
                        binding.etDepartment.setText(Objects.requireNonNull(selectedDepartment).getName());
                    }
                } else if (resultCode.equals(Constants.RESULT_ALL_MATCH)) {
                    String returnType = Objects.requireNonNull(intent).getStringExtra(Constants.RETURN_TYPE);
                    if (Objects.equals(returnType, Constants.TYPE_HOSPITAL)) {
                        Objects.requireNonNull(binding.etHealthFacility.getText()).clear();
                    } else if (Objects.equals(returnType, Constants.TYPE_DEPARTMENT)) {
                        Objects.requireNonNull(binding.etDepartment.getText()).clear();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorLookupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        eventHandling();
    }

    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.etHealthFacility.setOnClickListener(v -> launchSelectActivity(HealthFacilitySelectionActivity.class));
        binding.etDepartment.setOnClickListener(v -> launchSelectActivity(DepartmentSelectionActivity.class));
        binding.tvSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, DoctorSelectionActivity.class);
            intent.putExtra(Constants.KEY_HEALTH_FACILITY, selectedHealthFacility);
            intent.putExtra(Constants.KEY_DEPARTMENT, selectedDepartment);
            intent.putExtra(Constants.KEY_NAME, Objects.requireNonNull(binding.etName.getText()).toString().trim());
            intent.putExtra(Constants.KEY_SOURCE_ACTIVITY, "SearchDoctor");
            startActivity(intent);
        });
    }

    private void launchSelectActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(Constants.KEY_SOURCE_ACTIVITY, "SearchSchedule");
        mStartForResult.launch(intent);
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
}