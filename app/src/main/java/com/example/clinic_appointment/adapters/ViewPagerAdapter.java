package com.example.clinic_appointment.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clinic_appointment.fragments.AccountFragment;
import com.example.clinic_appointment.fragments.HomeFragment;
import com.example.clinic_appointment.fragments.MyScheduleFragment;
import com.example.clinic_appointment.fragments.NotificationFragment;
import com.example.clinic_appointment.fragments.PatientProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new PatientProfileFragment();
            case 2:
                return new MyScheduleFragment();
            case 3:
                return new NotificationFragment();
            default:
                return new AccountFragment();
        }
    }
}
