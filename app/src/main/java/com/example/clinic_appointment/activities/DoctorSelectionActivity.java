package com.example.clinic_appointment.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.ScheduleAdapter;
import com.example.clinic_appointment.adapters.SelectDoctorAdapter;
import com.example.clinic_appointment.databinding.ActivitySelectDoctorBinding;
import com.example.clinic_appointment.listeners.DoctorListener;
import com.example.clinic_appointment.listeners.ScheduleListener;
import com.example.clinic_appointment.models.AppointmentTime.AppointmentTime;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.Doctor.DoctorResponse;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.models.Schedule.DetailSchedule;
import com.example.clinic_appointment.models.Schedule.ScheduleResponse;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorSelectionActivity extends AppCompatActivity implements DoctorListener, ScheduleListener {
    private static List<Doctor> originalDoctors = new ArrayList<>();
    private static List<Doctor> dynamicDoctors;
    private final Handler handler = new Handler();
    private final long SEARCH_DELAY_MILLIS = 300;
    private ActivitySelectDoctorBinding binding;
    private Department selectedDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    private void initiate() {
        if (getIntent().getStringExtra(Constants.KEY_SOURCE_ACTIVITY) == null) {
            if (getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT) != null) {
                selectedDepartment = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
            }
            HealthFacility healthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
            Call<DoctorResponse> call = RetrofitClient.getPublicAppointmentService().getDoctorByDepartmentAndHealthFacility(selectedDepartment != null ? selectedDepartment.getId() : null, Objects.requireNonNull(healthFacility).getId(), null);
            call.enqueue(new Callback<DoctorResponse>() {
                @Override
                public void onResponse(@NonNull Call<DoctorResponse> call, @NonNull Response<DoctorResponse> response) {
                    if (response.body() != null && response.body().isSuccess() && response.body().getDoctors().size() > 0) {
                        originalDoctors = response.body().getDoctors();
                        dynamicDoctors = new ArrayList<>(originalDoctors);
                        SelectDoctorAdapter adapter = new SelectDoctorAdapter(DoctorSelectionActivity.this, dynamicDoctors);
                        binding.rvDoctor.setAdapter(adapter);
                        binding.rvDoctor.setVisibility(View.VISIBLE);
                        binding.pbLoading.setVisibility(View.GONE);
                    } else {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DoctorResponse> call, @NonNull Throwable t) {
                    displayError();
                }
            });
        } else if (Objects.equals(getIntent().getStringExtra(Constants.KEY_SOURCE_ACTIVITY), "SearchSchedule")) {
            AppointmentTime appointmentTime = (AppointmentTime) getIntent().getSerializableExtra(Constants.KEY_TIME);
            HealthFacility healthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
            Department department = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
            long start = getIntent().getLongExtra(Constants.KEY_START_DATE, -1);
            long end = getIntent().getLongExtra(Constants.KEY_END_DATE, -1);
            Call<ScheduleResponse> call = RetrofitClient.getPublicAppointmentService().getSchedules(
                    start == -1 ? null : start,
                    end == -1 ? null : end,
                    (appointmentTime != null) ? appointmentTime.getTimeNumber() : null,
                    (department) != null ? department.getName() : null,
                    (healthFacility) != null ? healthFacility.getName() : null,
                    null,
                    "-ratings"
            );
            call.enqueue(new Callback<ScheduleResponse>() {
                @Override
                public void onResponse(@NonNull Call<ScheduleResponse> call, @NonNull Response<ScheduleResponse> response) {
                    if (response.body() != null && response.body().isSuccess() && response.body().getSchedules().size() > 0) {
                        Set<String> uniqueIds = new HashSet<>();
                        List<DetailSchedule> schedules = response.body().getSchedules();
                        schedules.removeIf(detailSchedule -> !uniqueIds.add(detailSchedule.getDoctor().getDoctorInformation().getId()));
                        ScheduleAdapter adapter = new ScheduleAdapter(DoctorSelectionActivity.this, schedules);
                        binding.rvDoctor.setAdapter(adapter);
                        binding.rvDoctor.setVisibility(View.VISIBLE);
                        binding.pbLoading.setVisibility(View.GONE);
                    } else {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ScheduleResponse> call, @NonNull Throwable t) {
                    binding.tvNotFound.setVisibility(View.VISIBLE);
                }
            });
        } else if (Objects.equals(getIntent().getStringExtra(Constants.KEY_SOURCE_ACTIVITY), "SearchDoctor")) {
            HealthFacility healthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
            Department department = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
            String name = getIntent().getStringExtra(Constants.KEY_NAME);
            Call<DoctorResponse> call = RetrofitClient.getPublicAppointmentService().getDoctorByDepartmentAndHealthFacility(department != null ? department.getId() : null, healthFacility != null ? healthFacility.getId() : null, name);
            call.enqueue(new Callback<DoctorResponse>() {
                @Override
                public void onResponse(@NonNull Call<DoctorResponse> call, @NonNull Response<DoctorResponse> response) {
                    if (response.body() != null && response.body().isSuccess() && response.body().getDoctors().size() > 0) {
                        originalDoctors = response.body().getDoctors();
                        dynamicDoctors = new ArrayList<>(originalDoctors);
                        SelectDoctorAdapter adapter = new SelectDoctorAdapter(DoctorSelectionActivity.this, dynamicDoctors);
                        binding.rvDoctor.setAdapter(adapter);
                        binding.rvDoctor.setVisibility(View.VISIBLE);
                        binding.pbLoading.setVisibility(View.GONE);
                    } else {
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DoctorResponse> call, @NonNull Throwable t) {
                    displayError();
                }
            });
        }
    }

    private void displayError() {
        binding.pbLoading.setVisibility(View.GONE);
        binding.llError.setVisibility(View.VISIBLE);
    }

    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> search(editable.toString().trim()), SEARCH_DELAY_MILLIS);
            }
        });
    }

    private void search(String name) {
        dynamicDoctors.clear();
        for (Doctor doctor : originalDoctors) {
            if (doctor.getDoctorInformation().getFullName().toLowerCase().contains(name)) {
                dynamicDoctors.add(doctor);
            }
        }
        displayList();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayList() {
        if (dynamicDoctors.size() > 0) {
            binding.tvNotFound.setVisibility(View.GONE);
            Objects.requireNonNull(binding.rvDoctor.getAdapter()).notifyDataSetChanged();
        } else {
            binding.tvNotFound.setVisibility(View.VISIBLE);
        }
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
    public void onClick(Doctor doctor) {
        if (Objects.equals(getIntent().getStringExtra(Constants.KEY_SOURCE_ACTIVITY), "SearchDoctor")) {
            Intent intent = new Intent(this, DoctorInformationActivity.class);
            intent.putExtra(Constants.KEY_DOCTOR, doctor);
            startActivity(intent);
        } else if (!Objects.equals(doctor.getScheduleString(), "Không có lịch khám")) {
            HealthFacility selectedHealthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
            Intent intent = new Intent(this, DateSelectionActivity.class);
            intent.putExtra(Constants.KEY_DOCTOR, doctor);
            intent.putExtra(Constants.KEY_DEPARTMENT, selectedDepartment != null ? selectedDepartment : doctor.getDepartmentInformation());
            intent.putExtra(Constants.KEY_HEALTH_FACILITY, selectedHealthFacility);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.this_doctor_dont_have_schedule), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(DetailSchedule detailSchedule) {
        Intent intent = new Intent(this, DateSelectionActivity.class);
        intent.putExtra(Constants.KEY_DETAIL_DOCTOR, detailSchedule.getDoctor());
        intent.putExtra(Constants.KEY_DEPARTMENT, detailSchedule.getDoctor().getDepartmentInformation());
        intent.putExtra(Constants.KEY_HEALTH_FACILITY, detailSchedule.getDoctor().getHealthFacility());
        startActivity(intent);
    }
}