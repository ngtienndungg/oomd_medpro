package com.example.clinic_appointment.models.Appointment;

import com.example.clinic_appointment.models.Schedule.DetailSchedule;
import com.example.clinic_appointment.models.User.User;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DetailAppointment implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("patientID")
    private User patientInformation;
    @SerializedName("status")
    private String status;
    @SerializedName("scheduleID")
    private DetailSchedule schedule;
    @SerializedName("time")
    private String appointmentTime;

    public User getPatientInformation() {
        return patientInformation;
    }

    public void setPatientInformation(User patientInformation) {
        this.patientInformation = patientInformation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DetailSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(DetailSchedule schedule) {
        this.schedule = schedule;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
