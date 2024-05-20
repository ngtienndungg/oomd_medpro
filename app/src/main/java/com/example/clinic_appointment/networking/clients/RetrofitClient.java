package com.example.clinic_appointment.networking.clients;

import android.content.Context;

import com.example.clinic_appointment.networking.interceptors.CurrentSessionInterceptor;
import com.example.clinic_appointment.networking.services.AppointmentService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://172.20.10.2:5000/api/";
    public static final String PROVINCE_API_BASE_URL = "https://vapi.vnappmob.com/api/";
    static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static Retrofit authenticatedRetrofit = null;
    private static Retrofit publicRetrofit = null;
    private static Retrofit addressRetrofit = null;

    public static AppointmentService getAuthenticatedAppointmentService(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new CurrentSessionInterceptor(context))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        if (authenticatedRetrofit == null) {
            authenticatedRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return authenticatedRetrofit.create(AppointmentService.class);
    }

    public static AppointmentService getPublicAppointmentService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        if (publicRetrofit == null) {
            publicRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return publicRetrofit.create(AppointmentService.class);
    }

    public static AppointmentService getProvinceApiService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        if (addressRetrofit == null) {
            addressRetrofit = new Retrofit.Builder()
                    .baseUrl(PROVINCE_API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return addressRetrofit.create(AppointmentService.class);
    }
}