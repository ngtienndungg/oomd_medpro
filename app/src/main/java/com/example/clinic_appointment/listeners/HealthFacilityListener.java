package com.example.clinic_appointment.listeners;

import com.example.clinic_appointment.models.HealthFacility.HealthFacility;

public interface HealthFacilityListener {
    void onClick(HealthFacility healthFacility);

    void onProvinceSelect(String provinceName);
}
