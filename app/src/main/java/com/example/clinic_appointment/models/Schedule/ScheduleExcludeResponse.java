package com.example.clinic_appointment.models.Schedule;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScheduleExcludeResponse {
    @SerializedName("success")
    private boolean isSuccess;
    @SerializedName("data")
    private List<ScheduleExclude> schedules;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<ScheduleExclude> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleExclude> schedules) {
        this.schedules = schedules;
    }
}
