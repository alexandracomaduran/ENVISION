package com.aac.envision;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyApplication extends Application {

    private FirebaseFirestore firestore;


    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize Firebase
        FirebaseApp.initializeApp(this);

        //Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }
}
