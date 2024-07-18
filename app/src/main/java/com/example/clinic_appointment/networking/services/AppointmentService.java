package com.example.clinic_appointment.networking.services;

import com.example.clinic_appointment.models.Address.VietnamProvinceResponse;
import com.example.clinic_appointment.models.Appointment.AppointmentResponse;
import com.example.clinic_appointment.models.Appointment.DetailAppointmentResponse;
import com.example.clinic_appointment.models.Department.DepartmentResponse;
import com.example.clinic_appointment.models.Doctor.DoctorResponse;
import com.example.clinic_appointment.models.Doctor.DoctorSingleResponse;
import com.example.clinic_appointment.models.HealthFacility.HealthFacilitiesResponse;
import com.example.clinic_appointment.models.HealthFacility.HealthFacilityResponse;
import com.example.clinic_appointment.models.PatientProfile.PatientProfileResponse;
import com.example.clinic_appointment.models.Record.Record;
import com.example.clinic_appointment.models.Schedule.ScheduleExcludeResponse;
import com.example.clinic_appointment.models.User.UserResponse;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppointmentService {
    @POST("user/login")
    @FormUrlEncoded
    Call<UserResponse> login(@Field("email") String email, @Field("password") String password);

    @POST("user/logout")
    Call<Void> logout(@Header("Set-Cookie") String refreshToken);

    @POST("user/refreshtoken")
    Call<JsonObject> refreshToken(@Header("Set-Cookie") String refreshToken);

    @GET("user/current")
    Call<UserResponse> getCurrent(@Header("Authorization") String accessToken);

    @PUT("user/current")
    @FormUrlEncoded
    Call<UserResponse> updateUser(@Field("fullName") String fullName,
                                  @Field("email") String email,
                                  @Field("gender") String gender,
                                  @Field("address") String address);

    @POST("user/register")
    @FormUrlEncoded
    Call<UserResponse> register(@Field("email") String email, @Field("password") String password,
                                @Field("fullName") String fullName, @Field("mobile") String phoneNumber,
                                @Field("address") String address, @Field("gender") String gender,
                                @Field("dateOfBirth") String dateOfBirth);

    @GET("clinic")
    Call<HealthFacilitiesResponse> getAllHealthFacilities();

    @GET("patient")
    Call<PatientProfileResponse> getAllPatientProfiles();

    @POST("patient")
    @FormUrlEncoded
    Call<Void> addPatientProfile(@Field("fullName") String email, @Field("phone") String phoneNumber,
                                 @Field("gender") String gender, @Field("dob") Long date);

    @PUT("patient/{id}")
    @FormUrlEncoded
    Call<Void> updatePatientProfile(@Path("id") String id, @Field("fullName") String fullName,
                                    @Field("phone") String phoneNumber, @Field("gender") String gender,
                                    @Field("dob") Long date);

    @DELETE("patient/{id}")
    Call<Void> deletePatientProfile(@Path("id") String id);

    @GET("clinic/{id}")
    Call<HealthFacilityResponse> getHealthFacilityById(@Path("id") String clinicId);

    @GET("doctor/{id}")
    Call<DoctorSingleResponse> getDoctorById(@Path("id") String clinicId);

    @GET("specialty")
    Call<DepartmentResponse> getEntireDepartment();

    @GET("specialty")
    Call<DepartmentResponse> getFilteredDepartment();

    @GET("doctor")
    Call<DoctorResponse> getDoctorByDepartmentAndHealthFacility(@Query("specialtyID") String departmentId,
                                                                @Query("clinicID") String clinicId,
                                                                @Query("fullName") String name);

    @GET("schedule")
    Call<ScheduleExcludeResponse> getSchedules(@Query("startDate") Long startDate,
                                               @Query("endDate") Long endDate,
                                               @Query("timeType.time") String time,
                                               @Query(encoded = true, value = "nameSpecialty") String departmentName,
                                               @Query(encoded = true, value = "nameClinic") String clinicName,
                                               @Query("doctorID") String doctorId,
                                               @Query("fields") String exclude);

    @POST("booking/patient")
    Call<Void> bookAppointmentByPatient(@Body RequestBody requestBody);

    @GET("booking")
    Call<AppointmentResponse> getEntireAppointment();

    @GET("province")
    Call<VietnamProvinceResponse> getEntireProvinces();

    @GET("booking/{id}")
    Call<DetailAppointmentResponse> getAppointmentById(@Path("id") String appointmentId);

    @PUT("clinic/rating")
    @FormUrlEncoded
    Call<Void> rateClinic(@Field("star") int star, @Field("comment") String comment, @Field("clinicID") String clinicId);

    @PUT("doctor/rating")
    @FormUrlEncoded
    Call<Void> rateDoctor(@Field("star") int star, @Field("comment") String comment, @Field("doctorID") String doctorId);

    @PUT("booking/patient/{id}")
    @FormUrlEncoded
    Call<Void> cancelAppointment(@Path("id") String appointmentId, @Field("patientID") String patientId);

    @GET("record")
    Call<Record> getRecordByBookingId(@Query("bookingID") String bookingID);

    @PUT("booking/{id}")
    @FormUrlEncoded
    Call<Void> updateBooking(@Path("id") String id, @Field("status") String status, @Field("description") String description, @Field("isPaid") Boolean isPaid);
}
