package com.example.clinic_appointment.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.activities.DetailAppointmentActivity;
import com.example.clinic_appointment.activities.LoginActivity;
import com.example.clinic_appointment.adapters.AppointmentManagementAdapter;
import com.example.clinic_appointment.adapters.DoctorAppointmentAdapter;
import com.example.clinic_appointment.databinding.FragmentMyScheduleBinding;
import com.example.clinic_appointment.databinding.LayoutDialogNotificationBinding;
import com.example.clinic_appointment.listeners.AppointmentListener;
import com.example.clinic_appointment.models.Appointment.Appointment;
import com.example.clinic_appointment.models.Appointment.AppointmentResponse;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.SharedPrefs;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyScheduleFragment extends Fragment implements AppointmentListener {
    private long dateFrom = -1;
    private long dateTo = -1;
    private FragmentMyScheduleBinding binding;
    private TextView currentStatusOption = null;
    private TextView currentTimeOption = null;
    private DoctorAppointmentAdapter doctorAppointmentAdapter;
    private AppointmentManagementAdapter appointmentManagementAdapter;
    private List<Appointment> appointments;
    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Integer resultCode = result.getResultCode();
                if (result.getData() != null && resultCode == Activity.RESULT_OK && result.getData().getStringExtra("Cancel") != null) {
                    Toast.makeText(requireContext(), getString(R.string.cancel_success_fully), Toast.LENGTH_SHORT).show();
                    initiate();
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyScheduleBinding.inflate(getLayoutInflater());
        initiate();
        eventHandling();
        return binding.getRoot();
    }

    private void initiate() {
        optionPerformStatusClick(binding.tvWaitingConfirmation);
        optionPerformTimeClick(binding.tvDefault);
    }

    private void optionPerformStatusClick(TextView newSelection) {
        if (currentStatusOption == null) {
            setSelectedBackground(binding.tvWaitingConfirmation);
        } else {
            setUnselectedBackground(currentStatusOption);
            setSelectedBackground(newSelection);
        }
        currentStatusOption = newSelection;
        getAppointments();
    }

    private void optionPerformTimeClick(TextView newSelection) {
        if (currentTimeOption == null) {
            setSelectedBackground(binding.tvDefault);
        } else {
            setUnselectedBackground(currentTimeOption);
            setSelectedBackground(newSelection);
        }
        currentTimeOption = newSelection;
        getAppointments();
    }

    private void setUnselectedBackground(TextView textView) {
        textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorMyScheduleOptionText));
        textView.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.background_my_schedule_option_unselected));
    }

    private void setSelectedBackground(TextView textView) {
        textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorWhite));
        textView.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.background_my_schedule_option_selected));
    }

    private void eventHandling() {
        final SwipeRefreshLayout pullToRefresh = binding.swipeLayout;
        pullToRefresh.setOnRefreshListener(() -> {
            getAppointments();
            pullToRefresh.setRefreshing(false);
        });
        binding.tvWaitingConfirmation.setOnClickListener(v -> optionPerformStatusClick(binding.tvWaitingConfirmation));
        binding.tvConfirmed.setOnClickListener(v -> optionPerformStatusClick(binding.tvConfirmed));
        binding.tvCancelled.setOnClickListener(v -> optionPerformStatusClick(binding.tvCancelled));
        binding.tvEntire.setOnClickListener(v -> optionPerformStatusClick(binding.tvEntire));
        binding.tvChecked.setOnClickListener(v -> optionPerformStatusClick(binding.tvChecked));
        binding.tvDefault.setOnClickListener(v -> optionPerformTimeClick(binding.tvDefault));
        binding.tvToday.setOnClickListener(v -> optionPerformTimeClick(binding.tvToday));
        binding.tvThisWeek.setOnClickListener(v -> optionPerformTimeClick(binding.tvThisWeek));
        binding.tvSelectDate.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText(getString(R.string.please_select_date))
                    .setCalendarConstraints(new CalendarConstraints.Builder()
                            .setStart(currentDate.getTimeInMillis() - 12L * 30L * 86400000)
                            .setEnd(getTwoMonthLater(currentDate))
                            .build())
                    .setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                    .build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                dateFrom = selection.first;
                dateTo = selection.second;
                optionPerformTimeClick(binding.tvSelectDate);
            });
            materialDatePicker.show(requireActivity().getSupportFragmentManager(), materialDatePicker.toString());
        });
        binding.ivBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private long getTwoMonthLater(Calendar calendar) {
        calendar.add(Calendar.MONTH, 2);
        return calendar.getTimeInMillis();
    }

    private void getAppointments() {
        SharedPrefs sharedPrefs = SharedPrefs.getInstance();
        if (sharedPrefs.getData(Constants.KEY_ACCESS_TOKEN, String.class).equals("")) {
            displayDialog();
        } else if (sharedPrefs.getData(Constants.KEY_USER_ROLE, Integer.class).equals(4)) {
            binding.rvAppointments.setVisibility(View.GONE);
            binding.pbLoading.setVisibility(View.VISIBLE);
            Call<AppointmentResponse> call = RetrofitClient.getAuthenticatedAppointmentService(requireContext()).getEntireAppointment();
            call.enqueue(new Callback<AppointmentResponse>() {
                @Override
                public void onResponse(@NonNull Call<AppointmentResponse> call, @NonNull Response<AppointmentResponse> response) {
                    if (response.body() != null && response.isSuccessful()) {
                        appointments = response.body().getBooking();
                        if (currentStatusOption == binding.tvWaitingConfirmation) {
                            getAppointmentByStatus(appointments, "Đang xử lý");
                        } else if (currentStatusOption == binding.tvConfirmed) {
                            getAppointmentByStatus(appointments, "Đã xác nhận");
                        } else if (currentStatusOption == binding.tvCancelled) {
                            getAppointmentByStatus(appointments, "Đã huỷ");
                        } else if (currentStatusOption == binding.tvChecked) {
                            getAppointmentByStatus(appointments, "Đã khám");
                        }
                        if (currentTimeOption == binding.tvToday) {
                            LocalDate today = LocalDate.now();
                            appointments.removeIf(appointment -> {
                                LocalDate appointmentDate = appointment.getSchedule().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return !appointmentDate.isEqual(today);
                            });
                        } else if (currentTimeOption == binding.tvThisWeek) {
                            LocalDate today = LocalDate.now();
                            DayOfWeek startOfWeek = DayOfWeek.MONDAY;

                            LocalDate startOfWeekDate = today.with(TemporalAdjusters.previousOrSame(startOfWeek));
                            LocalDate endOfWeekDate = startOfWeekDate.plusDays(6);

                            appointments.removeIf(appointment -> {
                                LocalDate appointmentDate = appointment.getSchedule().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return appointmentDate.isBefore(startOfWeekDate) || appointmentDate.isAfter(endOfWeekDate);
                            });
                        } else if (currentTimeOption == binding.tvSelectDate) {
                            appointments.removeIf(appointment -> {
                                Date appointmentDate = appointment.getSchedule().getDate();
                                return appointmentDate.before(new Date(dateFrom)) || appointmentDate.after(new Date(dateTo));
                            });
                        }
                        appointmentManagementAdapter = new AppointmentManagementAdapter(MyScheduleFragment.this, appointments);
                        binding.rvAppointments.setAdapter(appointmentManagementAdapter);
                        binding.pbLoading.setVisibility(View.GONE);
                        binding.rvAppointments.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppointmentResponse> call, @NonNull Throwable t) {
                    binding.pbLoading.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), "Error", BaseTransientBottomBar.LENGTH_SHORT).show();
                    Log.d("FailCheck", Objects.requireNonNull(t.getMessage()));
                }
            });
        } else if (sharedPrefs.getData(Constants.KEY_USER_ROLE, Integer.class).equals(3)) {
            binding.rvAppointments.setVisibility(View.GONE);
            binding.pbLoading.setVisibility(View.VISIBLE);
            Call<AppointmentResponse> call = RetrofitClient.getAuthenticatedAppointmentService(requireContext()).getEntireAppointment();
            call.enqueue(new Callback<AppointmentResponse>() {
                @Override
                public void onResponse(@NonNull Call<AppointmentResponse> call, @NonNull Response<AppointmentResponse> response) {
                    if (response.body() != null && response.isSuccessful()) {
                        appointments = response.body().getBooking();
                        if (currentStatusOption == binding.tvWaitingConfirmation) {
                            getAppointmentByStatus(appointments, "Đang xử lý");
                        } else if (currentStatusOption == binding.tvConfirmed) {
                            getAppointmentByStatus(appointments, "Đã xác nhận");
                        } else if (currentStatusOption == binding.tvCancelled) {
                            getAppointmentByStatus(appointments, "Đã huỷ");
                        } else if (currentStatusOption == binding.tvChecked) {
                            getAppointmentByStatus(appointments, "Đã khám");
                        }
                        if (currentTimeOption == binding.tvToday) {
                            LocalDate today = LocalDate.now();
                            appointments.removeIf(appointment -> {
                                LocalDate appointmentDate = appointment.getSchedule().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return !appointmentDate.isEqual(today);
                            });
                        } else if (currentTimeOption == binding.tvThisWeek) {
                            LocalDate today = LocalDate.now();
                            DayOfWeek startOfWeek = DayOfWeek.MONDAY;

                            LocalDate startOfWeekDate = today.with(TemporalAdjusters.previousOrSame(startOfWeek));
                            LocalDate endOfWeekDate = startOfWeekDate.plusDays(6);

                            appointments.removeIf(appointment -> {
                                LocalDate appointmentDate = appointment.getSchedule().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                return appointmentDate.isBefore(startOfWeekDate) || appointmentDate.isAfter(endOfWeekDate);
                            });
                        } else if (currentTimeOption == binding.tvSelectDate) {
                            appointments.removeIf(appointment -> {
                                Date appointmentDate = appointment.getSchedule().getDate();
                                return appointmentDate.before(new Date(dateFrom)) || appointmentDate.after(new Date(dateTo));
                            });
                        }
                        doctorAppointmentAdapter = new DoctorAppointmentAdapter(MyScheduleFragment.this, appointments);
                        binding.rvAppointments.setAdapter(doctorAppointmentAdapter);
                        binding.pbLoading.setVisibility(View.GONE);
                        binding.rvAppointments.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppointmentResponse> call, @NonNull Throwable t) {
                    binding.pbLoading.setVisibility(View.GONE);
                }
            });
        }
    }

    private void getAppointmentByStatus(List<Appointment> appointments, String status) {
        appointments.removeIf(appointment -> !appointment.getStatus().equals(status));
    }

    @Override
    public void onClick(Appointment appointment) {
        Intent intent = new Intent(requireActivity(), DetailAppointmentActivity.class);
        if (appointment.getStatus().equals(Constants.STATUS_PROCESSING)) {
            intent.putExtra(Constants.KEY_STATUS, Constants.STATUS_PROCESSING);
        }
        intent.putExtra(Constants.KEY_BOOKING, appointment);
        startActivity(intent);
    }

    @Override
    public void onAcceptClick(Appointment appointment, int position) {
        Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(getContext()).updateBooking(appointment.getId(), "Đã duyệt", null, null);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                appointments.remove(appointment);
                doctorAppointmentAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onDenyClick(Appointment appointment, int position) {
        Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(getContext()).updateBooking(appointment.getId(), "Đã huỷ", null, null);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                appointments.remove(appointment);
                doctorAppointmentAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

            }
        });
    }

    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutDialogNotificationBinding dialogNotificationBinding = LayoutDialogNotificationBinding.inflate(getLayoutInflater());
        builder.setView(dialogNotificationBinding.getRoot());
        AlertDialog unLoginDialog = builder.create();
        if (unLoginDialog.getWindow() != null) {
            unLoginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogNotificationBinding.ivClose.setVisibility(View.INVISIBLE);
        dialogNotificationBinding.tvTitle.setText(getString(R.string.you_have_not_login_yet));
        dialogNotificationBinding.tvContent.setText(getString(R.string.this_function_need_to_login_to_use));
        dialogNotificationBinding.tvAction.setText(getString(R.string.login));
        dialogNotificationBinding.tvAction.setOnClickListener(v -> mStartForResult.launch(new Intent(requireActivity(), LoginActivity.class)));
        unLoginDialog.show();
    }
}