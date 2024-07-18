package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainerPatientProfileBinding;
import com.example.clinic_appointment.listeners.PatientProfileListener;
import com.example.clinic_appointment.models.PatientProfile.PatientProfile;
import com.example.clinic_appointment.utilities.CustomConverter;

import java.util.List;

public class PatientProfileAdapter extends RecyclerView.Adapter<PatientProfileAdapter.ItemViewHolder> {
    private final PatientProfileListener listener;
    private final List<PatientProfile> patientProfiles;

    public PatientProfileAdapter(PatientProfileListener listener, List<PatientProfile> patientProfiles) {
        this.listener = listener;
        this.patientProfiles = patientProfiles;
    }

    @NonNull
    @Override
    public PatientProfileAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PatientProfileAdapter.ItemViewHolder(ItemContainerPatientProfileBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull PatientProfileAdapter.ItemViewHolder holder, int position) {
        holder.setData(patientProfiles.get(position));
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(patientProfiles.get(holder.getBindingAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return patientProfiles.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerPatientProfileBinding binding;

        public ItemViewHolder(@NonNull ItemContainerPatientProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(PatientProfile patientProfile) {
            binding.tvName.setText(patientProfile.getFullName());
            binding.tvDateOfBirth.setText(CustomConverter.getFormattedDate(patientProfile.getDateOfBirth()));
            binding.tvGender.setText(patientProfile.getGenderVietnamese());
            binding.tvPhoneNumber.setText(patientProfile.getPhoneNumber());
        }
    }
}
