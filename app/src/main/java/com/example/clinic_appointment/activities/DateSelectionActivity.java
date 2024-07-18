package com.example.clinic_appointment.activities;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.databinding.ActivitySelectDateBinding;
import com.example.clinic_appointment.models.AppointmentTime.AppointmentTime;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.models.Schedule.ScheduleExclude;
import com.example.clinic_appointment.models.Schedule.ScheduleExcludeResponse;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.DayViewContainer;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.MonthDayBinder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateSelectionActivity extends AppCompatActivity {
    private ActivitySelectDateBinding binding;
    private List<ScheduleExclude> availableSchedules;
    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectDateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    void initiate() {
        doctor = (Doctor) getIntent().getSerializableExtra(Constants.KEY_DOCTOR);
        if (doctor == null) {
            doctor = ((Doctor) Objects.requireNonNull(getIntent().getSerializableExtra(Constants.KEY_DETAIL_DOCTOR)));
        }
        long currentTimeMillis = System.currentTimeMillis();
        long endTimeMillis = currentTimeMillis + (30L * 86400000);
        binding.pbLoading.setVisibility(View.VISIBLE);
        Call<ScheduleExcludeResponse> call = RetrofitClient.getAuthenticatedAppointmentService(this)
                .getSchedules(currentTimeMillis - 86400000, endTimeMillis, null, null, null, Objects.requireNonNull(doctor).getDoctorInformation().getId(), "-ratings");
        availableSchedules = new ArrayList<>();
        call.enqueue(new Callback<ScheduleExcludeResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleExcludeResponse> call, @NonNull Response<ScheduleExcludeResponse> response) {
                binding.pbLoading.setVisibility(View.GONE);
                if (response.body() != null && response.code() == 200) {
                    availableSchedules = response.body().getSchedules();
                    setupCalendar();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScheduleExcludeResponse> call, @NonNull Throwable t) {
                Log.d("FailCheck", Objects.requireNonNull(t.getMessage()));
                binding.pbLoading.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), getString(R.string.something_wrong_happened), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    private LocalDate getLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void setupCalendar() {
        binding.cvCalendar.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer dayViewContainer, CalendarDay calendarDay) {
                dayViewContainer.textView.setTextColor(getColor(R.color.colorCalendarDayText));
                dayViewContainer.textView.setBackgroundColor(getColor(R.color.colorCalendarDayBackground));
                dayViewContainer.textView.setEnabled(false);
                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    if (calendarDay.getDate().equals(LocalDate.now())) {
                        dayViewContainer.textView.setBackgroundResource(R.color.colorTodayDate);
                    }
                    for (ScheduleExclude schedule : availableSchedules) {
                        if (calendarDay.getDate().equals(getLocalDate(schedule.getDate()))) {
                            boolean isFull = true;
                            for (AppointmentTime appointmentTime : schedule.getAppointmentTimes()) {
                                if (!appointmentTime.isFull()) {
                                    isFull = false;
                                    break;
                                }
                            }
                            if (isFull) {
                                dayViewContainer.textView.setBackgroundResource(R.color.colorFullSlotDate);
                            } else {
                                dayViewContainer.textView.setEnabled(true);
                                dayViewContainer.textView.setBackgroundResource(R.color.colorAvailableDate);
                                dayViewContainer.textView.setOnClickListener(v -> {
                                    Intent intent = new Intent(getApplicationContext(), SelectTimeActivity.class);
                                    Department selectedDepartment = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
                                    HealthFacility selectedHealthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
                                    intent.putExtra(Constants.KEY_DATE, schedule);
                                    intent.putExtra(Constants.KEY_DOCTOR, doctor);
                                    intent.putExtra(Constants.KEY_DEPARTMENT, selectedDepartment);
                                    intent.putExtra(Constants.KEY_HEALTH_FACILITY, selectedHealthFacility);
                                    startActivity(intent);
                                });
                            }
                        }
                    }
                    dayViewContainer.getView().setVisibility(View.VISIBLE);
                    dayViewContainer.textView.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));
                } else {
                    dayViewContainer.getView().setVisibility(View.INVISIBLE);
                }
            }
        });

        YearMonth currentMonth = YearMonth.now();
        YearMonth endMonth = currentMonth.plusMonths(12);
        List<DayOfWeek> daysOfWeek = daysOfWeek();

        ViewGroup titlesContainer = findViewById(R.id.titlesContainer);
        for (int index = 0; index < titlesContainer.getChildCount(); index++) {
            View child = titlesContainer.getChildAt(index);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(String.valueOf(daysOfWeek.get(index)));
                String title = dayOfWeek.getDisplayName(TextStyle.NARROW_STANDALONE, Locale.getDefault());
                textView.setText(title);
            }
        }
        binding.cvCalendar.setup(currentMonth, endMonth, daysOfWeek.get(0));
        binding.cvCalendar.scrollToMonth(currentMonth);
    }

    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.cvCalendar.setMonthScrollListener(calendarMonth -> {
            String currentMonthYear = calendarMonth.getYearMonth().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " - " + calendarMonth.getYearMonth().getYear();
            binding.tvMonth.setText(currentMonthYear);
            return null;
        });
        binding.ivNextMonth.setOnClickListener(v -> binding.cvCalendar.smoothScrollToMonth(Objects.requireNonNull(binding.cvCalendar.findFirstVisibleMonth()).getYearMonth().plusMonths(1)));
        binding.ivPreviousMonth.setOnClickListener(v -> binding.cvCalendar.smoothScrollToMonth(Objects.requireNonNull(binding.cvCalendar.findFirstVisibleMonth()).getYearMonth().minusMonths(1)));
    }
}