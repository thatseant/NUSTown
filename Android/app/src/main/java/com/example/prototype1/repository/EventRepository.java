package com.example.prototype1.repository;


import com.example.prototype1.model.Filters;
import com.example.prototype1.model.NClub;
import com.example.prototype1.model.NEvent;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class EventRepository {

    public EventRepository() {
    }

    public void search(final MyCallback myCallback, Filters filters) {
        ArrayList<NEvent> mResults = new ArrayList<>();

        Query query = FirebaseFirestore.getInstance().collection("events");

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
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    NEvent newEvent = document.toObject(NEvent.class); //Converts document to NEvent object
                    newEvent.setID(document.getId());
                    mResults.add(newEvent); //Adds to list of NEvent objects that matches query
                }
                myCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });
    }

    public void searchClubs(final MyClubsCallback myClubsCallback) {
        ArrayList<NClub> mResults = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("clubs").get().addOnCompleteListener(task -> { //Performs query
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    NClub newClub = document.toObject(NClub.class); //Converts document to NClub object
                    mResults.add(newClub); //Adds to list of NClub objects that matches query
                }
                myClubsCallback.onCallback(mResults); //Callback required as Firebase query performed asynchronously; code after onCompleteListener will execute before it finishes
            }
        });
    }

    public void getAll(final MyCallback myCallback) { //Returns all documents in Events collection
        search(myCallback, new Filters());
    }

    public void getAllClubs(final MyClubsCallback myClubsCallback) { //Returns all documents in Events collection
        searchClubs(myClubsCallback);
    }

    public void updateEvent(NEvent updatedEvent) {
        FirebaseFirestore.getInstance().collection("events").document(updatedEvent.getID()).set(updatedEvent);
    }

    public interface MyCallback {
        void onCallback(ArrayList<NEvent> eventList);
    }

    public interface MyClubsCallback {
        void onCallback(ArrayList<NClub> clubList);
    }

}


