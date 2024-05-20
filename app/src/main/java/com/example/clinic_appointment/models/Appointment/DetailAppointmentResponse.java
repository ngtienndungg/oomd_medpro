package com.example.clinic_appointment.models.Appointment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DetailAppointmentResponse implements Serializable {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private DetailAppointment appointment;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public DetailAppointment getAppointment() {
        return appointment;
    }

    public void setAppointment(DetailAppointment appointment) {
        this.appointment = appointment;
    }
}
