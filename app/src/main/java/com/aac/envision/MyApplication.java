package com.aac.envision;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyApplication extends Application {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;


    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize Firebase
        FirebaseApp.initializeApp(this);

        //Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        //Initialize Storage
        storage = FirebaseStorage.getInstance();

    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }
    public FirebaseStorage getStorage() {
        return storage;
    }
}
