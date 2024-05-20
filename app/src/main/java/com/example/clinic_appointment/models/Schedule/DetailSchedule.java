package com.example.clinic_appointment.models.Schedule;

import com.example.clinic_appointment.models.AppointmentTime.AppointmentTime;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DetailSchedule implements Serializable {
    @SerializedName("_id")
    private String scheduleId;
    @SerializedName("doctorID")
    private Doctor doctor;
    @SerializedName("date")
    private Date date;
    @SerializedName("cost")
    private long price;
    @SerializedName("timeType")
    private List<AppointmentTime> appointmentTimes;

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public List<AppointmentTime> getAppointmentTimes() {
        return appointmentTimes;
    }

    public void setAppointmentTimes(List<AppointmentTime> appointmentTimes) {
        this.appointmentTimes = appointmentTimes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}

