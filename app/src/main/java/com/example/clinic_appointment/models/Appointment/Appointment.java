package com.example.clinic_appointment.models.Appointment;

import com.example.clinic_appointment.models.Schedule.DetailSchedule;
import com.example.clinic_appointment.models.User.User;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Appointment implements Serializable {
    @SerializedName("_id")
    private String id;
    @SerializedName("patientID")
    private User patient;
    @SerializedName("status")
    private String status;
    @SerializedName("scheduleID")
    private DetailSchedule schedule;
    @SerializedName("time")
    private String appointmentTime;
    @SerializedName("descriptionImg")
    private List<String> images;
    @SerializedName("description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
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
