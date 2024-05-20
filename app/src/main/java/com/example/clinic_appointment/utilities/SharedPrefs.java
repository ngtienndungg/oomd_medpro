package com.example.clinic_appointment.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    private static final String PREFS_NAME = "shared_prefs";
    private static SharedPrefs mInstance;
    private final SharedPreferences mSharedPrefs;

    private SharedPrefs() {
        mSharedPrefs = App.getSelf().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefs getInstance() {
        if (mInstance == null) {
            mInstance = new SharedPrefs();
        }
        return mInstance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> valueClass) {
        if (valueClass == String.class) {
            return (T) mSharedPrefs.getString(key, "");
        } else if (valueClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPrefs.getBoolean(key, false));
        } else if (valueClass == Float.class) {
            return (T) Float.valueOf(mSharedPrefs.getFloat(key, 0));
        } else if (valueClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPrefs.getInt(key, 0));
        } else if (valueClass == Long.class) {
            return (T) Long.valueOf(mSharedPrefs.getLong(key, 0));
        } else {
            return (T) App.getSelf().getGSon().fromJson(mSharedPrefs.getString(key, ""), valueClass);
        }
    }

    public <T> void putData(String key, T data) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        if (data instanceof String) {
            editor.putString(key, (String) data);
        } else if (data instanceof Boolean) {
            editor.putBoolean(key, (Boolean) data);
        } else if (data instanceof Float) {
            editor.putFloat(key, (Float) data);
        } else if (data instanceof Integer) {
            editor.putInt(key, (Integer) data);
        } else if (data instanceof Long) {
            editor.putLong(key, (Long) data);
        } else {
            editor.putString(key, App.getSelf().getGSon().toJson(data));
        }
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> anonymousClass, T defaultValue) {
        if (anonymousClass == String.class) {
            return (T) mSharedPrefs.getString(key, (String) defaultValue);
        } else if (anonymousClass == Boolean.class) {
            return (T) Boolean.valueOf(mSharedPrefs.getBoolean(key, (Boolean) defaultValue));
        } else if (anonymousClass == Float.class) {
            return (T) Float.valueOf(mSharedPrefs.getFloat(key, (Float) defaultValue));
        } else if (anonymousClass == Integer.class) {
            return (T) Integer.valueOf(mSharedPrefs.getInt(key, (Integer) defaultValue));
        } else if (anonymousClass == Long.class) {
            return (T) Long.valueOf(mSharedPrefs.getLong(key, (Long) defaultValue));
        } else {
            return (T) App.getSelf()
                    .getGSon()
                    .fromJson(mSharedPrefs.getString(key, ""), anonymousClass);
        }
    }

    public void clear() {
        mSharedPrefs.edit().clear().apply();
    }
}
