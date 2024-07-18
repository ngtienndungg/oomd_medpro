package com.example.clinic_appointment.listeners;

import com.example.clinic_appointment.models.message.User;

public interface ChatListener {
    void onRecentConversationClicked(User user);
}
