package com.example.clinic_appointment.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.ImageUrlAdapter;
import com.example.clinic_appointment.databinding.ActivityDetailAppointmentBinding;
import com.example.clinic_appointment.databinding.LayoutConfirmationDialogBinding;
import com.example.clinic_appointment.models.Appointment.Appointment;
import com.example.clinic_appointment.models.Record.Record;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.CustomConverter;
import com.example.clinic_appointment.utilities.SharedPrefs;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailAppointmentActivity extends AppCompatActivity {
    private ActivityDetailAppointmentBinding binding;
    private String appointmentID;
    private String patientID;
    private AlertDialog alertDialog;
    private boolean isPreExamining = false;

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
            if (appointment.getStatus().equals("Đã khám")) {
                binding.tvConfirm.setVisibility(View.VISIBLE);
                binding.tvConfirm.setText("Đặt lịch tái khám");
                binding.tvConfirm.setBackgroundColor(getColor(R.color.colorPrimary));
                isPreExamining = true;
                binding.tvConfirm.setOnClickListener(v -> {
                    Intent intent = new Intent(this, DoctorSelectionActivity.class);
                    intent.putExtra(Constants.KEY_HEALTH_FACILITY, appointment.getSchedule().getDoctor().getHealthFacility());
                    intent.putExtra(Constants.KEY_DEPARTMENT, appointment.getSchedule().getDoctor().getDepartmentInformation());
                    startActivity(intent);
                });
                binding.pbLoading.setVisibility(View.VISIBLE);
                Call<Record> call = RetrofitClient.getAuthenticatedAppointmentService(this).getRecordByBookingId(appointment.getId());
                call.enqueue(new Callback<Record>() {
                    @Override
                    public void onResponse(@NonNull Call<Record> call, @NonNull Response<Record> response) {
                        binding.pbLoading.setVisibility(View.GONE);
                        if (response.body().getCounts() > 0) {
                            binding.tvResultLabel.setVisibility(View.VISIBLE);
                            binding.tvResult.setVisibility(View.VISIBLE);
                            binding.tvResult.setText(Html.fromHtml(response.body().getData().get(0).getDescription(), Html.FROM_HTML_MODE_COMPACT));
                            int medicineArrSize = response.body().getData().get(0).getMedicineArr().size();
                            if (medicineArrSize > 0) {
                                binding.tableMedicine.setVisibility(View.VISIBLE);
                                binding.tableRowContent1.setVisibility(View.VISIBLE);
                                binding.tvTitleMedicineName1.setText(response.body().getData().get(0).getMedicineArr().get(0).getMedicineID().getName());
                                String beforeAfterResult = response.body().getData().get(0).getMedicineArr().get(0).getInstraction();
                                String beforeAfterText;
                                if (beforeAfterResult.contains("before")) {
                                    beforeAfterText = "Trước ăn";
                                } else {
                                    beforeAfterText = "Sau ăn";
                                }
                                binding.tvTitleMedicineBeforeAfter1.setText(beforeAfterText);
                                StringBuilder time = new StringBuilder();
                                int dosageSize = response.body().getData().get(0).getMedicineArr().get(0).getDosage().size();
                                for (int i = 0; i < dosageSize; i++) {
                                    String timeResult = response.body().getData().get(0).getMedicineArr().get(0).getDosage().get(i);
                                    if (Objects.equals(timeResult, "m")) {
                                        time.append("Sáng ");
                                    } else if (Objects.equals(timeResult, "a")) {
                                        time.append("Trưa ");
                                    } else if (Objects.equals(timeResult, "e")) {
                                        time.append("Tối ");
                                    }
                                }
                                binding.tvTitleMedicineTime1.setText(time.toString());
                                Log.d("ResultTest", String.valueOf(response.body().getData().get(0).getMedicineArr().size()));
                                binding.tvTitleMedicineQuantity1.setText(String.valueOf(response.body().getData().get(0).getMedicineArr().get(1).getQuantity()));
                            }
                            if (medicineArrSize > 1) {
                                binding.tableMedicine.setVisibility(View.VISIBLE);
                                binding.tableRowContent2.setVisibility(View.VISIBLE);
                                binding.tvTitleMedicineName2.setText(response.body().getData().get(0).getMedicineArr().get(1).getMedicineID().getName());
                                String beforeAfterResult = response.body().getData().get(0).getMedicineArr().get(1).getInstraction();
                                String beforeAfterText;
                                if (beforeAfterResult.contains("before")) {
                                    beforeAfterText = "Trước ăn";
                                } else {
                                    beforeAfterText = "Sau ăn";
                                }
                                binding.tvTitleMedicineBeforeAfter2.setText(beforeAfterText);
                                StringBuilder time = new StringBuilder();
                                int dosageSize = response.body().getData().get(0).getMedicineArr().get(1).getDosage().size();
                                for (int i = 0; i < dosageSize; i++) {
                                    String timeResult = response.body().getData().get(0).getMedicineArr().get(1).getDosage().get(i);
                                    if (Objects.equals(timeResult, "m")) {
                                        time.append("Sáng ");
                                    } else if (Objects.equals(timeResult, "a")) {
                                        time.append("Trưa ");
                                    } else if (Objects.equals(timeResult, "e")) {
                                        time.append("Tối ");
                                    }
                                }
                                binding.tvTitleMedicineTime2.setText(time.toString());
                                binding.tvTitleMedicineQuantity2.setText(String.valueOf(response.body().getData().get(0).getMedicineArr().get(1).getQuantity()));
                            }
                            if (medicineArrSize > 2) {
                                binding.tableMedicine.setVisibility(View.VISIBLE);
                                binding.tableRowContent3.setVisibility(View.VISIBLE);
                                binding.tvTitleMedicineName3.setText(response.body().getData().get(0).getMedicineArr().get(2).getMedicineID().getName());
                                String beforeAfterResult = response.body().getData().get(0).getMedicineArr().get(2).getInstraction();
                                String beforeAfterText;
                                if (beforeAfterResult.contains("before")) {
                                    beforeAfterText = "Trước ăn";
                                } else {
                                    beforeAfterText = "Sau ăn";
                                }
                                binding.tvTitleMedicineBeforeAfter3.setText(beforeAfterText);
                                StringBuilder time = new StringBuilder();
                                int dosageSize = response.body().getData().get(0).getMedicineArr().get(2).getDosage().size();
                                for (int i = 0; i < dosageSize; i++) {
                                    String timeResult = response.body().getData().get(0).getMedicineArr().get(2).getDosage().get(i);
                                    if (Objects.equals(timeResult, "m")) {
                                        time.append("Sáng ");
                                    } else if (Objects.equals(timeResult, "a")) {
                                        time.append("Trưa ");
                                    } else if (Objects.equals(timeResult, "e")) {
                                        time.append("Tối ");
                                    }
                                }
                                binding.tvTitleMedicineTime3.setText(time.toString());
                                binding.tvTitleMedicineQuantity3.setText(String.valueOf(response.body().getData().get(0).getMedicineArr().get(2).getQuantity()));
                            }
                            if (medicineArrSize > 3) {
                                binding.tableMedicine.setVisibility(View.VISIBLE);
                                binding.tableRowContent4.setVisibility(View.VISIBLE);
                                binding.tvTitleMedicineName4.setText(response.body().getData().get(0).getMedicineArr().get(3).getMedicineID().getName());
                                String beforeAfterResult = response.body().getData().get(0).getMedicineArr().get(3).getInstraction();
                                String beforeAfterText;
                                if (beforeAfterResult.contains("before")) {
                                    beforeAfterText = "Trước ăn";
                                } else {
                                    beforeAfterText = "Sau ăn";
                                }
                                binding.tvTitleMedicineBeforeAfter4.setText(beforeAfterText);
                                StringBuilder time = new StringBuilder();
                                int dosageSize = response.body().getData().get(0).getMedicineArr().get(3).getDosage().size();
                                for (int i = 0; i < dosageSize; i++) {
                                    String timeResult = response.body().getData().get(0).getMedicineArr().get(3).getDosage().get(i);
                                    if (Objects.equals(timeResult, "m")) {
                                        time.append("Sáng ");
                                    } else if (Objects.equals(timeResult, "a")) {
                                        time.append("Trưa ");
                                    } else if (Objects.equals(timeResult, "e")) {
                                        time.append("Tối ");
                                    }
                                }
                                binding.tvTitleMedicineTime4.setText(time.toString());
                                binding.tvTitleMedicineQuantity4.setText(String.valueOf(response.body().getData().get(0).getMedicineArr().get(2).getQuantity()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Record> call, @NonNull Throwable t) {
                        binding.pbLoading.setVisibility(View.GONE);
                    }
                });
            }
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
            patientID = appointment.getPatient().getId();
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
        if (!isPreExamining) {
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
                    Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(this).cancelAppointment(appointmentID, patientID);
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
}