package com.example.clinic_appointment.listeners;


import com.example.clinic_appointment.models.message.Message;

public interface MessageListener {
    void onHoldListener(Message message, int position);
}
