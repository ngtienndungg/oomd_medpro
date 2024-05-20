package com.example.clinic_appointment.networking.interceptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.clinic_appointment.activities.LoginActivity;
import com.example.clinic_appointment.databinding.LayoutDialogNotificationBinding;
import com.example.clinic_appointment.models.User.UserResponse;
import com.example.clinic_appointment.networking.clients.RetrofitClient;
import com.example.clinic_appointment.networking.services.AppointmentService;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.SharedPrefs;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrentSessionInterceptor implements Interceptor {
    private Context context;
    private AlertDialog alertDialog;
    private SharedPrefs sharedPrefs = SharedPrefs.getInstance();

    public CurrentSessionInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (isAccessTokenExpired()) {
            getNewAccessToken();
        }
        String accessToken = sharedPrefs.getData(Constants.KEY_ACCESS_TOKEN, String.class);
        return chain.proceed(chain.request().newBuilder()
                .header(Constants.HEADER_AUTHORIZATION, accessToken)
                .build());
    }

    private boolean isAccessTokenExpired() throws IOException {
        boolean isExpired = false;
        retrofit2.Response<UserResponse> response = new Retrofit.Builder()
                .baseUrl(RetrofitClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AppointmentService.class)
                .getCurrent(SharedPrefs.getInstance().getData(Constants.KEY_ACCESS_TOKEN, String.class))
                .execute();
        if (response.code() == 401) {
            isExpired = true;
        }
        return isExpired;
    }

    private void getNewAccessToken() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC);
        Call<JsonObject> call = new Retrofit.Builder()
                .baseUrl(RetrofitClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build())
                .build()
                .create(AppointmentService.class)
                .refreshToken(SharedPrefs.getInstance().getData(Constants.KEY_REFRESH_TOKEN, String.class));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    String newAccessToken = jsonResponse.get(Constants.KEY_NEW_ACCESS_TOKEN).getAsString();
                    sharedPrefs.putData(Constants.KEY_ACCESS_TOKEN, newAccessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                sharedPrefs.clear();
                displayDialog();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutDialogNotificationBinding dialogNotificationBinding = LayoutDialogNotificationBinding.inflate(LayoutInflater.from(context));
        builder.setView(dialogNotificationBinding.getRoot());
        alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogNotificationBinding.tvTitle.setText("Phiên bản đã hết bạn");
        dialogNotificationBinding.tvContent.setText("Vui lòng đăng nhập lại để sử dụng ứng dụng");
        dialogNotificationBinding.tvAction.setText("Đăng nhập");
        dialogNotificationBinding.tvAction.setOnClickListener(v -> context.startActivity(new Intent(context, LoginActivity.class)));
        dialogNotificationBinding.ivClose.setOnClickListener(v ->
        {
            alertDialog.dismiss();
            alertDialog = null;
        });
        alertDialog.show();
    }
}
