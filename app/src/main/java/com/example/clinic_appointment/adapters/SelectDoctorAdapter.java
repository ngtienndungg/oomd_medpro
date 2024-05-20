package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainerSelectDoctorBinding;
import com.example.clinic_appointment.listeners.DoctorListener;
import com.example.clinic_appointment.models.Doctor.Doctor;

import java.util.List;

public class SelectDoctorAdapter extends RecyclerView.Adapter<SelectDoctorAdapter.ItemViewHolder> {
    private final DoctorListener listener;
    private final List<Doctor> doctors;

    public SelectDoctorAdapter(DoctorListener listener, List<Doctor> doctors) {
        this.listener = listener;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectDoctorAdapter.ItemViewHolder(ItemContainerSelectDoctorBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(doctors.get(position));
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(doctors.get(holder.getBindingAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSelectDoctorBinding binding;

        public ItemViewHolder(@NonNull ItemContainerSelectDoctorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Doctor doctor) {
            String fullDoctorName;
            if (doctor.getAcademicLevel() == null || doctor.getAcademicLevel().length() == 0) {
                fullDoctorName = "Bác sĩ ".toUpperCase() + doctor.getDoctorInformation().getFullName();
            } else {
                fullDoctorName = doctor.getAcademicLevel() + " " + doctor.getDoctorInformation().getFullName();
            }
            binding.tvName.setText(fullDoctorName);
            binding.tvGender.setText(doctor.getDoctorInformation().getGenderVietnamese());
            binding.tvDepartment.setText(doctor.getDepartmentInformation().getName());
            binding.tvCalendar.setText(doctor.getScheduleString());
        }
    }
}
