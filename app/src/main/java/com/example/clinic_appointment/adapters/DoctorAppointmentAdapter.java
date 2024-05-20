package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainerDoctorAppointmentBinding;
import com.example.clinic_appointment.listeners.AppointmentListener;
import com.example.clinic_appointment.models.Appointment.Appointment;
import com.example.clinic_appointment.utilities.CustomConverter;

import java.util.List;

public class DoctorAppointmentAdapter extends RecyclerView.Adapter<DoctorAppointmentAdapter.ItemViewHolder> {
    private final AppointmentListener listener;
    private final List<Appointment> appointments;

    public DoctorAppointmentAdapter(AppointmentListener listener, List<Appointment> appointments) {
        this.listener = listener;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(ItemContainerDoctorAppointmentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(appointments.get(position));
        holder.binding.llViewDetail.setOnClickListener(v -> listener.onClick(appointments.get(holder.getBindingAdapterPosition())));
        holder.binding.llInformation.setOnClickListener(v -> listener.onClick(appointments.get(holder.getBindingAdapterPosition())));
        holder.binding.ivAccept.setOnClickListener(v -> {
            listener.onAcceptClick(appointments.get(holder.getBindingAdapterPosition()), holder.getBindingAdapterPosition());
            holder.binding.pbLoadingAccept.setVisibility(View.VISIBLE);
        });
        holder.binding.ivDeny.setOnClickListener(v -> {
            listener.onDenyClick(appointments.get(holder.getBindingAdapterPosition()), holder.getBindingAdapterPosition());
            holder.binding.pbLoadingDeny.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerDoctorAppointmentBinding binding;

        public ItemViewHolder(ItemContainerDoctorAppointmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Appointment appointment) {
            binding.tvHealthFacility.setText(appointment.getSchedule().getDoctor().getHealthFacility().getName());
            binding.tvName.setText(appointment.getPatient().getFullName());
            binding.tvCode.setText(appointment.getId().toUpperCase());
            binding.tvPhoneNumber.setText(appointment.getPatient().getPhoneNumber());
            binding.tvDate.setText(CustomConverter.getFormattedDate(appointment.getSchedule().getDate()));
            binding.tvTime.setText(CustomConverter.getStringAppointmentTime(appointment.getAppointmentTime()));
            binding.tvStatus.setText(appointment.getStatus());
            if (appointment.getStatus().equals("Đang xử lý")) {
                binding.rlResponse.setVisibility(View.VISIBLE);
            } else {
                binding.rlResponse.setVisibility(View.GONE);
            }
        }
    }
}
