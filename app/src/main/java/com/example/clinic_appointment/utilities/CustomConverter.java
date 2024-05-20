package com.example.clinic_appointment.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomConverter {
    public static String getStringAppointmentTime(String timeNumber) {
        switch (timeNumber) {
            case "1":
                return "07:00 - 08:00";
            case "2":
                return "08:00 - 09:00";
            case "3":
                return "09:00 - 10:00";
            case "4":
                return "10:00 - 11:00";
            case "5":
                return "11:00 - 12:00";
            case "6":
                return "13:00 - 14:00";
            case "7":
                return "14:00 - 15:00";
            case "8":
                return "15:00 - 16:00";
            case "9":
                return "16:00 - 17:00";
            case "10":
                return "17:00 - 18:00";
            case "11":
                return "18:00 - 19:00";
            case "12":
                return "19:00 - 20:00";
            case "13":
                return "20:00 - 21:00";
            default:
                return "00:00 - 00:00";
        }
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd - MM - yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }
}
