package com.example.clinic_appointment.models.Schedule;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ScheduleResponse implements Serializable {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private List<DetailSchedule> schedules;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<DetailSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<DetailSchedule> schedules) {
        this.schedules = schedules;
    }
}
