package com.example.clinic_appointment.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.databinding.ActivityScheduleLookupBinding;
import com.example.clinic_appointment.models.AppointmentTime.AppointmentTime;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.CustomConverter;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScheduleLookupActivity extends AppCompatActivity {
    private HealthFacility selectedHealthFacility;
    private Department selectedDepartment;
    private AppointmentTime selectedAppointmentTime;
    private long dateFrom = -1;
    private long dateTo = -1;
    private ActivityScheduleLookupBinding binding;
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
                    } else if (Objects.requireNonNull(intent).getSerializableExtra(Constants.KEY_TIME) != null) {
                        selectedAppointmentTime = (AppointmentTime) intent.getSerializableExtra(Constants.KEY_TIME);
                        binding.etTime.setText(CustomConverter.getStringAppointmentTime(Objects.requireNonNull(selectedAppointmentTime).getTimeNumber()));
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
        binding = ActivityScheduleLookupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        eventHandling();
    }

    @SuppressLint("SetTextI18n")
    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.etHealthFacility.setOnClickListener(v -> launchSelectActivity(HealthFacilitySelectionActivity.class));
        binding.etDepartment.setOnClickListener(v -> launchSelectActivity(DepartmentSelectionActivity.class));
        binding.etTime.setOnClickListener(v -> launchSelectActivity(SelectTimeActivity.class));
        binding.etFromDate.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
            CalendarConstraints.DateValidator dateValidatorMin = DateValidatorPointForward.from(currentDate.getTimeInMillis());
            CalendarConstraints.DateValidator dateValidatorMax = DateValidatorPointBackward.before(currentDate.getTimeInMillis() + 30L * 86400000 * 2);
            ArrayList<CalendarConstraints.DateValidator> listValidators = new ArrayList<>();
            listValidators.add(dateValidatorMin);
            listValidators.add(dateValidatorMax);
            CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);
            constraintsBuilderRange.setValidator(validators);
            constraintsBuilderRange.setStart(currentDate.getTimeInMillis());
            constraintsBuilderRange.setEnd(currentDate.getTimeInMillis() + 30L * 86400000 * 2);
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText(getString(R.string.please_select_date))
                    .setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                    .setCalendarConstraints(constraintsBuilderRange.build())
                    .build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                dateFrom = selection.first;
                dateTo = selection.second;
                binding.etFromDate.setText(getDateFormatted(dateFrom) + " - " + getDateFormatted(dateTo));
            });
            materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.toString());
        });
        binding.tvSearch.setOnClickListener(v -> {
            if (dateFrom == -1 || dateTo == -1) {
                Toast.makeText(this, "Vui lòng chọn ngày khám", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, DoctorSelectionActivity.class);
                intent.putExtra(Constants.KEY_START_DATE, dateFrom);
                intent.putExtra(Constants.KEY_END_DATE, dateTo);
                intent.putExtra(Constants.KEY_HEALTH_FACILITY, selectedHealthFacility);
                intent.putExtra(Constants.KEY_DEPARTMENT, selectedDepartment);
                intent.putExtra(Constants.KEY_TIME, selectedAppointmentTime);
                intent.putExtra(Constants.KEY_SOURCE_ACTIVITY, "SearchSchedule");
                startActivity(intent);
            }
        });
    }

    private String getDateFormatted(Long selectedDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'tháng' MM, yyyy", Locale.getDefault());
        return sdf.format(new Date(selectedDate));
    }

    private void launchSelectActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(Constants.KEY_SOURCE_ACTIVITY, "SearchSchedule");
        if (activityClass == SelectTimeActivity.class) {
            intent.putExtra(Constants.KEY_DATE, (Serializable) generateEntireAppointmentArray());
        }
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

    private List<AppointmentTime> generateEntireAppointmentArray() {
        List<AppointmentTime> resultList = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            AppointmentTime appointmentTime = new AppointmentTime();
            appointmentTime.setTimeNumber(String.valueOf(i));
            appointmentTime.setFull(false);
            appointmentTime.setMaxAvailability(3);
            resultList.add(appointmentTime);
        }
        return resultList;
    }
}