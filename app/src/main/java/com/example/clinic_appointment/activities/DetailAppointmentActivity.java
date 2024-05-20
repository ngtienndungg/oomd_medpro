package com.example.clinic_appointment.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.ImageUrlAdapter;
import com.example.clinic_appointment.databinding.ActivityDetailAppointmentBinding;
import com.example.clinic_appointment.databinding.LayoutConfirmationDialogBinding;
import com.example.clinic_appointment.models.Appointment.Appointment;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.CustomConverter;
import com.example.clinic_appointment.utilities.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailAppointmentActivity extends AppCompatActivity {
    private ActivityDetailAppointmentBinding binding;
    private String appointmentID;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    @SuppressLint("SetTextI18n")
    private void initiate() {
        Appointment appointment = (Appointment) getIntent().getSerializableExtra(Constants.KEY_BOOKING);
        if (getIntent().getStringExtra(Constants.KEY_STATUS) != null) {
            binding.tvConfirm.setVisibility(View.VISIBLE);
            if (SharedPrefs.getInstance().getData(Constants.KEY_USER_ROLE, Integer.class).equals(3)) {
                binding.tvAccept.setVisibility(View.VISIBLE);
            }
        }
        if (appointment != null) {
            binding.tvHealthFacility.setText(appointment.getSchedule().getDoctor().getHealthFacility().getName());
            binding.tvAddress.setText(appointment.getSchedule().getDoctor().getHealthFacility().getAddressString());
            binding.tvDepartment.setText(appointment.getSchedule().getDoctor().getDepartmentInformation().getName());
            binding.tvDoctor.setText(appointment.getSchedule().getDoctor().getDoctorInformation().getFullName());
            binding.tvDate.setText(CustomConverter.getFormattedDate(appointment.getSchedule().getDate()));
            binding.tvTime.setText(CustomConverter.getStringAppointmentTime(appointment.getAppointmentTime()));
            binding.tvStatus.setText(appointment.getStatus());
            binding.tvPatientName.setText(appointment.getPatient().getFullName());
            binding.tvPhoneNumber.setText(appointment.getPatient().getPhoneNumber());
            binding.tvGender.setText(appointment.getPatient().getGenderVietnamese());
            binding.tvPrice.setText(appointment.getSchedule().getPrice() + " VND");
            appointmentID = appointment.getId();
            if (appointment.getImages() != null && appointment.getImages().size() > 0) {
                ImageUrlAdapter adapter = new ImageUrlAdapter(appointment.getImages());
                binding.rvImages.setAdapter(adapter);
                binding.tvImage.setVisibility(View.VISIBLE);
                binding.rvImages.setVisibility(View.VISIBLE);
            }
            if (appointment.getDescription() != null && !appointment.getDescription().isEmpty()) {
                binding.tvResultLabel.setVisibility(View.VISIBLE);
                binding.tvResult.setVisibility(View.VISIBLE);
                binding.tvResult.setText(Html.fromHtml(appointment.getDescription(), Html.FROM_HTML_MODE_COMPACT));
            }
        }
    }

    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvConfirm.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutConfirmationDialogBinding confirmationDialogBinding = LayoutConfirmationDialogBinding.inflate(getLayoutInflater());
            builder.setView(confirmationDialogBinding.getRoot());
            alertDialog = builder.create();
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            confirmationDialogBinding.tvTitle.setText(getString(R.string.are_you_sure_you_want_to_cancel));
            confirmationDialogBinding.tvContent.setText(getString(R.string.please_make_sure_that_you_want_to_cancel));
            confirmationDialogBinding.tvPositiveAction.setOnClickListener(v1 -> {
                Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(this).cancelAppointment(appointmentID);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        setResult(RESULT_OK);
                        Intent intent = new Intent();
                        intent.putExtra("Cancel", "Cancel");
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                });
            });
            confirmationDialogBinding.ivClose.setOnClickListener(v1 -> alertDialog.dismiss());
            confirmationDialogBinding.tvNegativeAction.setOnClickListener(v1 -> alertDialog.dismiss());
            alertDialog.show();
        });
    }
}