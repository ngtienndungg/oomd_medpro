package com.example.clinic_appointment.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainerSelectDoctorBinding;
import com.example.clinic_appointment.listeners.ScheduleListener;
import com.example.clinic_appointment.models.Doctor.Doctor;
import com.example.clinic_appointment.models.Schedule.DetailSchedule;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ItemViewHolder> {
    private final ScheduleListener listener;
    private final List<DetailSchedule> schedules;

    public ScheduleAdapter(ScheduleListener listener, List<DetailSchedule> doctors) {
        this.listener = listener;
        this.schedules = doctors;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScheduleAdapter.ItemViewHolder(ItemContainerSelectDoctorBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(schedules.get(position));
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(schedules.get(holder.getBindingAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSelectDoctorBinding binding;

        public ItemViewHolder(@NonNull ItemContainerSelectDoctorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void setData(DetailSchedule detailSchedule) {
            Doctor doctor = detailSchedule.getDoctor();
            String fullDoctorName;
            if (doctor.getAcademicLevel() == null || doctor.getAcademicLevel().length() == 0) {
                fullDoctorName = "Bác sĩ ".toUpperCase() + detailSchedule.getDoctor().getDoctorInformation().getFullName();
            } else {
                fullDoctorName = doctor.getAcademicLevel() + " " + doctor.getDoctorInformation().getFullName();
            }
            binding.tvName.setText(fullDoctorName);
            binding.tvGender.setText(doctor.getDoctorInformation().getGenderVietnamese());
            binding.tvDepartment.setText(doctor.getDepartmentInformation().getName());
            binding.tvCalendarLabel.setText("Bệnh viện: ");
            binding.tvCalendar.setText(doctor.getHealthFacility().getName());
            binding.tvPrice.setText(detailSchedule.getPrice() + "đ");
        }
    }
}
