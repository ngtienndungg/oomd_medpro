package com.example.clinic_appointment.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.ImageBase64Adapter;
import com.example.clinic_appointment.databinding.ActivityConfirmationBinding;
import com.example.clinic_appointment.models.Department.Department;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.HealthFacility.HealthFacility;
import com.example.clinic_appointment.models.PatientProfile.PatientProfile;
import com.example.clinic_appointment.models.Schedule.ScheduleExclude;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.CustomConverter;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class ConfirmationActivity extends AppCompatActivity {
    public static ArrayList<String> base64Images;
    private ActivityConfirmationBinding binding;
    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                base64Images = new ArrayList<>();
                ClipData clipData = data.getClipData();
                if (clipData != null && clipData.getItemCount() <= 3) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        String base64Image = convertImageToBase64(imageUri);
                        base64Images.add(base64Image);
                    }
                    ImageBase64Adapter adapter = new ImageBase64Adapter(base64Images);
                    binding.rvImages.setAdapter(adapter);
                    binding.rvImages.setVisibility(View.VISIBLE);
                    binding.tvAddImage.setVisibility(View.GONE);
                } else {
                    Snackbar.make(binding.getRoot(), getString(R.string.limit_3_images), BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        }
    });
    private Doctor selectedDoctor;
    private Department selectedDepartment;
    private HealthFacility selectedHealthFacility;
    private ScheduleExclude selectedSchedule;
    private String timeNumber;
    private PatientProfile patientProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initiate();
        eventHandling();
    }

    @SuppressLint("SetTextI18n")
    private void initiate() {
        patientProfile = (PatientProfile) getIntent().getSerializableExtra(Constants.KEY_PATIENT_ID);
        selectedDoctor = (Doctor) getIntent().getSerializableExtra(Constants.KEY_DOCTOR);
        selectedDepartment = (Department) getIntent().getSerializableExtra(Constants.KEY_DEPARTMENT);
        selectedHealthFacility = (HealthFacility) getIntent().getSerializableExtra(Constants.KEY_HEALTH_FACILITY);
        selectedSchedule = (ScheduleExclude) getIntent().getSerializableExtra(Constants.KEY_DATE);
        timeNumber = getIntent().getStringExtra(Constants.KEY_TIME);
        binding.tvHealthFacility.setText(Objects.requireNonNull(selectedHealthFacility).getName());
        binding.tvDepartment.setText(Objects.requireNonNull(selectedDepartment).getName());
        binding.tvDoctor.setText(Objects.requireNonNull(selectedDoctor).getDoctorInformation().getFullName());
        binding.tvDate.setText(CustomConverter.getFormattedDate(selectedSchedule.getDate()));
        binding.tvPrice.setText(Objects.requireNonNull(selectedSchedule).getPrice() + " VND");
        binding.tvTime.setText(CustomConverter.getStringAppointmentTime(timeNumber));
    }

    private void eventHandling() {
        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.tvConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(this, MethodPaymentSelectionActivity.class);
            intent.putExtra(Constants.KEY_DATE, selectedSchedule);
            intent.putExtra(Constants.KEY_DOCTOR, selectedDoctor);
            intent.putExtra(Constants.KEY_DEPARTMENT, selectedDepartment);
            intent.putExtra(Constants.KEY_HEALTH_FACILITY, selectedHealthFacility);
            intent.putExtra(Constants.KEY_TIME, timeNumber);
            intent.putExtra(Constants.KEY_PATIENT_ID, patientProfile);
            startActivity(intent);
        });
        binding.rlAddImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select images"));
        });
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            return new ConvertToBase64Task().execute(imageUri).get();
        } catch (Exception e) {
            Log.e("ImageConversion", "Error converting image to Base64", e);
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ConvertToBase64Task extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            Uri imageUri = uris[0];
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    Bitmap resizedBitmap = resizeImage(inputStream);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    if (resizedBitmap != null) {
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                    }
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    return Base64.encodeToString(bytes, Base64.DEFAULT);
                }
            } catch (IOException e) {
                Log.e("ImageConversion", "Error converting image to Base64", e);
            }
            return null;
        }

        private Bitmap resizeImage(InputStream inputStream) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
                return bitmap;
            } catch (IOException e) {
                Log.e("ImageConversion", "Error resizing image", e);
                return null;
            }
        }
    }

}