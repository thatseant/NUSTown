package com.example.prototype1.repository;



import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NEvent;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;


public class EventRepository {
    private static FirebaseFirestore db;

    public interface MyCallback {
        void onCallback(ArrayList<NEvent> eventList);
    }


    public EventRepository() {
        db = FirebaseFirestore.getInstance(); //Initialize Firebase database reference
    }


    public void search(final MyCallback myCallback, Filters filters) {
        ArrayList<NEvent> mResults = new ArrayList<>();

        Query query = db.collection("events");

        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }
        if (filters.hasPlace()) {
            query = query.whereEqualTo("place", filters.getPlace());
        }

        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        query.get().addOnCompleteListener(task -> { //Performs query
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    NEvent newEvent = document.toObject(NEvent.class); //Converts document to NEvent object
                    mResults.add(newEvent); //Adds to list of NEvent objects that matches query
                }
                myCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });
    }

    public void getAll(final MyCallback myCallback) { //Returns all documents in Events collection
        search(myCallback, new Filters());
    }

}


