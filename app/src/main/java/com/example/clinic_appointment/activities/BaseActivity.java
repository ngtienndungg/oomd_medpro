package com.example.clinic_appointment.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinic_appointment.utilities.Constants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document("cHSLta2KI8Q6DukIgUsK1Z3E1Jy2");
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constants.KEY_USER_AVAILABILITY, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_USER_AVAILABILITY, false);
    }


}
