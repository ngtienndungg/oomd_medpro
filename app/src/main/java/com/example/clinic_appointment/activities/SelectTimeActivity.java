package com.example.clinic_appointment.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.SelectTimeAdapter;
import com.example.clinic_appointment.databinding.ActivitySelectTimeBinding;
import com.example.clinic_appointment.databinding.LayoutDialogNotificationBinding;
import com.example.clinic_appointment.listeners.AppointmentTimeListener;
import com.example.clinic_appointment.models.AppointmentTime.AppointmentTime;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.models.Schedule.ScheduleExclude;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.SharedPrefs;

import java.util.List;
import java.util.Objects;

public class SelectTimeActivity extends AppCompatActivity implements AppointmentTimeListener {
    private final SharedPrefs sharedPrefs = SharedPrefs.getInstance();
    private ActivitySelectTimeBinding binding;
    private AlertDialog unLoginDialog;
    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Integer resultCode = result.getResultCode();
                if (resultCode == Activity.RESULT_OK) {
                    unLoginDialog.dismiss();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    private void initiate() {
        List<AppointmentTime> appointmentTimes;
        if (getIntent().getStringExtra(Constants.KEY_SOURCE_ACTIVITY) == null) {
            ScheduleExclude schedule = (ScheduleExclude) getIntent().getSerializableExtra(Constants.KEY_DATE);
            appointmentTimes = Objects.requireNonNull(schedule).getAppointmentTimes();
        } else {
            appointmentTimes = (List<AppointmentTime>) getIntent().getSerializableExtra(Constants.KEY_DATE);
            binding.rlColorIndicator.setVisibility(View.GONE);
            binding.tvInformation.setVisibility(View.GONE);
        }
        SelectTimeAdapter adapter = new SelectTimeAdapter(appointmentTimes, this);
        int numberOfColumns = 3;
        binding.rvAppointmentTimes.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        binding.rvAppointmentTimes.setAdapter(adapter);
        binding.rvAppointmentTimes.setVisibility(View.VISIBLE);
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

    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onClick(AppointmentTime appointmentTime) {
        if (Objects.equals(sharedPrefs.getData(Constants.KEY_ACCESS_TOKEN, String.class), "")) {
            displayDialog();
        } else {
            if (getIntent().getStringExtra(Constants.KEY_SOURCE_ACTIVITY) == null) {
                Intent intent = new Intent(this, SelectPatientProfileActivity.class);
                Doctor selectedDoctor = (Doctor) getIntent().getSerializableExtra(Constants.KEY_DOCTOR);
                Department selectedDepartment = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
                HealthFacility selectedHealthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
                ScheduleExclude selectedSchedule = (ScheduleExclude) getIntent().getSerializableExtra(Constants.KEY_DATE);
                intent.putExtra(Constants.KEY_DATE, selectedSchedule);
                intent.putExtra(Constants.KEY_DOCTOR, selectedDoctor);
                intent.putExtra(Constants.KEY_DEPARTMENT, selectedDepartment);
                intent.putExtra(Constants.KEY_HEALTH_FACILITY, selectedHealthFacility);
                intent.putExtra(Constants.KEY_TIME, appointmentTime.getTimeNumber());
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_TIME, appointmentTime);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        }
    }

    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectTimeActivity.this);
        LayoutDialogNotificationBinding dialogNotificationBinding = LayoutDialogNotificationBinding.inflate(getLayoutInflater());
        builder.setView(dialogNotificationBinding.getRoot());
        unLoginDialog = builder.create();
        if (unLoginDialog.getWindow() != null) {
            unLoginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogNotificationBinding.tvTitle.setText(getString(R.string.you_have_not_login_yet));
        dialogNotificationBinding.tvContent.setText(getString(R.string.this_function_need_to_login_to_use));
        dialogNotificationBinding.tvAction.setText(getString(R.string.login));
        dialogNotificationBinding.tvAction.setOnClickListener(v -> mStartForResult.launch(new Intent(this, LoginActivity.class)));
        dialogNotificationBinding.ivClose.setOnClickListener(v ->
        {
            unLoginDialog.dismiss();
            unLoginDialog = null;
        });
        unLoginDialog.show();
    }
}