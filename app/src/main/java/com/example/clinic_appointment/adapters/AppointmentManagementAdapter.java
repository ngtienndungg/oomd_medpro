package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainerAppoinmentBinding;
import com.example.clinic_appointment.listeners.AppointmentListener;
import com.example.clinic_appointment.models.Appointment.Appointment;
import com.example.clinic_appointment.utilities.Constants;
import com.example.clinic_appointment.utilities.CustomConverter;

import java.util.List;

public class AppointmentManagementAdapter extends RecyclerView.Adapter<AppointmentManagementAdapter.ItemViewHolder> {
    private final AppointmentListener listener;
    private final List<Appointment> appointments;

    public AppointmentManagementAdapter(AppointmentListener listener, List<Appointment> appointments) {
        this.listener = listener;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemContainerAppoinmentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(appointments.get(position));
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(appointments.get(holder.getBindingAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerAppoinmentBinding binding;

        public ItemViewHolder(ItemContainerAppoinmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Appointment appointment) {
            binding.tvHealthFacility.setText(appointment.getSchedule().getDoctor().getHealthFacility().getName());
            binding.tvDepartment.setText(appointment.getSchedule().getDoctor().getDepartmentInformation().getName());
            binding.tvCode.setText(appointment.getId().toUpperCase());
            binding.tvDoctor.setText(appointment.getSchedule().getDoctor().getDoctorInformation().getFullName());
            binding.tvPatientName.setText(appointment.getPatient().getFullName());
            binding.tvDate.setText(CustomConverter.getFormattedDate(appointment.getSchedule().getDate()));
            binding.tvTime.setText(CustomConverter.getStringAppointmentTime(appointment.getAppointmentTime()));
            if (appointment.getStatus().equals(Constants.KEY_STATUS_PENDING)) {
                appointment.setStatus("Đang chờ xác nhận");
            }
            if (appointment.getStatus().equals(Constants.KEY_STATUS_CONFIRMED)) {
                appointment.setStatus("Đã duyệt");
            }
            if (appointment.getStatus().equals(Constants.KEY_STATUS_CANCELLED)) {
                appointment.setStatus("Đã hủy");
            }
            if (appointment.getStatus().equals(Constants.KEY_STATUS_EXAMINED)) {
                appointment.setStatus("Đã khám");
            }
            binding.tvStatus.setText(appointment.getStatus());
        }
    }
}
