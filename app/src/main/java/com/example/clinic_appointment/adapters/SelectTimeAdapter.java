package com.example.clinic_appointment.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinic_appointment.databinding.ItemContainerAppointmentTimeAvailableBinding;
import com.example.clinic_appointment.databinding.ItemContainerAppointmentTimeFullBinding;
import com.example.clinic_appointment.listeners.AppointmentTimeListener;
import com.example.clinic_appointment.models.AppointmentTime.AppointmentTime;
import com.example.clinic_appointment.utilities.CustomConverter;

import java.util.List;
import java.util.Map;

public class SelectTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_AVAILABLE = 1;
    public static final int VIEW_TYPE_FULL = 0;
    public static Map<String, String> timeMap;
    private final List<AppointmentTime> appointmentTimes;
    private final AppointmentTimeListener listener;

    public SelectTimeAdapter(List<AppointmentTime> appointmentTimes, AppointmentTimeListener listener) {
        this.appointmentTimes = appointmentTimes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_AVAILABLE) {
            return new AvailableViewHolder(ItemContainerAppointmentTimeAvailableBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        } else {
            return new FullViewHolder(ItemContainerAppointmentTimeFullBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_AVAILABLE) {
            ((AvailableViewHolder) holder).setData(appointmentTimes.get(position));
            ((AvailableViewHolder) holder).binding.getRoot().setOnClickListener(v -> listener.onClick(appointmentTimes.get(holder.getBindingAdapterPosition())));
        } else {
            ((FullViewHolder) holder).setData(appointmentTimes.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!appointmentTimes.get(position).isFull()) {
            return VIEW_TYPE_AVAILABLE;
        } else {
            return VIEW_TYPE_FULL;
        }
    }

    @Override
    public int getItemCount() {
        return appointmentTimes.size();
    }

    public static class AvailableViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerAppointmentTimeAvailableBinding binding;

        public AvailableViewHolder(ItemContainerAppointmentTimeAvailableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(AppointmentTime appointmentTime) {
            binding.tvTime.setText(CustomConverter.getStringAppointmentTime(appointmentTime.getTimeNumber()));
        }
    }

    public static class FullViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerAppointmentTimeFullBinding binding;

        public FullViewHolder(ItemContainerAppointmentTimeFullBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setData(AppointmentTime appointmentTime) {
            binding.tvTime.setText(CustomConverter.getStringAppointmentTime(appointmentTime.getTimeNumber()));
        }
    }
}
