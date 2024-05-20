package com.example.clinic_appointment.models.Appointment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentResponse {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private List<Appointment> appointment;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<Appointment> getBooking() {
        return appointment;
    }

    public void setBooking(List<Appointment> appointment) {
        this.appointment = appointment;
    }
}
