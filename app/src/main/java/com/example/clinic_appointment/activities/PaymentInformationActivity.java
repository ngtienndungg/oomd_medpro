package com.example.clinic_appointment.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.databinding.ActivityPaymentInformationBinding;
import com.example.clinic_appointment.databinding.LayoutConfirmationDialogBinding;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.models.Schedule.DetailSchedule;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.CustomConverter;
import com.example.clinic_appointment.utilities.SharedPrefs;
import com.example.clinic_appointment.zalopay.Api.CreateOrder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentInformationActivity extends AppCompatActivity {
    private ActivityPaymentInformationBinding binding;
    private DetailSchedule selectedSchedule;
    private String selectedTime;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(553, Environment.SANDBOX);
        binding = ActivityPaymentInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    @SuppressLint("SetTextI18n")
    private void initiate() {
        Doctor selectedDoctor = (Doctor) getIntent().getSerializableExtra(Constants.KEY_DOCTOR);
        Department selectedDepartment = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
        HealthFacility selectedHealthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
        selectedSchedule = (DetailSchedule) getIntent().getSerializableExtra(Constants.KEY_DATE);
        selectedTime = getIntent().getStringExtra(Constants.KEY_TIME);
        binding.tvPaymentMethod.setText(getIntent().getStringExtra(Constants.KEY_PAYMENT_METHOD));
        binding.tvHealthFacility.setText(Objects.requireNonNull(selectedHealthFacility).getName());
        binding.tvDepartment.setText(Objects.requireNonNull(selectedDepartment).getName());
        binding.tvDoctor.setText(Objects.requireNonNull(selectedDoctor).getDoctorInformation().getFullName());
        binding.tvDate.setText(CustomConverter.getFormattedDate(Objects.requireNonNull(selectedSchedule).getDate()));
        binding.tvPrice.setText(Objects.requireNonNull(selectedSchedule).getPrice() + " VND");
        binding.tvTime.setText(CustomConverter.getStringAppointmentTime(Objects.requireNonNull(selectedTime)));
        binding.tvPatientName.setText(SharedPrefs.getInstance().getData(Constants.KEY_CURRENT_NAME, String.class));
        binding.tvPatientEmail.setText(SharedPrefs.getInstance().getData(Constants.KEY_CURRENT_EMAIL, String.class));
        binding.tvPatientPhoneNumber.setText(SharedPrefs.getInstance().getData(Constants.KEY_CURRENT_PHONE_NUMBER, String.class));
        binding.tvUtilityPrice.setText(selectedSchedule.getPrice() / 10 + " VND");
        binding.tvTotalPrice.setText(selectedSchedule.getPrice() + selectedSchedule.getPrice() / 10 + " VND");
    }

    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvConfirmPayment.setOnClickListener(v -> {
            displayConfirmationDialog();
        });
    }

    private void displayConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutConfirmationDialogBinding confirmationDialogBinding = LayoutConfirmationDialogBinding.inflate(getLayoutInflater());
        builder.setView(confirmationDialogBinding.getRoot());
        alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        confirmationDialogBinding.tvTitle.setText(getString(R.string.are_you_sure_to_book));
        confirmationDialogBinding.tvContent.setText(R.string.ensure_exactly_information);
        confirmationDialogBinding.tvPositiveAction.setOnClickListener(v -> {
            if (Objects.equals(getIntent().getStringExtra(Constants.KEY_PAYMENT_METHOD), getString(R.string.payment_zalo_pay))) {
                handleZalopay();
                alertDialog.dismiss();
            } else {
                bookAppointment(selectedSchedule.getScheduleId(), selectedTime, ConfirmationActivity.base64Images);
                alertDialog.dismiss();
            }
        });
        confirmationDialogBinding.ivClose.setOnClickListener(v -> alertDialog.dismiss());
        confirmationDialogBinding.tvNegativeAction.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    private void handleZalopay() {
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder("100000");
            String code = data.getString("returncode");
            if (code.equals("1")) {
                String token = data.getString("zptranstoken");
                ZaloPaySDK.getInstance().payOrder(PaymentInformationActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        bookAppointment(selectedSchedule.getScheduleId(), selectedTime, ConfirmationActivity.base64Images);
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Log.d("ClickTest", "Can");
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Log.d("ClickTest", "Pay Error" + zaloPayError.name());
                    }
                });
            }

        } catch (Exception e) {
            Log.d("ClickTest", "Return code: fail");
            e.printStackTrace();
        }
    }

    public void bookAppointment(String scheduleID, String appointmentTime, List<String> images) {
        try {
            binding.tvConfirmPayment.setVisibility(View.GONE);
            binding.pbLoading.setVisibility(View.VISIBLE);
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("scheduleID", scheduleID);
            jsonPayload.put("time", appointmentTime);

            JSONArray imagesArray = new JSONArray();
            if (images != null) {
                for (String imageData : images) {
                    String formattedImageData = "data:image/jpeg;base64," + imageData;
                    imagesArray.put(formattedImageData);
                }
                jsonPayload.put("images", imagesArray);
            }

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonPayload.toString());
            Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(this).bookAppointmentByPatient(requestBody);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(PaymentInformationActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Constants.KEY_STATUS_CODE, response.code());
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        startActivity(intent);
                    } else if (response.code() == 500) {
                        Snackbar.make(binding.getRoot(), getString(R.string.this_schedule_was_book), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                    binding.pbLoading.setVisibility(View.GONE);
                    binding.tvConfirmPayment.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Snackbar.make(binding.getRoot(), getString(R.string.something_wrong_happened), BaseTransientBottomBar.LENGTH_SHORT).show();
                    binding.pbLoading.setVisibility(View.GONE);
                    binding.tvConfirmPayment.setVisibility(View.VISIBLE);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}