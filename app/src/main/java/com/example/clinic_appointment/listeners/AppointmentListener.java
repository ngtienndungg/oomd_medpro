package com.example.clinic_appointment.listeners;

import com.example.clinic_appointment.models.Appointment.Appointment;

public interface AppointmentListener {
    void onClick(Appointment appointment);

    void onAcceptClick(Appointment appointment, int position);

    void onDenyClick(Appointment appointment, int position);
}
