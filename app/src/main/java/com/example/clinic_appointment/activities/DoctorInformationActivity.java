package com.example.clinic_appointment.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.clinic_appointment.R;
import com.example.clinic_appointment.adapters.RatingAdapter;
import com.example.clinic_appointment.databinding.ActivityDoctorInformationBinding;
import com.example.clinic_appointment.databinding.LayoutDialogRateBinding;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.Doctor.DoctorSingleResponse;
import com.example.clinic_appointment.models.Rating.Rating;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.SharedPrefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorInformationActivity extends AppCompatActivity {
    ImageView[] starImageRootViews;
    private ActivityDoctorInformationBinding binding;
    private Doctor doctor;
    private AlertDialog alertDialog;
    private int selectedStar = 5;
    private int starNumbers = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        starImageRootViews = new ImageView[]{
                binding.ivStar5,
                binding.ivStar4,
                binding.ivStar3,
                binding.ivStar2,
                binding.ivStar1
        };
        initiate();
        eventHandling();
    }

    private void initiate() {
        if (SharedPrefs.getInstance().getData(Constants.KEY_ACCESS_TOKEN, String.class).equals("")) {
            binding.tvYourRating.setVisibility(View.GONE);
            binding.tvYourComment.setVisibility(View.GONE);
            binding.llYourStar.setVisibility(View.GONE);
            binding.tvChange.setVisibility(View.GONE);
        }
        doctor = (Doctor) getIntent().getSerializableExtra(Constants.KEY_DOCTOR);
        if (doctor != null) {
            binding.tvDepartment.setText(doctor.getDepartmentInformation().getName());
            binding.tvPosition.setText(doctor.getAcademicLevel());
            binding.tvClinic.setText(doctor.getHealthFacility().getName());
            binding.tvName.setText(doctor.getDoctorInformation().getFullName());
            binding.tvDescription.setText(Html.fromHtml(doctor.getDescription(), Html.FROM_HTML_MODE_COMPACT));
            binding.tvGender.setText(doctor.getDoctorInformation().getGenderVietnamese());
            Glide.with(this).load(doctor.getDoctorInformation().getAvatar()).centerCrop().into(binding.ivImage);
            Call<DoctorSingleResponse> call = RetrofitClient.getPublicAppointmentService().getDoctorById(doctor.getDoctorInformation().getId());
            call.enqueue(new Callback<DoctorSingleResponse>() {
                @Override
                public void onResponse(@NonNull Call<DoctorSingleResponse> call, @NonNull Response<DoctorSingleResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getDoctors().getRatings().size() == 0) {
                            binding.tvNoRating.setVisibility(View.VISIBLE);
                        } else {
                            List<Rating> ratings = new ArrayList<>(response.body().getDoctors().getRatings());
                            for (Rating rating : ratings) {
                                if (Objects.equals(rating.getPostedBy().getId(), SharedPrefs.getInstance().getData(Constants.KEY_CURRENT_UID, String.class))) {
                                    binding.tvYourComment.setVisibility(View.VISIBLE);
                                    binding.llYourStar.setVisibility(View.VISIBLE);
                                    binding.tvChange.setText(getString(R.string.change_rating));
                                    starNumbers = rating.getStar();

                                    for (int i = 0; i < starImageRootViews.length; i++) {
                                        starImageRootViews[i].setVisibility(starNumbers >= (5 - i) ? View.VISIBLE : View.GONE);
                                    }
                                    binding.tvYourComment.setText(rating.getComment());
                                    ratings.remove(rating);
                                    break;
                                }
                            }
                            RatingAdapter ratingAdapter = new RatingAdapter(ratings);
                            binding.tvRating.append("(" + response.body().getDoctors().getAverageRating() + ")");
                            binding.rvRatings.setAdapter(ratingAdapter);
                            binding.rvRatings.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DoctorSingleResponse> call, @NonNull Throwable t) {

                }
            });
        }
    }

    private void eventHandling() {
        binding.tvChange.setOnClickListener(v -> {
            displayConfirmationDialog();
        });
        binding.tvClinicInformation.setOnClickListener(v -> {
            Intent intent = new Intent(this, HealthFacilityInformationActivity.class);
            intent.putExtra(Constants.KEY_HEALTH_FACILITY, doctor.getHealthFacility());
            startActivity(intent);
        });
        binding.tvSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, DateSelectionActivity.class);
            intent.putExtra(Constants.KEY_DOCTOR, doctor);
            intent.putExtra(Constants.KEY_HEALTH_FACILITY, doctor.getHealthFacility());
            intent.putExtra(Constants.KEY_DEPARTMENT, doctor.getDepartmentInformation());
            startActivity(intent);
        });
    }

    private void displayConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutDialogRateBinding confirmationDialogBinding = LayoutDialogRateBinding.inflate(getLayoutInflater());
        builder.setView(confirmationDialogBinding.getRoot());
        confirmationDialogBinding.etComment.setText(binding.tvYourComment.getText().toString());
        final ImageView[] starImageViews = {
                confirmationDialogBinding.ivStar1,
                confirmationDialogBinding.ivStar2,
                confirmationDialogBinding.ivStar3,
                confirmationDialogBinding.ivStar4,
                confirmationDialogBinding.ivStar5};

        final int numStars = starImageViews.length;

        for (int i = 0; i < numStars; i++) {
            final int starPosition = i;
            starImageViews[i].setOnClickListener(v -> {
                selectedStar = starPosition + 1;
                for (int j = 0; j < numStars; j++) {
                    starImageViews[j].setImageResource(j < selectedStar ? R.mipmap.ic_gold_star : R.mipmap.ic_star_white);
                    Log.d("Star test", String.valueOf(selectedStar));
                }
            });
        }
        starImageViews[starNumbers - 1].performClick();
        alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        confirmationDialogBinding.tvTitle.setText(getString(R.string.rate));
        confirmationDialogBinding.tvPositiveAction.setOnClickListener(v -> {
            Call<Void> call = RetrofitClient.getAuthenticatedAppointmentService(this).rateDoctor(
                    selectedStar,
                    confirmationDialogBinding.etComment.getText().toString(),
                    doctor.getDoctorInformation().getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        starNumbers = selectedStar;
                        alertDialog.dismiss();
                        binding.tvYourComment.setText(confirmationDialogBinding.etComment.getText().toString());
                        for (int i = 0; i < starImageRootViews.length; i++) {
                            starImageRootViews[i].setVisibility(selectedStar >= (5 - i) ? View.VISIBLE : View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                }
            });
        });
        confirmationDialogBinding.ivClose.setOnClickListener(v -> {
            alertDialog.dismiss();
            alertDialog = null;
        });
        confirmationDialogBinding.tvNegativeAction.setOnClickListener(v -> {
            alertDialog.dismiss();
            alertDialog = null;
        });
        alertDialog.show();
    }
}