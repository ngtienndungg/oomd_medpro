package com.example.clinic_appointment.utilities;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.clinic_appointment.databinding.LayoutCalendarDayBinding;
import com.kizitonwose.calendar.view.ViewContainer;

public class DayViewContainer extends ViewContainer {
    public TextView textView;

    public DayViewContainer(@NonNull View view) {
        super(view);
        this.textView = LayoutCalendarDayBinding.bind(view).calendarDayText;
    }
}